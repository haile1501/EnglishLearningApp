module org.ict.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j;


    opens org.ict.client to javafx.fxml;
    exports org.ict.client;
}