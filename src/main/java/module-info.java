module com.example.desktopapp440 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.desktopapp440 to javafx.fxml;
    exports com.example.desktopapp440;
}