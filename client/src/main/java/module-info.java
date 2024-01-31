module org.ict.client {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires static lombok;
    requires javafx.media;
    requires de.jensd.fx.glyphs.fontawesome;

    opens org.ict.client to javafx.fxml;
    opens org.ict.client.controllers to javafx.fxml;
    exports org.ict.client;
    exports org.ict.client.models;
    exports org.ict.client.models.dtos;
    exports org.ict.client.controllers;
    exports org.ict.client.controllers.components;
    exports org.ict.client.controllers.studentpages;
    exports org.ict.client.controllers.teacherpages;
    opens org.ict.client.controllers.studentpages to javafx.fxml;
    opens org.ict.client.controllers.teacherpages to javafx.fxml;
    opens org.ict.client.controllers.components to javafx.fxml;
    opens org.ict.client.controllers.components.quiz to javafx.fxml;
}