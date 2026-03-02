module com.facturo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.pdfbox;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.facturo to javafx.fxml;

    exports com.facturo;
    exports com.facturo.controller;
    exports com.facturo.model;

    opens com.facturo.controller to javafx.fxml;
    opens com.facturo.model to javafx.base;
}
