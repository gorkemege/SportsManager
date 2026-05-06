package com.team14.sportsmanager.ui;
import com.team14.sportsmanager.logic.League;
import com.team14.sportsmanager.logic.SportFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
public class HelloController {
    @FXML
    private ComboBox<String> sportSelectionBox;
    @FXML
    private Button startNewGameButton;
    @FXML
    public void initialize() {
        sportSelectionBox.getItems().add("HeadBall");
        sportSelectionBox.getSelectionModel().selectFirst();
    }
    @FXML
    protected void onStartNewGameClick(ActionEvent event) {
        String selectedSport = sportSelectionBox.getValue();
        System.out.println("[SYSTEM] Selected Sport: " + selectedSport);
        League newLeague = SportFactory.createLeague(selectedSport);
        System.out.println("[SYSTEM] League successfully created! Total teams: " + newLeague.getStandings().size());
    }
}