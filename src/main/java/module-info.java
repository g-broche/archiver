module com.gbroche.archiver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.gbroche.archiver to javafx.fxml;
    opens com.gbroche.archiver.controllers to javafx.fxml;
    exports com.gbroche.archiver;
}