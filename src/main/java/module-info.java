module com.example.desktopapp440 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.desktopapp440 to javafx.fxml;
    exports com.example.desktopapp440;
    exports com.example.desktopapp440.controllers;
    opens com.example.desktopapp440.controllers to javafx.fxml;
    exports com.example.desktopapp440.objects;
    opens com.example.desktopapp440.objects to javafx.fxml;
}