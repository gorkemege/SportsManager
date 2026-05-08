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
import com.team14.sportsmanager.logic.SaveManager;
import com.team14.sportsmanager.model.Team;
public class HelloController {
    @FXML private ComboBox<String> sportSelectionBox;
    @FXML private Button startNewGameButton;
    @FXML
    public void initialize() {
        sportSelectionBox.getItems().add("Headball");
        sportSelectionBox.getItems().add("Handball");
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
        java.util.List<String> sortedTeams = new java.util.ArrayList<>();

        for (ITeam t : newLeague.getStandings()) {
            sortedTeams.add(t.getTeamName());
        }

        sortedTeams.sort((isim1, isim2) -> {
            try {
                int sayi1 = Integer.parseInt(isim1.replaceAll("[^0-9]", ""));
                int sayi2 = Integer.parseInt(isim2.replaceAll("[^0-9]", ""));
                return Integer.compare(sayi1, sayi2);
            } catch (Exception ex) {
                return isim1.compareTo(isim2);
            }
        });

        teamList.getItems().addAll(sortedTeams);
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

    @FXML
    protected void onLoadGameClick(ActionEvent event) {
        String selectedSport = sportSelectionBox.getValue();
        League loadedLeague = SaveManager.loadGame(selectedSport);

        if (loadedLeague == null || loadedLeague.getStandings().isEmpty()) {
            return;
        }

        String managerTeamName = SaveManager.loadManagerTeamName(selectedSport);

        ITeam loadedTeam = loadedLeague.getStandings().get(0);
        for (ITeam team : loadedLeague.getStandings()) {
            if (team.getTeamName().equals(managerTeamName)) {
                loadedTeam = team;
                break;
            }
        }

        String managerTactic = SaveManager.loadManagerTactic(selectedSport);

        if (loadedTeam instanceof Team && managerTactic != null) {
            ((Team) loadedTeam).setActiveTacticName(managerTactic);
        }

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        goToDashboard(loadedLeague, loadedTeam, stage, true, selectedSport);
    }

    private void goToDashboard(League league, ITeam myTeam, Stage stage) {
        goToDashboard(league, myTeam, stage, false, "");
    }

    private void goToDashboard(League league, ITeam myTeam, Stage stage, boolean restoreLineup, String selectedSport) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setLeagueAndTeam(league, myTeam);

            if (restoreLineup) {
                controller.restoreSavedLineup(
                        SaveManager.loadLineupPlayerNames("starter", selectedSport),
                        SaveManager.loadLineupPlayerNames("substitute", selectedSport)
                );
            }

            stage.setScene(new Scene(root, 1000, 700));
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}