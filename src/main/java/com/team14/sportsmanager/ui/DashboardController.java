package com.team14.sportsmanager.ui;
import com.team14.sportsmanager.core.ICoach;
import com.team14.sportsmanager.core.IPlayer;
import com.team14.sportsmanager.core.ITeam;
import com.team14.sportsmanager.logic.League;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
public class DashboardController {
    @FXML private Label weekLabel;
    @FXML private TableView<ITeam> standingsTable;
    @FXML private TableColumn<ITeam, String> teamNameCol;
    @FXML private TableColumn<ITeam, Integer> pointsCol;
    @FXML private Label selectedTeamNameLabel;
    @FXML private TableView<IPlayer> playersTable;
    @FXML private TableColumn<IPlayer, String> playerNameCol;
    @FXML private TableColumn<IPlayer, String> playerAttrCol;
    @FXML private ListView<String> coachesList;
    private League currentLeague;
    public void setLeague(League league) {
        this.currentLeague = league;
        setupTables();
        updateUI();
    }
    private void setupTables() {
        teamNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTeamName()));
        pointsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalPoints()).asObject());
        standingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showTeamDetails(newSelection);
            }
        });
        playerNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        playerAttrCol.setCellValueFactory(cellData -> {
            IPlayer player = cellData.getValue();
            return new SimpleStringProperty(player.getAttributes().toString());
        });
    }
    private void updateUI() {
        weekLabel.setText("Current Week: " + currentLeague.getCurrentWeek());
        standingsTable.setItems(FXCollections.observableArrayList(currentLeague.getStandings()));
    }
    private void showTeamDetails(ITeam team) {
        selectedTeamNameLabel.setText(team.getTeamName() + " Details");
        playersTable.setItems(FXCollections.observableArrayList(team.getRoster()));
        coachesList.getItems().clear();
        for (ICoach coach : team.getCoachingStaff()) {
            coachesList.getItems().add(coach.getName() + " - Specialties: " + coach.getSpecialties().toString());
        }
    }
    @FXML
    protected void onNextWeekClick() {
        if (!currentLeague.isLeagueFinished()) {
            currentLeague.advanceWeek();
            updateUI();
            ITeam selected = standingsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showTeamDetails(selected);
            }
        } else {
            weekLabel.setText("League is Finished!");
        }
    }
}