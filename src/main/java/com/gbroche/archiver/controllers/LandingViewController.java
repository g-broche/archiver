package com.gbroche.archiver.controllers;

import com.gbroche.archiver.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class LandingViewController {
    @FXML
    private Button archivePicker;
    @FXML
    private Button extractDestinationPicker;
    @FXML
    private Button extractButton;
    @FXML
    private CheckBox addSubDirCheckbox;
    @FXML
    private Label selectedArchivePathLabel;
    @FXML
    private TextField extractionPathField;
    @FXML
    private TextField archivePasswordField;

    private Stage stage;
    private boolean mustIncludeExtractionSubDir;
    private File selectedArchive;
    private File extractionDirectory;

    @FXML
    public void initialize() {
        extractButton.setDisable(true);
        addSubDirCheckbox.setSelected(true);
        mustIncludeExtractionSubDir = true;

        extractionPathField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Path path = Path.of(newValue).toAbsolutePath().normalize();
                extractionDirectory = path.toFile();
            } catch (InvalidPathException e) {
                new Alert(Alert.AlertType.INFORMATION, "invalid character entered").showAndWait();
            }
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
            extractionPathField.setText(extractionDirectory.getAbsolutePath());

            extractButton.setDisable(false);
        } else {
            System.out.println("no file selected");
        }
    }

    @FXML
    private void selectDestinationOfExtraction(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select directory to extract to");
        if (extractionDirectory != null) {
            directoryChooser.setInitialDirectory(extractionDirectory);
        }
        File dir = directoryChooser.showDialog(stage);
        if (dir != null) {
            extractionDirectory = dir;
            extractionPathField.setText(extractionDirectory.getAbsolutePath());
            extractButton.setDisable(false);
            System.out.println("extraction path: "+extractionDirectory.getAbsolutePath());

        } else {
            System.out.println("no extraction directory selected");
        }
    }

    @FXML
    private void toggleExtractionSubDir(){
        mustIncludeExtractionSubDir = addSubDirCheckbox.isSelected();
        String message = mustIncludeExtractionSubDir ? "including sub dir is true" : "including sub dir is false";
        System.out.println(message);
        appendSubDirIfRequired();
    }

    @FXML
    private void extractArchive(){
        new Alert(Alert.AlertType.INFORMATION, "extraction triggered").showAndWait();
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
            return;
        }
        String currentSelectedExtractionDir = extractionDirectory.getAbsolutePath();
        String archiveName = FileUtils.getFileNameWithoutExtension(selectedArchive);
        extractionDirectory = new File(currentSelectedExtractionDir, archiveName);
    }


    private void removeExtractionSubDir(){
        if (selectedArchive == null || extractionDirectory == null) {
            return;
        }
        boolean isArchiveInsideExtractionDirectory = FileUtils.isDirectoryDirectParentOfFile(extractionDirectory, selectedArchive);
        if (isArchiveInsideExtractionDirectory){
            extractionDirectory = selectedArchive.getParentFile();
            return;
        }

        // if (extractionDirectory)
    }

    private void appendSubDirIfRequired(){
        if (mustIncludeExtractionSubDir){
            addExtractionSubDir();
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Must implement removal of archive name").showAndWait();
        }
    }
}


