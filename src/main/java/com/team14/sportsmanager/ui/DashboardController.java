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
    private javafx.scene.control.ListView<String> fixtureList;
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
        playerNameCol.setPrefWidth(160);
        playerAttrCol.setPrefWidth(400);
        playersTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        playerAttrCol.setCellValueFactory(cellData -> {
            IPlayer player = cellData.getValue();
            String injuryStatus = player.isInjured() ? " INJURED (" + player.getRemainingInjuryDuration() + " games" : "";
            return new SimpleStringProperty(player.getAttributes().toString() + injuryStatus);
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
        if (fixtureList != null && !currentLeague.isLeagueFinished()) {
            fixtureList.getItems().clear();
            java.util.List<com.team14.sportsmanager.core.IMatch> weekMatches = currentLeague.getFixtures().get(currentLeague.getCurrentWeek());

            for (com.team14.sportsmanager.core.IMatch match : weekMatches) {
                com.team14.sportsmanager.logic.MatchEngine engine = (com.team14.sportsmanager.logic.MatchEngine) match;
                String home = engine.getTeam1().getTeamName();
                String away = engine.getTeam2().getTeamName();
                String matchText = home + " vs " + away;

                if (home.equals(myTeam.getTeamName()) || away.equals(myTeam.getTeamName())) {
                    matchText = ">>> " + matchText + " <<< (YOUR MATCH)";
                }
                fixtureList.getItems().add(matchText);
            }
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

        com.team14.sportsmanager.core.IMatch myMatch = null;
        com.team14.sportsmanager.core.ITeam opponent = null;

        java.util.List<com.team14.sportsmanager.core.IMatch> weekMatches = currentLeague.getFixtures().get(currentLeague.getCurrentWeek());

        for (com.team14.sportsmanager.core.IMatch match : weekMatches) {
            com.team14.sportsmanager.logic.MatchEngine engine = (com.team14.sportsmanager.logic.MatchEngine) match;
            if (engine.getTeam1().getTeamName().equals(myTeam.getTeamName())) {
                myMatch = engine;
                opponent = engine.getTeam2();
                break;
            } else if (engine.getTeam2().getTeamName().equals(myTeam.getTeamName())) {
                myMatch = engine;
                opponent = engine.getTeam1();
                break;
            }
        }

        openLiveMatchScreen(myMatch, opponent);
    }

    private void openLiveMatchScreen(com.team14.sportsmanager.core.IMatch matchObj, com.team14.sportsmanager.core.ITeam opponent) {
        com.team14.sportsmanager.logic.MatchEngine actualMatch = (com.team14.sportsmanager.logic.MatchEngine) matchObj;
        javafx.stage.Stage matchStage = new javafx.stage.Stage();
        matchStage.setTitle("LIVE MATCH");
        final int[] currentQuarter = {1};
        javafx.scene.layout.VBox mainBox = new javafx.scene.layout.VBox(30);
        mainBox.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 40;");
        mainBox.setAlignment(javafx.geometry.Pos.CENTER);
        javafx.scene.control.Label quarterLabel = new javafx.scene.control.Label("QUARTER 1");
        quarterLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
        quarterLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 24));
        javafx.scene.layout.HBox scoreBox = new javafx.scene.layout.HBox(40);
        scoreBox.setAlignment(javafx.geometry.Pos.CENTER);
        javafx.scene.control.Label myTeamLabel = new javafx.scene.control.Label(myTeam.getTeamName() + "\n0");
        myTeamLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        myTeamLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 32));
        myTeamLabel.setAlignment(javafx.geometry.Pos.CENTER);
        myTeamLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        javafx.scene.control.Label vsLabel = new javafx.scene.control.Label("-");
        vsLabel.setTextFill(javafx.scene.paint.Color.GRAY);
        vsLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 36));
        javafx.scene.control.Label oppTeamLabel = new javafx.scene.control.Label(opponent.getTeamName() + "\n0");
        oppTeamLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        oppTeamLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 32));
        oppTeamLabel.setAlignment(javafx.geometry.Pos.CENTER);
        oppTeamLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        scoreBox.getChildren().addAll(myTeamLabel, vsLabel, oppTeamLabel);
        javafx.scene.control.Button playQuarterBtn = new javafx.scene.control.Button("PLAY QUARTER 1");
        playQuarterBtn.setStyle("-fx-background-color: #e50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 10 20;");
        javafx.scene.control.Button subsBtn = new javafx.scene.control.Button("Make Substitutions / Tactics");
        subsBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        subsBtn.setOnAction(e -> openSubstitutionScreen(actualMatch));
        playQuarterBtn.setOnAction(e -> {
            if (!actualMatch.isMatchFinished()) {
                actualMatch.simulatePeriod();
                int myRealScore = actualMatch.getCurrentScore().get(myTeam);
                int oppRealScore = actualMatch.getCurrentScore().get(opponent);
                myTeamLabel.setText(myTeam.getTeamName() + "\n" + myRealScore);
                oppTeamLabel.setText(opponent.getTeamName() + "\n" + oppRealScore);
                currentQuarter[0]++;
                if (!actualMatch.isMatchFinished()) {
                    quarterLabel.setText("QUARTER " + currentQuarter[0]);
                    playQuarterBtn.setText("PLAY QUARTER " + currentQuarter[0]);
                } else {
                    if (myRealScore > oppRealScore) {
                        quarterLabel.setText("MATCH FINISHED! YOU WON!");
                        quarterLabel.setTextFill(javafx.scene.paint.Color.LIMEGREEN);
                    } else if (myRealScore < oppRealScore) {
                        quarterLabel.setText("MATCH FINISHED! YOU LOST!");
                        quarterLabel.setTextFill(javafx.scene.paint.Color.RED);
                    } else {
                        quarterLabel.setText("MATCH FINISHED! DRAW!");
                        quarterLabel.setTextFill(javafx.scene.paint.Color.YELLOW);
                    }
                    playQuarterBtn.setText("END MATCH & SIMULATE LEAGUE");
                    subsBtn.setDisable(true);
                }
            } else {
                matchStage.close();
                currentLeague.advanceWeekExcluding(actualMatch);
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
        });
        mainBox.getChildren().addAll(quarterLabel, scoreBox, playQuarterBtn, subsBtn);
        javafx.scene.Scene matchScene = new javafx.scene.Scene(mainBox, 700, 500);
        matchStage.setScene(matchScene);
        matchStage.centerOnScreen();
        matchStage.showAndWait();
    }
    private void openSubstitutionScreen(com.team14.sportsmanager.logic.MatchEngine actualMatch) {
        javafx.stage.Stage subStage = new javafx.stage.Stage();
        subStage.setTitle("Substitutions & Tactics");
        javafx.scene.layout.VBox mainBox = new javafx.scene.layout.VBox(20);
        mainBox.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 30;");
        mainBox.setAlignment(javafx.geometry.Pos.CENTER);
        javafx.scene.control.Label title = new javafx.scene.control.Label("MAKE SUBSTITUTIONS");
        title.setTextFill(javafx.scene.paint.Color.WHITE);
        title.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 20));

        javafx.scene.control.ListView<com.team14.sportsmanager.core.IPlayer> startersList = new javafx.scene.control.ListView<>();
        startersList.getItems().addAll(myStarters);
        startersList.setCellFactory(param -> new javafx.scene.control.ListCell<com.team14.sportsmanager.core.IPlayer>() {
            @Override
            protected void updateItem(com.team14.sportsmanager.core.IPlayer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " | Stats: " + item.getAttributes());
                }
            }
        });

        javafx.scene.control.ListView<com.team14.sportsmanager.core.IPlayer> subsList = new javafx.scene.control.ListView<>();
        subsList.getItems().addAll(mySubs);
        subsList.setCellFactory(param -> new javafx.scene.control.ListCell<com.team14.sportsmanager.core.IPlayer>() {
            @Override
            protected void updateItem(com.team14.sportsmanager.core.IPlayer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " | Stats: " + item.getAttributes());
                }
            }
        });

        javafx.scene.control.Button swapBtn = new javafx.scene.control.Button("< SWAP PLAYERS >");
        swapBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        swapBtn.setOnAction(e -> {
            com.team14.sportsmanager.core.IPlayer pStarter = startersList.getSelectionModel().getSelectedItem();
            com.team14.sportsmanager.core.IPlayer pSub = subsList.getSelectionModel().getSelectedItem();
            if (pStarter != null && pSub != null) {
                if (actualMatch != null) {
                    actualMatch.applyUserSubstitution(pStarter, pSub);
                }
                myStarters.remove(pStarter);
                mySubs.remove(pSub);
                myStarters.add(pSub);
                mySubs.add(pStarter);
                startersList.getItems().remove(pStarter);
                subsList.getItems().remove(pSub);
                startersList.getItems().add(pSub);
                subsList.getItems().add(pStarter);
            }
        });

        javafx.scene.control.ComboBox<String> tacticBox = new javafx.scene.control.ComboBox<>();
        tacticBox.getItems().addAll("4-2-1 Balanced", "3-3-1 Offensive", "5-1-1 Defensive");
        tacticBox.setValue(myTactic);
        javafx.scene.control.Button saveBtn = new javafx.scene.control.Button("CONFIRM CHANGES");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> {
            myTactic = tacticBox.getValue();
            subStage.close();
        });

        javafx.scene.layout.HBox listsBox = new javafx.scene.layout.HBox(20, new javafx.scene.layout.VBox(5, new javafx.scene.control.Label("On Pitch"), startersList), swapBtn, new javafx.scene.layout.VBox(5, new javafx.scene.control.Label("Bench"), subsList));
        listsBox.setAlignment(javafx.geometry.Pos.CENTER);
        mainBox.getChildren().addAll(title, listsBox, new javafx.scene.control.Label("Change Tactic:"), tacticBox, saveBtn);
        javafx.scene.Scene scene = new javafx.scene.Scene(mainBox, 850, 500);
        scene.getRoot().setStyle("-fx-base: #2b2b2b;");
        subStage.setScene(scene);
        subStage.centerOnScreen();
        subStage.showAndWait();
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
