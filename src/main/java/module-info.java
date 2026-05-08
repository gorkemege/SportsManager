module com.team14.sportsmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.team14.sportsmanager.ui to javafx.fxml;

    opens com.team14.sportsmanager.model to javafx.base;

    exports com.team14.sportsmanager.ui;
    exports com.team14.sportsmanager.core;
    exports com.team14.sportsmanager.logic;
    exports com.team14.sportsmanager.model;
}