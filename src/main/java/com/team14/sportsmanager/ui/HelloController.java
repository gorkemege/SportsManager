package com.team14.sportsmanager.ui;
import com.team14.sportsmanager.logic.League;
import com.team14.sportsmanager.logic.SportFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
public class HelloController {
    @FXML private ComboBox<String> sportSelectionBox;
    @FXML private Button startNewGameButton;
    @FXML
    public void initialize() {
        sportSelectionBox.getItems().add("HeadBall");
        sportSelectionBox.getSelectionModel().selectFirst();
    }
    @FXML
    protected void onStartNewGameClick(ActionEvent event) {
        String selectedSport = sportSelectionBox.getValue();
        League newLeague = SportFactory.createLeague(selectedSport);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setLeague(newLeague);
            Stage stage = (Stage) startNewGameButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}