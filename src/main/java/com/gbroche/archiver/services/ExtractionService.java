package com.gbroche.archiver.services;

import com.gbroche.archiver.classes.Command;
import com.gbroche.archiver.enums.ConflictStrategy;
import com.gbroche.archiver.enums.Extension;
import com.gbroche.archiver.utils.FileUtils;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Consumer;

public class ExtractionService {
    public static void extractArchive(
            File archive,
            File destination,
            String password,
            ConflictStrategy conflictStrategy,
            Consumer<String> onOutput,    // called for each line of CLI output
            Consumer<Boolean> onComplete,  // called with true=success, false=failure
            Consumer<Process> onProcessStarted  // expose process to allow cancellation
    ) {
        // Run on a background thread to avoid freezing the UI
        Thread thread = new Thread(() -> {
            try {
                Extension extension = FileUtils.getExtensionEnumFromFile(archive);
                Command command = buildCommand(extension, archive, destination, password, conflictStrategy);

                if (command == null) {
                    onOutput.accept("Unsupported archive format: " + extension);
                    onComplete.accept(false);
                    return;
                }
                boolean createdDestination = false;
                if (!destination.exists()) {
                    boolean created = destination.mkdirs();
                    if (!created) {
                        Platform.runLater(() -> onOutput.accept("Error: could not create destination directory"));
                        Platform.runLater(() -> onComplete.accept(false));
                        return;
                    }
                    createdDestination = true;
                }
                Platform.runLater(() -> onOutput.accept("Running command: " + String.join(" ", command.toList())));
                ProcessBuilder pb = new ProcessBuilder(command.toList());
                pb.redirectErrorStream(true);
                pb.directory(destination);
                Process process = pb.start();

                // Expose process to controller for cancel button
                final Process finalProcess = process;
                Platform.runLater(() -> onProcessStarted.accept(finalProcess));

                // Kill process if app closes while it's running
                final Process shutdownProcess = process;
                Thread shutdownHook = new Thread(() -> {
                    if (shutdownProcess.isAlive()) {
                        shutdownProcess.destroyForcibly();
                    }
                });
                Runtime.getRuntime().addShutdownHook(shutdownHook);

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String finalLine = line;
                        // Route output back to JavaFX thread for UI updates
                        Platform.runLater(() -> onOutput.accept(finalLine));
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    Platform.runLater(() -> onComplete.accept(true));
                } else {
                    if (createdDestination) {
                        FileUtils.deleteDirectory(destination);
                        Platform.runLater(() -> onOutput.accept("Cleaned up destination directory after failure."));
                    }
                    Platform.runLater(() -> onComplete.accept(false));
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    onOutput.accept("Error: " + e.getMessage());
                    onComplete.accept(false);
                });
            }
        });

        thread.setDaemon(true); // Don't prevent app from closing if thread is still running
        thread.start();
    }

    private static Command buildCommand(
            Extension extension,
            File archive,
            File destination,
            String password,
            ConflictStrategy conflictStrategy
    ) throws Exception {
        if(extension == null){
            throw new Exception("Invalid file format received ("+FileUtils.getFileExtension(archive)+")");
        }
        if(archive == null){
            throw new Exception("No archive file provided for extraction");
        }
        if(destination == null){
            throw new Exception("Directory for extraction is null");
        }

        String archivePath = archive.getAbsolutePath();
        String destPath = destination.getAbsolutePath();
        boolean hasPassword = password != null && !password.isEmpty();
        return switch (extension) {
            case Extension.RAR -> {
                Command.Builder cmd = new Command.Builder("unrar").flag("x");
                if(conflictStrategy.flagUnrar == null){
                    throw new Exception("Invalid conflict strategy resolution picked for .rar files");
                }
                cmd.flag(conflictStrategy.flagUnrar);
                if (hasPassword) {
                    cmd.flagConcat("-p", password);
                } else {
//                    cmd.flag("-p-"); // explicitly no password, fail instead of prompting in the void
                }
                cmd.argument(archivePath);
                cmd.argument(destPath + "/");
                yield cmd.build();
            }
            case Extension.ZIP, Extension.SEVEN_ZIP -> {
                Command.Builder cmd = new Command.Builder("7z").flag("x");
                cmd.flag(conflictStrategy.flagSevenZip);
                if (hasPassword) cmd.flagConcat("-p", password);
                cmd.argument(archivePath);
                cmd.flagConcat("-o", destPath);
                yield cmd.build();
            }
            default -> null;
        };
    }
}
