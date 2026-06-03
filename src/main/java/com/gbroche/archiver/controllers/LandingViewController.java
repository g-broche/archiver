package com.gbroche.archiver.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;

public class LandingViewController {
    @FXML
    private Button archivePicker;
    @FXML
    private Button extractDestinationPicker;
    @FXML
    private Button extractButton;
    @FXML
    private Label selectedFilePath;
    @FXML
    private TextField extractionPathField;
    @FXML
    private TextField archivePasswordField;

    private File selectedArchive;
    private Stage stage;

    @FXML
    public void initialize() {
        extractButton.setDisable(true);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void selectArchive(){
        new Alert(Alert.AlertType.INFORMATION, "select archive triggered").showAndWait();
    }
    @FXML
    private void selectDestinationOfExtraction(){
        new Alert(Alert.AlertType.INFORMATION, "select destination triggered").showAndWait();
    }
    @FXML
    private void extractArchive(){
        new Alert(Alert.AlertType.INFORMATION, "extraction triggered").showAndWait();
    }
}
