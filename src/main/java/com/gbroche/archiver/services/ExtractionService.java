package com.gbroche.archiver.services;

import com.gbroche.archiver.classes.Command;
import com.gbroche.archiver.enums.ConflictStrategy;
import com.gbroche.archiver.utils.FileUtils;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Consumer;

public class ExtractionService {
    private static final List<String> allowedExtensions = List.of("zip", "7z", "rar");
    public static void extractArchive(
            File archive,
            File destination,
            String password,
            ConflictStrategy conflictStrategy,
            Consumer<String> onOutput,    // called for each line of CLI output
            Consumer<Boolean> onComplete  // called with true=success, false=failure
    ) {
        String extension = FileUtils.getFileExtension(archive);
        Command command = buildCommand(extension, archive, destination, password, conflictStrategy);

        if (command == null) {
            onOutput.accept("Unsupported archive format: " + extension);
            onComplete.accept(false);
            return;
        }
        // Run on a background thread to avoid freezing the UI
        Thread thread = new Thread(() -> {
            try {
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

            } catch (IOException | InterruptedException e) {
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
            String extension,
            File archive,
            File destination,
            String password,
            ConflictStrategy conflictStrategy
    ) {
        if(!allowedExtensions.contains(extension)){
            return null;
        }

        String archivePath = archive.getAbsolutePath();
        String destPath = destination.getAbsolutePath();
        boolean hasPassword = password != null && !password.isEmpty();
        Command.Builder cmd = new Command.Builder("7z").flag("x");
        cmd.flag(conflictStrategy.flag);
        if (hasPassword) cmd.flagConcat("-p", password);
        cmd.argument(archivePath);
        cmd.flagConcat("-o", destPath);
        return cmd.build();
    }
}
