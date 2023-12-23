module com.example.Kurs {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.example.Kurs to javafx.fxml;
    exports com.example.Kurs;
    exports com.example.Kurs.controllers;

    opens com.example.Kurs.controllers to javafx.fxml;
    opens com.example.Kurs.models to  javafx.base;
    opens com.example.Kurs.models.dispatchers to javafx.base;
    opens com.example.Kurs.models.streams to javafx.base;

}