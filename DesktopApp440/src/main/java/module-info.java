module com.example.desktopapp440 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens edu.csun.desktopapp440 to javafx.fxml;
    exports edu.csun.desktopapp440;
    exports edu.csun.desktopapp440.controllers;
    opens edu.csun.desktopapp440.controllers to javafx.fxml;
    exports edu.csun.desktopapp440.objects;
    opens edu.csun.desktopapp440.objects to javafx.fxml;
}