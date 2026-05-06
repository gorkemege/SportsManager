package com.team14.sportsmanager.ui;
import com.team14.sportsmanager.core.ITeam;
import com.team14.sportsmanager.logic.League;
import com.team14.sportsmanager.logic.SportFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
        VBox teamSelectBox = new VBox(20);
        teamSelectBox.setAlignment(Pos.CENTER);
        teamSelectBox.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 50;");
        Label title = new Label("SELECT YOUR TEAM");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        Label subtitle = new Label("Choose the team you want to manage:");
        subtitle.setTextFill(Color.web("#dddddd"));
        subtitle.setFont(Font.font("System", 16));
        ListView<String> teamList = new ListView<>();
        for (ITeam t : newLeague.getStandings()) {
            teamList.getItems().add(t.getTeamName());
        }
        teamList.setPrefHeight(300);
        teamList.setPrefWidth(400);
        teamList.setStyle("-fx-font-size: 16px;");
        Button confirmBtn = new Button("Confirm Team & Start Game");
        confirmBtn.setPrefHeight(45.0);
        confirmBtn.setPrefWidth(300.0);
        confirmBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 5;");
        confirmBtn.setOnAction(e -> {
            String chosenName = teamList.getSelectionModel().getSelectedItem();
            if (chosenName != null) {
                ITeam myTeam = null;
                for (ITeam t : newLeague.getStandings()) {
                    if (t.getTeamName().equals(chosenName)) {
                        myTeam = t;
                        break;
                    }
                }
               
                Stage currentStage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
                goToDashboard(newLeague, myTeam, currentStage);
            }
        });
        teamSelectBox.getChildren().addAll(title, subtitle, teamList, confirmBtn);
        Scene teamScene = new Scene(teamSelectBox, 600, 550);
        Stage stage = (Stage) startNewGameButton.getScene().getWindow();
        stage.setScene(teamScene);
        stage.centerOnScreen();
    }

    private void goToDashboard(League league, ITeam myTeam, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setLeagueAndTeam(league, myTeam);
            stage.setScene(new Scene(root, 1000, 700));
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}