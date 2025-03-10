module com.example.imageviewerjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires spring.web;
    requires tomcat.embed.core;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires org.json;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires spring.core;

    opens com.example.imageviewerjavafx to javafx.fxml;
    exports com.example.imageviewerjavafx;
    exports APICall;
    opens APICall to javafx.fxml;
}