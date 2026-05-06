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
    @FXML
    private Label weekLabel;
    @FXML
    private TableView<ITeam> standingsTable;
    @FXML
    private TableColumn<ITeam, String> teamNameCol;
    @FXML
    private TableColumn<ITeam, Integer> pointsCol;
    @FXML
    private Label selectedTeamNameLabel;
    @FXML
    private TableView<IPlayer> playersTable;
    @FXML
    private TableColumn<IPlayer, String> playerNameCol;
    @FXML
    private TableColumn<IPlayer, String> playerAttrCol;
    @FXML
    private ListView<String> coachesList;
    private League currentLeague;
    private ITeam myTeam;


    public void setLeagueAndTeam(League league, ITeam chosenTeam) {
        this.currentLeague = league;
        this.myTeam = chosenTeam;

        setupTables();
        updateUI();

        standingsTable.getSelectionModel().select(myTeam);
        showTeamDetails(myTeam);
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
        if (myTeam != null) {
            weekLabel.setText("Manager of: " + myTeam.getTeamName() + "  |  Current Week: " + currentLeague.getCurrentWeek());
        } else {
            weekLabel.setText("Current Week: " + currentLeague.getCurrentWeek());
        }

        standingsTable.setItems(FXCollections.observableArrayList(currentLeague.getStandings()));

        ITeam selected = standingsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            playersTable.refresh();
        }
    }

    private void showTeamDetails(ITeam team) {
        selectedTeamNameLabel.setText(team.getTeamName() + " Details");
        playersTable.setItems(FXCollections.observableArrayList(team.getRoster()));
        coachesList.getItems().clear();
        for (ICoach coach : team.getCoachingStaff()) {
            coachesList.getItems().add(coach.getName() + " - Specialties: " + coach.getSpecialties().toString());
        }
    }

    private java.util.List<com.team14.sportsmanager.core.IPlayer> myStarters = new java.util.ArrayList<>();
    private java.util.List<com.team14.sportsmanager.core.IPlayer> mySubs = new java.util.ArrayList<>();
    private String myTactic = "4-2-1 Balanced";

    @FXML
    protected void onNextWeekClick() {
        if (currentLeague.isLeagueFinished()) {
            weekLabel.setText("League is Finished!");
            return;
        }

        if (myStarters.size() != 7) {
            weekLabel.setText("ERROR: You must set exactly 7 starters in the Locker Room first!");
            return;
        }

        currentLeague.advanceWeek();
        for (com.team14.sportsmanager.core.ITeam team : currentLeague.getStandings()) {
            for (com.team14.sportsmanager.core.ICoach coach : team.getCoachingStaff()) {
                for (com.team14.sportsmanager.core.IPlayer player : team.getRoster()) {
                    coach.train(player);
                }
            }
        }

        updateUI();
        com.team14.sportsmanager.core.ITeam selected = standingsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showTeamDetails(selected);
        }
    }

    @FXML
    protected void onLockerRoomClick() {
        javafx.stage.Stage prepStage = new javafx.stage.Stage();
        prepStage.setTitle("Locker Room - Set Squad");

        javafx.scene.layout.VBox mainBox = new javafx.scene.layout.VBox(20);
        mainBox.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 30;");
        mainBox.setAlignment(javafx.geometry.Pos.CENTER);

        javafx.scene.control.Label title = new javafx.scene.control.Label("LOCKER ROOM - TACTICS");
        title.setTextFill(javafx.scene.paint.Color.WHITE);
        title.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 24));

        javafx.scene.control.ListView<String> rosterList = new javafx.scene.control.ListView<>();
        javafx.scene.control.ListView<String> startersList = new javafx.scene.control.ListView<>();
        javafx.scene.control.ListView<String> subsList = new javafx.scene.control.ListView<>();

        for (com.team14.sportsmanager.core.IPlayer p : myTeam.getRoster()) {
            boolean isStarter = myStarters.stream().anyMatch(s -> s.getName().equals(p.getName()));
            boolean isSub = mySubs.stream().anyMatch(s -> s.getName().equals(p.getName()));

            if (isStarter) {
                startersList.getItems().add(p.getName());
            } else if (isSub) {
                subsList.getItems().add(p.getName());
            } else {
                rosterList.getItems().add(p.getName());
            }
        }

        javafx.scene.control.Button btnStarter = new javafx.scene.control.Button("Add to Starters ->");
        btnStarter.setOnAction(e -> {
            String sel = rosterList.getSelectionModel().getSelectedItem();
            if (sel != null && startersList.getItems().size() < 7) {
                rosterList.getItems().remove(sel);
                startersList.getItems().add(sel);
            }
        });

        javafx.scene.control.Button btnSub = new javafx.scene.control.Button("Add to Subs ->");
        btnSub.setOnAction(e -> {
            String sel = rosterList.getSelectionModel().getSelectedItem();
            if (sel != null && subsList.getItems().size() < 3) {
                rosterList.getItems().remove(sel);
                subsList.getItems().add(sel);
            }
        });

        javafx.scene.control.Button btnRemoveStarter = new javafx.scene.control.Button("<- Remove Starter");
        btnRemoveStarter.setOnAction(e -> {
            String sel = startersList.getSelectionModel().getSelectedItem();
            if (sel != null) {
                startersList.getItems().remove(sel);
                rosterList.getItems().add(sel);
            }
        });

        javafx.scene.control.Button btnRemoveSub = new javafx.scene.control.Button("<- Remove Sub");
        btnRemoveSub.setOnAction(e -> {
            String sel = subsList.getSelectionModel().getSelectedItem();
            if (sel != null) {
                subsList.getItems().remove(sel);
                rosterList.getItems().add(sel);
            }
        });

        javafx.scene.layout.VBox leftBox = new javafx.scene.layout.VBox(5, new javafx.scene.control.Label("Available Roster"), rosterList);
        javafx.scene.layout.VBox midBox = new javafx.scene.layout.VBox(15, btnStarter, btnRemoveStarter, new javafx.scene.control.Label("---"), btnSub, btnRemoveSub);
        midBox.setAlignment(javafx.geometry.Pos.CENTER);
        javafx.scene.layout.VBox rightBox = new javafx.scene.layout.VBox(5, new javafx.scene.control.Label("Starting 7"), startersList, new javafx.scene.control.Label("Substitutes (Max 3)"), subsList);

        javafx.scene.layout.HBox listsHBox = new javafx.scene.layout.HBox(20, leftBox, midBox, rightBox);
        listsHBox.setAlignment(javafx.geometry.Pos.CENTER);

        javafx.scene.control.ComboBox<String> tacticBox = new javafx.scene.control.ComboBox<>();
        tacticBox.getItems().addAll("4-2-1 Balanced", "3-3-1 Offensive", "5-1-1 Defensive");
        tacticBox.setValue(myTactic); // Eski taktiği hatırla
        javafx.scene.layout.HBox tacticHBox = new javafx.scene.layout.HBox(10, new javafx.scene.control.Label("Select Tactic:"), tacticBox);
        tacticHBox.setAlignment(javafx.geometry.Pos.CENTER);


        javafx.scene.control.Button saveBtn = new javafx.scene.control.Button("SAVE SQUAD & TACTICS");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        saveBtn.setOnAction(e -> {
            if (startersList.getItems().size() < 7) {
                title.setText("ERROR: You must select exactly 7 starters!");
                title.setTextFill(javafx.scene.paint.Color.RED);
            } else {

                myStarters.clear();
                mySubs.clear();
                myTactic = tacticBox.getValue();

                for (com.team14.sportsmanager.core.IPlayer p : myTeam.getRoster()) {
                    if (startersList.getItems().contains(p.getName())) {
                        myStarters.add(p);
                    } else if (subsList.getItems().contains(p.getName())) {
                        mySubs.add(p);
                    }
                }


                weekLabel.setText("Squad saved! Ready for match.");
                prepStage.close();
            }
        });

        mainBox.getChildren().addAll(title, listsHBox, tacticHBox, saveBtn);
        javafx.scene.Scene prepScene = new javafx.scene.Scene(mainBox, 800, 600);
        prepScene.getRoot().setStyle("-fx-base: #2b2b2b;");

        prepStage.setScene(prepScene);
        prepStage.centerOnScreen();
        prepStage.showAndWait();
    }
}
