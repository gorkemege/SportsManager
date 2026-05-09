package com.team14.sportsmanager.ui;

import com.team14.sportsmanager.core.ICoach;
import com.team14.sportsmanager.core.IPlayer;
import com.team14.sportsmanager.core.ITeam;
import com.team14.sportsmanager.logic.League;
import com.team14.sportsmanager.logic.SaveManager;
import com.team14.sportsmanager.model.Team;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardController {

    @FXML private Label weekLabel;
    @FXML private Label managerLabel;

    @FXML private TableView<ITeam> standingsTable;
    @FXML private TableColumn<ITeam, Integer> posCol;
    @FXML private TableColumn<ITeam, String>  teamNameCol;
    @FXML private TableColumn<ITeam, Integer> winsCol;
    @FXML private TableColumn<ITeam, Integer> drawsCol;
    @FXML private TableColumn<ITeam, Integer> lossesCol;
    @FXML private TableColumn<ITeam, Integer> gdCol;
    @FXML private TableColumn<ITeam, Integer> pointsCol;

    @FXML private Label fixtureHeader;
    @FXML private ListView<String> fixtureList;

    @FXML private Label selectedTeamNameLabel;
    @FXML private TableView<IPlayer> playersTable;
    @FXML private TableColumn<IPlayer, String>  playerNameCol;
    @FXML private TableColumn<IPlayer, String>  playerAttrCol;
    @FXML private TableColumn<IPlayer, Integer> playerTotalCol;
    @FXML private TableColumn<IPlayer, String>  playerStatusCol;
    @FXML private ListView<String> coachesList;

    @FXML private Label squadStatusLabel;
    @FXML private ListView<String> squadList;

    @FXML private Label tacticLabel;

    private League currentLeague;
    private ITeam myTeam;
    private String sportType = "Headball";
    private java.util.List<IPlayer> myStarters = new java.util.ArrayList<>();
    private java.util.List<IPlayer> mySubs     = new java.util.ArrayList<>();
    private String myTactic = "4-2-1 Balanced";
    private int currentSlot = 1;


    public void setLeagueAndTeam(League league, ITeam chosenTeam) {
        this.currentLeague = league;
        this.myTeam        = chosenTeam;
        this.sportType     = detectSportType();
        this.myTactic      = (chosenTeam instanceof Team && ((Team) chosenTeam).getActiveTacticName() != null)
                ? ((Team) chosenTeam).getActiveTacticName()
                : getDefaultTactic();
        setupTables();
        updateUI();
        standingsTable.getSelectionModel().select(myTeam);
        showTeamDetails(myTeam);
    }

    public void restoreSavedLineup(java.util.List<String> starterNames,
                                   java.util.List<String> substituteNames) {
        myStarters.clear();
        mySubs.clear();
        if (starterNames != null) {
            for (String name : starterNames)
                for (IPlayer p : myTeam.getRoster())
                    if (p.getName().equals(name) && !p.isInjured() && myStarters.size() < 7) {
                        myStarters.add(p); break;
                    }
        }
        if (substituteNames != null) {
            for (String name : substituteNames)
                for (IPlayer p : myTeam.getRoster())
                    if (p.getName().equals(name) && !p.isInjured() && mySubs.size() < 3) {
                        mySubs.add(p); break;
                    }
        }
        updateSquadPanel();
    }



    private void setupTables() {
        posCol.setCellValueFactory(cell -> {
            java.util.List<ITeam> sorted = getSortedStandings();
            return new SimpleIntegerProperty(sorted.indexOf(cell.getValue()) + 1).asObject();
        });
        teamNameCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTeamName()));
        winsCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getWins()).asObject());
        drawsCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getDraws()).asObject());
        lossesCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getLosses()).asObject());
        gdCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getGoalDifference()).asObject());
        pointsCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getTotalPoints()).asObject());

        standingsTable.setRowFactory(tv -> new TableRow<ITeam>() {
            @Override
            protected void updateItem(ITeam item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && myTeam != null
                        && item.getTeamName().equals(myTeam.getTeamName())) {
                    setStyle("-fx-background-color: #0f2d1a;");
                } else {
                    setStyle("");
                }
            }
        });

        standingsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, neu) -> { if (neu != null) showTeamDetails(neu); });

        playerNameCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getName()));
        playerAttrCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAttributes().toString()));
        playerTotalCol.setCellValueFactory(cell -> {
            int total = cell.getValue().getAttributes().values()
                    .stream().mapToInt(Integer::intValue).sum();
            return new SimpleIntegerProperty(total).asObject();
        });
        playerStatusCol.setCellValueFactory(cell -> {
            IPlayer p = cell.getValue();
            return new SimpleStringProperty(
                    p.isInjured() ? "INJ " + p.getRemainingInjuryDuration() + "w" : "OK");
        });
        playerStatusCol.setCellFactory(col -> new TableCell<IPlayer, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.startsWith("INJ")
                        ? "-fx-text-fill: #f85149; -fx-font-weight: bold;"
                        : "-fx-text-fill: #3fb950; -fx-font-weight: bold;");
            }
        });

        playersTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }


    private void updateUI() {
        weekLabel.setText("Week " + currentLeague.getCurrentWeek()
                + " / " + currentLeague.getFixtures().size());
        managerLabel.setText("Managing: " + (myTeam != null ? myTeam.getTeamName() : "—"));

        standingsTable.setItems(FXCollections.observableArrayList(getSortedStandings()));

        fixtureList.getItems().clear();
        if (!currentLeague.isLeagueFinished()) {
            fixtureHeader.setText("WEEK " + (currentLeague.getCurrentWeek() + 1) + " FIXTURES");
            for (com.team14.sportsmanager.core.IMatch match
                    : currentLeague.getFixtures().get(currentLeague.getCurrentWeek())) {
                com.team14.sportsmanager.logic.MatchEngine e =
                        (com.team14.sportsmanager.logic.MatchEngine) match;
                String home = e.getTeam1().getTeamName();
                String away = e.getTeam2().getTeamName();
                boolean isMyMatch = myTeam != null &&
                        (home.equals(myTeam.getTeamName()) || away.equals(myTeam.getTeamName()));
                fixtureList.getItems().add(
                        (isMyMatch ? "▶ " : "   ") + home + "  vs  " + away
                                + (isMyMatch ? "  ◀ YOUR MATCH" : ""));
            }
        } else {
            fixtureHeader.setText("SEASON COMPLETE");
        }

        tacticLabel.setText("Tactic: " + myTactic);

        updateSquadPanel();

        ITeam selected = standingsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showTeamDetails(selected);
            playersTable.refresh();
        } else if (myTeam != null) {
            showTeamDetails(myTeam);
            playersTable.refresh();
        }
    }

    private void updateSquadPanel() {
        squadStatusLabel.setText(
                "Starters: " + myStarters.size() + "/7   |   Subs: " + mySubs.size() + "/3");
        squadList.getItems().clear();

        if (!myStarters.isEmpty()) {
            squadList.getItems().add("── STARTING 7 ──");
            for (IPlayer p : myStarters) {
                int total = p.getAttributes().values().stream().mapToInt(Integer::intValue).sum();
                squadList.getItems().add("  ● " + p.getName() + "  (" + total + ")");
            }
        }
        if (!mySubs.isEmpty()) {
            squadList.getItems().add("── SUBSTITUTES ──");
            for (IPlayer p : mySubs) {
                int total = p.getAttributes().values().stream().mapToInt(Integer::intValue).sum();
                squadList.getItems().add("  ○ " + p.getName() + "  (" + total + ")");
            }
        }
        boolean anyInjured = false;
        for (IPlayer p : myTeam.getRoster()) {
            if (p.isInjured()) {
                if (!anyInjured) {
                    squadList.getItems().add("── INJURED ──");
                    anyInjured = true;
                }
                squadList.getItems().add("  ✕ " + p.getName()
                        + "  (" + p.getRemainingInjuryDuration() + "w)");
            }
        }
    }

    private void showTeamDetails(ITeam team) {
        int gd = team.getGoalDifference();
        selectedTeamNameLabel.setText(
                team.getTeamName() + "   —   " + team.getTotalPoints() + " pts"
                        + "   GD " + (gd >= 0 ? "+" : "") + gd);
        playersTable.setItems(FXCollections.observableArrayList(team.getRoster()));
        coachesList.getItems().clear();
        for (ICoach c : team.getCoachingStaff())
            coachesList.getItems().add("  " + c.getName() + "  —  " + c.getSpecialties());
    }

    private java.util.List<ITeam> getSortedStandings() {
        java.util.List<ITeam> sorted = new java.util.ArrayList<>(currentLeague.getStandings());
        sorted.sort((a, b) -> {
            if (b.getTotalPoints() != a.getTotalPoints())
                return Integer.compare(b.getTotalPoints(), a.getTotalPoints());
            return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
        });
        return sorted;
    }


    @FXML
    protected void onNextWeekClick() {
        if (currentLeague.isLeagueFinished()) { showChampionWindow(); return; }
        if (!handleInjuredStarters()) return;
        if (myStarters.size() != 7) {
            weekLabel.setText("ERROR: Set exactly 7 starters in Locker Room first!");
            return;
        }

        com.team14.sportsmanager.core.IMatch myMatch = null;
        com.team14.sportsmanager.core.ITeam  opponent = null;
        for (com.team14.sportsmanager.core.IMatch m
                : currentLeague.getFixtures().get(currentLeague.getCurrentWeek())) {
            com.team14.sportsmanager.logic.MatchEngine eng =
                    (com.team14.sportsmanager.logic.MatchEngine) m;
            if (eng.getTeam1().getTeamName().equals(myTeam.getTeamName())) {
                myMatch = eng; opponent = eng.getTeam2(); break;
            } else if (eng.getTeam2().getTeamName().equals(myTeam.getTeamName())) {
                myMatch = eng; opponent = eng.getTeam1(); break;
            }
        }

        if (myMatch == null) { currentLeague.advanceWeek(); updateUI(); }
        else openLiveMatchScreen(myMatch, opponent);
    }


    @FXML
    protected void onSimulateWeeksClick() {
        if (currentLeague.isLeagueFinished()) { showChampionWindow(); return; }

        int remaining = currentLeague.getFixtures().size() - currentLeague.getCurrentWeek();
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Simulate Weeks");
        dialog.setHeaderText("Remaining weeks: " + remaining);
        dialog.setContentText("How many weeks?");
        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        try {
            int count = Integer.parseInt(result.get());
            if (count > remaining) {
                new Alert(Alert.AlertType.ERROR, "Only " + remaining + " weeks left.").showAndWait();
                return;
            }
            for (int i = 0; i < count; i++) {
                if (currentLeague.isLeagueFinished()) break;
                autoFillLineupIfNeeded();
                if (myStarters.size() != 7) break;

                com.team14.sportsmanager.core.IMatch myMatch = null;
                for (com.team14.sportsmanager.core.IMatch m
                        : currentLeague.getFixtures().get(currentLeague.getCurrentWeek())) {
                    com.team14.sportsmanager.logic.MatchEngine eng =
                            (com.team14.sportsmanager.logic.MatchEngine) m;
                    if (eng.getTeam1().getTeamName().equals(myTeam.getTeamName())
                            || eng.getTeam2().getTeamName().equals(myTeam.getTeamName())) {
                        myMatch = eng; break;
                    }
                }
                if (myMatch != null) {
                    com.team14.sportsmanager.logic.MatchEngine eng =
                            (com.team14.sportsmanager.logic.MatchEngine) myMatch;
                    eng.setLineup(myTeam, new java.util.ArrayList<>(myStarters));
                    eng.simulateMatch();
                    currentLeague.advanceWeekExcluding(eng);
                } else {
                    currentLeague.advanceWeek();
                }
                for (ITeam t : currentLeague.getStandings())
                    for (ICoach c : t.getCoachingStaff())
                        for (IPlayer p : t.getRoster()) c.train(p);
            }
            updateUI();
            if (currentLeague.isLeagueFinished()) showChampionWindow();
        } catch (NumberFormatException e) {
            weekLabel.setText("Invalid number.");
        }
    }

    private void autoFillLineupIfNeeded() {
        java.util.List<com.team14.sportsmanager.core.IPlayer> healthyPlayers = new java.util.ArrayList<>();
        for (com.team14.sportsmanager.core.IPlayer p : myTeam.getRoster()) {
            if (!p.isInjured()) {
                healthyPlayers.add(p);
            }
        }

        healthyPlayers.sort((p1, p2) -> {
            int total1 = p1.getAttributes().values().stream().mapToInt(Integer::intValue).sum();
            int total2 = p2.getAttributes().values().stream().mapToInt(Integer::intValue).sum();
            return Integer.compare(total2, total1);
        });
        myStarters.clear();
        mySubs.clear();
        for (int i = 0; i < healthyPlayers.size(); i++) {
            if (i < 7) {
                myStarters.add(healthyPlayers.get(i));
            } else if (i < 10) {
                mySubs.add(healthyPlayers.get(i));
            } else {
                break;
            }
        }
    }


    private void openLiveMatchScreen(com.team14.sportsmanager.core.IMatch matchObj,
                                     com.team14.sportsmanager.core.ITeam opponent) {
        com.team14.sportsmanager.logic.MatchEngine actualMatch =
                (com.team14.sportsmanager.logic.MatchEngine) matchObj;
        actualMatch.setLineup(myTeam, new java.util.ArrayList<>(myStarters));

        boolean isHandball = "Handball".equalsIgnoreCase(sportType);
        String periodWord  = isHandball ? "HALF" : "QUARTER";
        int totalPeriods   = isHandball ? 2 : 4;

        javafx.stage.Stage matchStage = new javafx.stage.Stage();
        matchStage.setTitle("LIVE MATCH");

        final int[] currentPeriod = {1};

        javafx.scene.layout.VBox mainBox = new javafx.scene.layout.VBox(20);
        mainBox.setStyle("-fx-background-color: #0d1117; -fx-padding: 30;");
        mainBox.setAlignment(javafx.geometry.Pos.CENTER);

        Label periodLabel = new Label(periodWord + " 1 of " + totalPeriods);
        periodLabel.setTextFill(Color.ORANGE);
        periodLabel.setFont(Font.font("System", FontWeight.BOLD, 22));

        javafx.scene.layout.HBox scoreBox = new javafx.scene.layout.HBox(40);
        scoreBox.setAlignment(javafx.geometry.Pos.CENTER);
        scoreBox.setStyle("-fx-background-color: #161b22; -fx-padding: 16 30; -fx-background-radius: 10;");

        Label myTeamLbl = new Label(myTeam.getTeamName() + "\n0");
        myTeamLbl.setTextFill(Color.WHITE);
        myTeamLbl.setFont(Font.font("System", FontWeight.BOLD, 28));
        myTeamLbl.setAlignment(javafx.geometry.Pos.CENTER);
        myTeamLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label vsLbl = new Label("VS");
        vsLbl.setTextFill(Color.web("#8b949e"));
        vsLbl.setFont(Font.font("System", FontWeight.BOLD, 20));

        Label oppLbl = new Label(opponent.getTeamName() + "\n0");
        oppLbl.setTextFill(Color.web("#8b949e"));
        oppLbl.setFont(Font.font("System", FontWeight.BOLD, 28));
        oppLbl.setAlignment(javafx.geometry.Pos.CENTER);
        oppLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        scoreBox.getChildren().addAll(myTeamLbl, vsLbl, oppLbl);

        ListView<String> commentBox = new ListView<>();
        commentBox.setPrefHeight(200);
        commentBox.setStyle("-fx-background-color: #161b22; -fx-border-color: #21262d; -fx-border-radius: 8;");
        commentBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle("-fx-background-color: transparent;"); return; }
                setText(item);
                String low = item.toLowerCase(java.util.Locale.ENGLISH);
                if (low.contains("period") || low.contains("half") || low.contains("quarter"))
                    setStyle("-fx-text-fill: #d29922; -fx-font-weight: bold; -fx-background-color: transparent;");
                else if (item.contains("⚽") && item.contains(myTeam.getTeamName()))
                    setStyle("-fx-text-fill: #3fb950; -fx-font-weight: bold; -fx-background-color: transparent;");
                else if (item.contains("⚽"))
                    setStyle("-fx-text-fill: #f85149; -fx-font-weight: bold; -fx-background-color: transparent;");
                else if (item.contains("🚑"))
                    setStyle("-fx-text-fill: #f85149; -fx-background-color: transparent;");
                else if (item.contains("🔄"))
                    setStyle("-fx-text-fill: #388bfd; -fx-background-color: transparent;");
                else
                    setStyle("-fx-text-fill: #c9d1d9; -fx-background-color: transparent;");
            }
        });

        Button playBtn = new Button("PLAY " + periodWord + " 1");
        playBtn.setStyle("-fx-background-color: #1f6feb; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 30; -fx-background-radius: 8;");

        Button subsBtn = new Button("Substitutions / Tactics");
        subsBtn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-font-weight: bold; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 18;");
        subsBtn.setOnAction(e -> openSubstitutionScreen(actualMatch));

        playBtn.setOnAction(e -> {
            if (!actualMatch.isMatchFinished()) {
                actualMatch.simulatePeriod();
                int myScore  = actualMatch.getCurrentScore().get(myTeam);
                int oppScore = actualMatch.getCurrentScore().get(opponent);
                myTeamLbl.setText(myTeam.getTeamName() + "\n" + myScore);
                oppLbl.setText(opponent.getTeamName() + "\n" + oppScore);

                commentBox.getItems().clear();
                java.util.List<String> events = actualMatch.getMatchEvents();
                int from = 0;
                for (int i = events.size() - 1; i >= 0; i--)
                    if (events.get(i).startsWith("Period")) { from = i; break; }
                for (int i = from; i < events.size(); i++) {
                    String ev = events.get(i);
                    if (ev.startsWith("Period"))        commentBox.getItems().add("♦ " + ev.toUpperCase(java.util.Locale.ENGLISH));
                    else if (ev.contains("scored"))     commentBox.getItems().add("⚽ " + ev);
                    else if (ev.toLowerCase().contains("injur")) commentBox.getItems().add("🚑 " + ev);
                    else if (ev.contains("Substitution")) commentBox.getItems().add("🔄 " + ev);
                    else                                commentBox.getItems().add("» " + ev);
                }

                currentPeriod[0]++;
                if (!actualMatch.isMatchFinished()) {
                    periodLabel.setText(periodWord + " " + currentPeriod[0] + " of " + totalPeriods);
                    playBtn.setText("PLAY " + periodWord + " " + currentPeriod[0]);
                } else {
                    if (myScore > oppScore) {
                        periodLabel.setText("MATCH FINISHED — YOU WON!");
                        periodLabel.setTextFill(Color.LIMEGREEN);
                    } else if (myScore < oppScore) {
                        periodLabel.setText("MATCH FINISHED — YOU LOST");
                        periodLabel.setTextFill(Color.web("#f85149"));
                    } else {
                        periodLabel.setText("MATCH FINISHED — DRAW");
                        periodLabel.setTextFill(Color.YELLOW);
                    }
                    playBtn.setText("CONTINUE →");
                    subsBtn.setDisable(true);
                }
            } else {
                matchStage.close();
                currentLeague.advanceWeekExcluding(actualMatch);
                for (ITeam t : currentLeague.getStandings())
                    for (ICoach c : t.getCoachingStaff())
                        for (IPlayer p : t.getRoster()) c.train(p);
                updateUI();
                ITeam sel = standingsTable.getSelectionModel().getSelectedItem();
                if (sel != null) showTeamDetails(sel);
                handleInjuredStarters();
            }
        });

        javafx.scene.layout.HBox btnRow = new javafx.scene.layout.HBox(12, subsBtn, playBtn);
        btnRow.setAlignment(javafx.geometry.Pos.CENTER);

        mainBox.getChildren().addAll(periodLabel, scoreBox, commentBox, btnRow);
        javafx.scene.Scene scene = new javafx.scene.Scene(mainBox, 720, 520);
        matchStage.setScene(scene);
        matchStage.centerOnScreen();
        matchStage.showAndWait();
    }


    private void openSubstitutionScreen(com.team14.sportsmanager.logic.MatchEngine match) {
        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.setTitle("Substitutions & Tactics");

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(16);
        box.setStyle("-fx-background-color: #0d1117; -fx-padding: 24;");
        box.setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label("MAKE A SUBSTITUTION");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        ListView<IPlayer> startersLv = new ListView<>();
        startersLv.getItems().addAll(myStarters);
        startersLv.setCellFactory(lv -> playerCell());

        ListView<IPlayer> subsLv = new ListView<>();
        subsLv.getItems().addAll(mySubs);
        subsLv.setCellFactory(lv -> playerCell());

        Button swapBtn = new Button("⇄  SWAP");
        swapBtn.setStyle("-fx-background-color: #d29922; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6;");
        swapBtn.setOnAction(e -> {
            IPlayer s = startersLv.getSelectionModel().getSelectedItem();
            IPlayer b = subsLv.getSelectionModel().getSelectedItem();
            if (s != null && b != null) {
                if (match != null) { match.applyUserSubstitution(s, b); match.setLineup(myTeam, myStarters); }
                myStarters.remove(s); mySubs.remove(b);
                myStarters.add(b);   mySubs.add(s);
                startersLv.getItems().remove(s); subsLv.getItems().remove(b);
                startersLv.getItems().add(b);    subsLv.getItems().add(s);
            }
        });

        javafx.scene.layout.HBox lists = new javafx.scene.layout.HBox(16,
                vboxLabeled("On Pitch", startersLv), swapBtn, vboxLabeled("Bench", subsLv));
        lists.setAlignment(javafx.geometry.Pos.CENTER);

        ComboBox<String> tacticBox = new ComboBox<>();
        tacticBox.getItems().addAll(getAvailableTactics());
        tacticBox.setValue(myTactic);

        Button confirmBtn = new Button("CONFIRM");
        confirmBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 24; -fx-background-radius: 6;");
        confirmBtn.setOnAction(e -> {
            myTactic = tacticBox.getValue();
            if (myTeam instanceof Team) ((Team) myTeam).setActiveTacticName(myTactic);
            tacticLabel.setText("Tactic: " + myTactic);
            stage.close();
        });

        box.getChildren().addAll(title, lists,
                new Label("Tactic:"), tacticBox, confirmBtn);
        stage.setScene(new javafx.scene.Scene(box, 800, 460));
        stage.centerOnScreen();
        stage.showAndWait();
    }


    @FXML
    protected void onLockerRoomClick() {
        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.setTitle("Locker Room");

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(16);
        box.setStyle("-fx-background-color: #0d1117; -fx-padding: 24;");
        box.setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label("LOCKER ROOM — SET YOUR SQUAD");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        ListView<String> rosterLv   = new ListView<>();
        ListView<String> startersLv = new ListView<>();
        ListView<String> subsLv     = new ListView<>();
        rosterLv.setPrefHeight(260);
        startersLv.setPrefHeight(180);
        subsLv.setPrefHeight(90);

        for (IPlayer p : myTeam.getRoster()) {
            int total = p.getAttributes().values().stream().mapToInt(Integer::intValue).sum();
            String display = p.getName() + "  (" + total + ")";
            if (p.isInjured()) {
                rosterLv.getItems().add(display + "  [INJURED " + p.getRemainingInjuryDuration() + "w]");
                myStarters.removeIf(s -> s.getName().equals(p.getName()));
                mySubs.removeIf(s -> s.getName().equals(p.getName()));
            } else if (myStarters.stream().anyMatch(s -> s.getName().equals(p.getName()))) {
                startersLv.getItems().add(display);
            } else if (mySubs.stream().anyMatch(s -> s.getName().equals(p.getName()))) {
                subsLv.getItems().add(display);
            } else {
                rosterLv.getItems().add(display);
            }
        }

        Button toStarter = new Button("→ Starter");
        toStarter.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 14;");
        toStarter.setOnAction(e -> {
            String sel = rosterLv.getSelectionModel().getSelectedItem();
            if (sel == null || sel.contains("[INJURED")) { title.setText("Cannot add injured player!"); title.setTextFill(Color.web("#f85149")); return; }
            if (startersLv.getItems().size() < 7) { rosterLv.getItems().remove(sel); startersLv.getItems().add(sel); }
        });

        Button toSub = new Button("→ Sub");
        toSub.setStyle("-fx-background-color: #9e6a03; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 14;");
        toSub.setOnAction(e -> {
            String sel = rosterLv.getSelectionModel().getSelectedItem();
            if (sel == null || sel.contains("[INJURED")) { title.setText("Cannot add injured player!"); title.setTextFill(Color.web("#f85149")); return; }
            if (subsLv.getItems().size() < 3) { rosterLv.getItems().remove(sel); subsLv.getItems().add(sel); }
        });

        Button rmStarter = new Button("← Remove");
        rmStarter.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 14;");
        rmStarter.setOnAction(e -> {
            String sel = startersLv.getSelectionModel().getSelectedItem();
            if (sel != null) { startersLv.getItems().remove(sel); rosterLv.getItems().add(sel); }
        });

        Button rmSub = new Button("← Remove");
        rmSub.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 14;");
        rmSub.setOnAction(e -> {
            String sel = subsLv.getSelectionModel().getSelectedItem();
            if (sel != null) { subsLv.getItems().remove(sel); rosterLv.getItems().add(sel); }
        });

        javafx.scene.layout.VBox midBox = new javafx.scene.layout.VBox(10,
                toStarter, rmStarter,
                new Label(""),
                toSub, rmSub);
        midBox.setAlignment(javafx.geometry.Pos.CENTER);

        javafx.scene.layout.VBox rightBox = new javafx.scene.layout.VBox(6,
                new Label("Starting 7"), startersLv,
                new Label("Substitutes (max 3)"), subsLv);

        javafx.scene.layout.HBox lists = new javafx.scene.layout.HBox(16,
                vboxLabeled("Available Roster", rosterLv), midBox, rightBox);
        lists.setAlignment(javafx.geometry.Pos.CENTER);

        ComboBox<String> tacticBox = new ComboBox<>();
        tacticBox.getItems().addAll(getAvailableTactics());
        tacticBox.setValue(myTactic);

        Button saveBtn = new Button("SAVE SQUAD & TACTICS");
        saveBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 28; -fx-background-radius: 8;");
        saveBtn.setOnAction(e -> {
            if (startersLv.getItems().size() < 7) {
                title.setText("Select exactly 7 starters!");
                title.setTextFill(Color.web("#f85149"));
                return;
            }
            myStarters.clear(); mySubs.clear();
            myTactic = tacticBox.getValue();
            if (myTeam instanceof Team) ((Team) myTeam).setActiveTacticName(myTactic);

            for (IPlayer p : myTeam.getRoster()) {
                int total = p.getAttributes().values().stream().mapToInt(Integer::intValue).sum();
                String display = p.getName() + "  (" + total + ")";
                if (startersLv.getItems().contains(display)) myStarters.add(p);
                else if (subsLv.getItems().contains(display)) mySubs.add(p);
            }
            tacticLabel.setText("Tactic: " + myTactic);
            updateSquadPanel();
            stage.close();
        });

        box.getChildren().addAll(title, lists,
                new Label("Tactic:"), tacticBox, saveBtn);
        stage.setScene(new javafx.scene.Scene(box, 860, 580));
        stage.centerOnScreen();
        stage.showAndWait();
    }


    @FXML
    protected void onSaveGameClick() {
        if (currentLeague == null) return;
        String sport = detectSportType();

        javafx.scene.control.ChoiceDialog<String> dialog = new javafx.scene.control.ChoiceDialog<>("Slot 1",
                java.util.List.of("Slot 1", "Slot 2", "Slot 3"));
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Choose a save slot:");
        dialog.setContentText("Slot:");

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        int slot = Integer.parseInt(result.get().replace("Slot ", ""));
        this.currentSlot = slot;
        SaveManager.saveGame(currentLeague, sport, myTeam.getTeamName(), myTactic, myStarters, mySubs, slot);
        weekLabel.setText("Saved to Slot " + slot + "!");
    }

    @FXML
    protected void onLoadGameClick() {
        String sport = detectSportType();

        java.util.List<String> availableSlots = new java.util.ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            if (SaveManager.slotExists(sport, i)) {
                availableSlots.add("Slot " + i);
            }
        }

        if (availableSlots.isEmpty()) {
            weekLabel.setText("No save found for " + sport + ".");
            return;
        }

        javafx.scene.control.ChoiceDialog<String> dialog =
                new javafx.scene.control.ChoiceDialog<>(availableSlots.get(0), availableSlots);
        dialog.setTitle("Load Game");
        dialog.setHeaderText("Choose a save slot (" + sport + "):");
        dialog.setContentText("Slot:");

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        int slot = Integer.parseInt(result.get().replace("Slot ", ""));
        this.currentSlot = slot;

        League loaded = SaveManager.loadGame(sport, slot);
        if (loaded == null) { weekLabel.setText("No save found."); return; }
        this.currentLeague = loaded;

        String savedName = SaveManager.loadManagerTeamName(sport, slot);
        for (ITeam t : loaded.getStandings())
            if (t.getTeamName().equals(savedName)) { myTeam = t; break; }

        String savedTactic = SaveManager.loadManagerTactic(sport, slot);
        if (myTeam instanceof Team && savedTactic != null)
            ((Team) myTeam).setActiveTacticName(savedTactic);

        myStarters.clear(); mySubs.clear();
        restoreSavedLineup(
                SaveManager.loadLineupPlayerNames("starter", sport, slot),
                SaveManager.loadLineupPlayerNames("substitute", sport, slot));

        updateUI();
        showTeamDetails(myTeam);
        standingsTable.getSelectionModel().select(myTeam);
    }

    private boolean handleInjuredStarters() {
        java.util.List<IPlayer> injured = new java.util.ArrayList<>();
        for (IPlayer p : myStarters) if (p.isInjured()) injured.add(p);
        if (injured.isEmpty()) return true;

        java.util.List<IPlayer> toAdd = new java.util.ArrayList<>();
        java.util.Iterator<IPlayer> it = myStarters.iterator();
        while (it.hasNext()) {
            IPlayer p = it.next();
            if (p.isInjured()) {
                it.remove();
                IPlayer best = null;
                int maxStats = -1;
                for (IPlayer sub : mySubs) {
                    if (!sub.isInjured()) {
                        int total = sub.getAttributes().values().stream().mapToInt(Integer::intValue).sum();
                        if (total > maxStats) { maxStats = total; best = sub; }
                    }
                }
                if (best != null) { mySubs.remove(best); toAdd.add(best); }
                else {
                    for (IPlayer r : myTeam.getRoster()) {
                        if (!r.isInjured() && !myStarters.contains(r) && !mySubs.contains(r)) {
                            int total = r.getAttributes().values().stream().mapToInt(Integer::intValue).sum();
                            if (total > maxStats) { maxStats = total; best = r; }
                        }
                    }
                    if (best != null) toAdd.add(best);
                }
            }
        }
        myStarters.addAll(toAdd);
        updateUI();
        return true;
    }


    private void showChampionWindow() {
        java.util.List<ITeam> sorted = getSortedStandings();
        ITeam champion = sorted.get(0);
        boolean weWon = champion.getTeamName().equals(myTeam.getTeamName());

        int myPosition = sorted.indexOf(myTeam) + 1;
        boolean isFired = myPosition > (sorted.size() / 2 + 3); // Eğer ligin alt yarısından da kötüysen kovulursun

        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.setTitle("SEASON FINISHED - CONTRACT OFFERS");

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(20);
        box.setStyle("-fx-background-color: #0d1117; -fx-padding: 40;");
        box.setAlignment(javafx.geometry.Pos.CENTER);

        Label trophy = new Label(weWon ? "🏆" : (isFired ? "❌" : "📊"));
        trophy.setFont(Font.font(60));

        Label headline = new Label(weWon ? "CHAMPIONS!" : (isFired ? "YOU ARE FIRED!" : "SEASON COMPLETE"));
        headline.setFont(Font.font("System", FontWeight.BOLD, 28));
        headline.setTextFill(weWon ? Color.GOLD : (isFired ? Color.web("#f85149") : Color.WHITE));

        Label infoLabel = new Label("You finished at position: " + myPosition);
        infoLabel.setTextFill(Color.web("#8b949e"));

        java.util.List<ITeam> offers = new java.util.ArrayList<>();
        java.util.Random rand = new java.util.Random();

        if (!isFired) {
            offers.add(myTeam);

            if (myPosition <= 5) {
                while (offers.size() < 3) {
                    ITeam randomTopTeam = sorted.get(rand.nextInt(7));
                    if (!offers.contains(randomTopTeam)) offers.add(randomTopTeam);
                }
            } else {
                while (offers.size() < 3) {
                    ITeam randomMidTeam = sorted.get(rand.nextInt(sorted.size() / 2) + (sorted.size() / 2 - 2));
                    if (!offers.contains(randomMidTeam)) offers.add(randomMidTeam);
                }
            }
        } else {
            int bottomStart = Math.max(0, sorted.size() - 5);
            while (offers.size() < 3) {
                ITeam randomBottomTeam = sorted.get(rand.nextInt(sorted.size() - bottomStart) + bottomStart);
                if (!offers.contains(randomBottomTeam) && !randomBottomTeam.getTeamName().equals(myTeam.getTeamName())) {
                    offers.add(randomBottomTeam);
                }
            }
        }

        javafx.scene.layout.VBox offersBox = new javafx.scene.layout.VBox(10);
        offersBox.setAlignment(javafx.geometry.Pos.CENTER);

        Label offersTitle = new Label("CONTRACT OFFERS FOR NEXT SEASON");
        offersTitle.setTextFill(Color.WHITE);
        offersTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        offersBox.getChildren().add(offersTitle);

        for (ITeam offeredTeam : offers) {
            boolean isCurrentTeam = offeredTeam.getTeamName().equals(myTeam.getTeamName());
            Button btn = new Button(isCurrentTeam ? "Stay at " + offeredTeam.getTeamName() : "Sign with " + offeredTeam.getTeamName());

            if (isCurrentTeam) {
                btn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
            } else {
                btn.setStyle("-fx-background-color: #1f6feb; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
            }

            btn.setOnAction(e -> {
                myTeam = offeredTeam;

                currentLeague.prepareNewSeason();
                updateUI();
                showTeamDetails(myTeam);
                standingsTable.getSelectionModel().select(myTeam);

                stage.close();
            });

            offersBox.getChildren().add(btn);
        }

        Button quitBtn = new Button("QUIT TO DESKTOP");
        quitBtn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #f85149; -fx-font-weight: bold; -fx-padding: 8 28; -fx-background-radius: 6; -fx-border-color: #30363d; -fx-border-radius: 6;");
        quitBtn.setOnAction(e -> javafx.application.Platform.exit());

        box.getChildren().addAll(trophy, headline, infoLabel, offersBox, quitBtn);
        stage.setScene(new javafx.scene.Scene(box, 500, 600));
        stage.centerOnScreen();
        stage.showAndWait();
    }

    private String detectSportType() {
        if (myTeam != null && !myTeam.getRoster().isEmpty())
            return myTeam.getRoster().get(0).getAttributes().containsKey("ThrowPower")
                    ? "Handball" : "Headball";
        return "Headball";
    }

    private String getDefaultTactic() {
        return "Handball".equalsIgnoreCase(sportType) ? "6-0 Balanced" : "4-2-1 Balanced";
    }

    private java.util.List<String> getAvailableTactics() {
        if ("Handball".equalsIgnoreCase(sportType))
            return java.util.List.of("6-0 Balanced", "5-1 Offensive", "6-0 Defensive");
        return java.util.List.of("4-2-1 Balanced", "3-3-1 Offensive", "5-1-1 Defensive");
    }

    private ListCell<IPlayer> playerCell() {
        return new ListCell<IPlayer>() {
            @Override
            protected void updateItem(IPlayer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                int total = item.getAttributes().values().stream().mapToInt(Integer::intValue).sum();
                setText(item.getName() + "  (Total: " + total + ")"
                        + (item.isInjured() ? "  [INJURED]" : ""));
                setStyle(item.isInjured() ? "-fx-text-fill: #f85149;" : "-fx-text-fill: #e6edf3;");
            }
        };
    }

    private javafx.scene.layout.VBox vboxLabeled(String label, javafx.scene.Node node) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px; -fx-font-weight: bold;");
        javafx.scene.layout.VBox v = new javafx.scene.layout.VBox(6, lbl, node);
        return v;
    }
}