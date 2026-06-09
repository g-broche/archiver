package com.gbroche.archiver.controllers;

import com.gbroche.archiver.enums.ConflictStrategy;
import com.gbroche.archiver.enums.Extension;
import com.gbroche.archiver.services.ExtractionService;
import com.gbroche.archiver.utils.FileUtils;
import com.gbroche.archiver.utils.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;

public class LandingViewController {
    @FXML
    private Button archivePicker;
    @FXML
    private Button extractDestinationPicker;
    @FXML
    private Button extractButton;
    @FXML
    private Button cancelButton;
    @FXML
    private CheckBox addSubDirCheckbox;
    @FXML
    private Label selectedArchivePathLabel;
    @FXML
    private TextField extractionPathField;
    @FXML
    private TextField archivePasswordField;
    @FXML
    private ComboBox<ConflictStrategy> conflictStrategyComboBox;
    @FXML
    private TextArea logArea;

    private Stage stage;
    private boolean mustIncludeExtractionSubDir;
    private File selectedArchive;
    private File extractionDirectory;
    private Process currentExtractionProcess;

    @FXML
    public void initialize() {
        extractButton.setDisable(true);
        addSubDirCheckbox.setSelected(true);
        mustIncludeExtractionSubDir = true;
        fillConflictStrategyBox();

        //maybe uncheck sub dir checkbox there to keep things sync ?
        extractionPathField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Path path = Path.of(newValue).toAbsolutePath().normalize();
                extractionDirectory = path.toFile();
            } catch (InvalidPathException e) {
                new Alert(Alert.AlertType.INFORMATION, "invalid character entered").showAndWait();
            }
        });

        logArea.textProperty().addListener((obs, oldText, newText) -> {
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void selectArchive(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select archive to extract");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Archives", "*.zip", "*.rar", "*.7z"),
                new FileChooser.ExtensionFilter("ZIP files", "*.zip"),
                new FileChooser.ExtensionFilter("RAR files", "*.rar"),
                new FileChooser.ExtensionFilter("7z files", "*.7z")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            selectedArchive = file;
            selectedArchivePathLabel.setText(file.getAbsolutePath());
            extractionDirectory = mustIncludeExtractionSubDir
                ? new File(selectedArchive.getParentFile(), FileUtils.getFileNameWithoutExtension(selectedArchive))
                : selectedArchive.getParentFile();
            updateExtractionDirectoryLabel();
            fillConflictStrategyBox();
            extractButton.setDisable(false);
            return;
        }

        if (selectedArchive == null){
            extractButton.setDisable(true);
        }
    }

    @FXML
    private void selectDestinationOfExtraction(){
        DirectoryChooser directoryChooser = prepareDirectoryChooser();
        File dir = directoryChooser.showDialog(stage);
        if (dir != null) {
            extractionDirectory = dir;
            extractionPathField.setText(extractionDirectory.getAbsolutePath());
            extractButton.setDisable(false);
            return;
        }
        if (extractionDirectory.getAbsolutePath().isEmpty()){
            extractButton.setDisable(true);
        }
    }

    @FXML
    private void toggleExtractionSubDir(){
        mustIncludeExtractionSubDir = addSubDirCheckbox.isSelected();
        appendSubDirIfRequired();
    }

    @FXML
    private void extractArchive() {
        extractButton.setDisable(true);
        cancelButton.setDisable(false);  // added
        ExtractionService.extractArchive(
                selectedArchive,
                extractionDirectory,
                archivePasswordField.getText(),
                conflictStrategyComboBox.getValue(),
                message -> logArea.appendText(message + "\n"),
                success -> {
                    extractButton.setDisable(false);
                    cancelButton.setDisable(true);  // added
                    currentExtractionProcess = null;  // added
                    if (success) {
                        logArea.appendText("Extraction complete.\n");
                    } else {
                        logArea.appendText("Extraction failed.\n");
                    }
                },
                process -> currentExtractionProcess = process  // added
        );
    }

    @FXML
    private void cancelTask() {
        if (currentExtractionProcess != null && currentExtractionProcess.isAlive()) {
            currentExtractionProcess.destroyForcibly();
            logArea.appendText("Extraction cancelled.\n");
            extractButton.setDisable(false);
            cancelButton.setDisable(true);
            currentExtractionProcess = null;
        }
    }

    private void addExtractionSubDir(){
        if (selectedArchive == null || extractionDirectory == null) {
            return;
        }
        boolean isArchiveInsideExtractionDirectory = FileUtils.isDirectoryDirectParentOfFile(extractionDirectory, selectedArchive);
        if (isArchiveInsideExtractionDirectory){
            File parentFile = selectedArchive.getParentFile();
            String archiveName = FileUtils.getFileNameWithoutExtension(selectedArchive);
            extractionDirectory = new File(parentFile, archiveName);
            updateExtractionDirectoryLabel();
            return;
        }
        String currentSelectedExtractionDir = extractionDirectory.getAbsolutePath();
        String archiveName = FileUtils.getFileNameWithoutExtension(selectedArchive);
        extractionDirectory = new File(currentSelectedExtractionDir, archiveName);
        updateExtractionDirectoryLabel();
    }


    private void removeExtractionSubDir(){
        if (selectedArchive == null || extractionDirectory == null) {
            return;
        }
        String archiveName = FileUtils.getFileNameWithoutExtension(selectedArchive);
        boolean isExtractionDirectoryEndingWithFileName = FileUtils.doesDirectoryEndWithSegment(extractionDirectory, archiveName);
        if (isExtractionDirectoryEndingWithFileName){
            extractionDirectory = extractionDirectory.getParentFile();
            updateExtractionDirectoryLabel();
        }
    }

    private void appendSubDirIfRequired(){
        if (mustIncludeExtractionSubDir){
            addExtractionSubDir();
        } else {
            removeExtractionSubDir();
        }
    }

    private void updateExtractionDirectoryLabel(){
        extractionPathField.setText(extractionDirectory.getAbsolutePath());
    }


    private DirectoryChooser prepareDirectoryChooser(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select directory to extract to");
        // possibly add recursive parent look up there for non-existing extractionDirectory
        Logger.log("current extraction directory : "+extractionDirectory.getAbsolutePath());
        Logger.log(
                extractionDirectory.exists() ? "directory exists" : "directory doesn't exist"
        );
        boolean isValidExtractionDirSet = extractionDirectory != null && extractionDirectory.exists();
        if(!isValidExtractionDirSet && selectedArchive == null) {
            return directoryChooser;
        }
        if(!isValidExtractionDirSet){
            directoryChooser.setInitialDirectory(selectedArchive.getParentFile());
            return directoryChooser;
        }
        directoryChooser.setInitialDirectory(extractionDirectory);
        return directoryChooser;
    }

    private void fillConflictStrategyBox(){
        Extension extension = FileUtils.getExtensionEnumFromFile(selectedArchive);
        if (extension == null) extension = Extension.RAR; // default to rar if invalid enum as it is the less restrictive
        conflictStrategyComboBox.getItems().clear();
        conflictStrategyComboBox.getItems().addAll(extension.availableConflictStrategies);
        conflictStrategyComboBox.setValue(
                extension.availableConflictStrategies.contains(ConflictStrategy.SKIP)
                        ? ConflictStrategy.SKIP
                        : extension.availableConflictStrategies.getFirst()
        );
    }
}


