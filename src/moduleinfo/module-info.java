module HW3 {
    requires javafx.controls;
    requires java.sql;
    
    
    requires static org.junit.jupiter.api;
    requires static org.junit.platform.engine;
    requires static org.junit.platform.commons;
    requires static org.junit.jupiter.params;
    
    opens application to javafx.graphics, javafx.fxml;
    opens test to org.junit.jupiter.api, org.junit.platform.commons, org.junit.jupiter.params;
}