module com.team14.sportsmanager {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.team14.sportsmanager.ui to javafx.fxml;
    exports com.team14.sportsmanager.ui;
    exports com.team14.sportsmanager.core;
}