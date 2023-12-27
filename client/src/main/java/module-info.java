module org.ict.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires lombok;


    opens org.ict.client to javafx.fxml;
    opens org.ict.client.controllers to javafx.fxml;
    exports org.ict.client;
    exports org.ict.client.models;
    exports org.ict.client.models.dtos;
}