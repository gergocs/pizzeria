module com.pizzeria.pizzeria {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.java;
    requires commons.codec;
    requires org.apache.commons.io;
    requires java.sql.rowset;

    opens com.pizzeria to javafx.fxml;
    exports com.pizzeria;
}