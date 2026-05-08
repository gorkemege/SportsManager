package com.team14.sportsmanager.logic;

import com.team14.sportsmanager.core.IMatch;
import com.team14.sportsmanager.core.IPlayer;
import com.team14.sportsmanager.core.ISport;
import com.team14.sportsmanager.core.ITeam;
import com.team14.sportsmanager.model.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MatchEngine implements IMatch {

    private ISport currentSport;
    private ITeam team1;
    private ITeam team2;
    private int currentPeriod;
    private boolean isFinished;
    private Map<ITeam, Integer> scoreMap;
    private List<String> matchEvents;
    private List<IPlayer> team1Lineup;
    private List<IPlayer> team2Lineup;

    public MatchEngine(ISport sport, ITeam t1, ITeam t2) {
        this.currentSport = sport;
        this.team1 = t1;
        this.team2 = t2;
        this.currentPeriod = 0;
        this.isFinished = false;
        this.matchEvents = new ArrayList<>();

        this.scoreMap = new HashMap<>();
        this.scoreMap.put(this.team1, 0);
        this.scoreMap.put(this.team2, 0);
        this.team1Lineup = defaultLineup(t1);
        this.team2Lineup = defaultLineup(t2);
    }

    public void simulateMatch() {
        while (!isFinished) {
            simulatePeriod();
        }
    }

    private List<IPlayer> defaultLineup(ITeam team) {
        List<IPlayer> lineup = new ArrayList<>();

        for (IPlayer player : team.getRoster()) {
            if (!player.isInjured()) {
                lineup.add(player);
            }

            if (lineup.size() == 7) {
                break;
            }
        }

        return lineup;
    }

    public void setLineup(ITeam team, List<IPlayer> lineup) {

        if (lineup == null || lineup.size() != 7) {
            throw new IllegalArgumentException("Lineup must contain exactly 7 players.");
        }

        for (IPlayer player : lineup) {
            if (player.isInjured()) {
                throw new IllegalArgumentException("Injured players cannot be in the lineup.");
            }
        }

        if (team == team1) {
            this.team1Lineup = new ArrayList<>(lineup);
        } else if (team == team2) {
            this.team2Lineup = new ArrayList<>(lineup);
        } else {
            throw new IllegalArgumentException("Lineup team is not part of this match.");
        }
    }

    @Override
    public void simulatePeriod() {
        if (isFinished) return;

        currentPeriod++;
        matchEvents.add("Period " + currentPeriod + " has started.");

      
        double t1Power = calculateTeamPower(team1);
        double t2Power = calculateTeamPower(team2);

        Random rand = new Random();
        int t1Goals = 0;
        int t2Goals = 0;

        int scoringAttempts = getScoringAttemptsPerPeriod();
        double scoreChance = getScoreChance();

        for (int i = 0; i < scoringAttempts; i++) {
            double totalPower = t1Power + t2Power;
            double chance = rand.nextDouble() * totalPower;

            if (chance < t1Power) {
                if (rand.nextDouble() < scoreChance) {
                    t1Goals++;
                }
            } else {
                if (rand.nextDouble() < scoreChance) {
                    t2Goals++;
                }
            }
        }
        scoreMap.put(team1, scoreMap.get(team1) + t1Goals);
        scoreMap.put(team2, scoreMap.get(team2) + t2Goals);

        matchEvents.add(team1.getTeamName() + " scored " + t1Goals + " goals.");
        matchEvents.add(team2.getTeamName() + " scored " + t2Goals + " goals.");

        if (currentPeriod >= currentSport.getPeriodCount()) {
            isFinished = true;
            matchEvents.add("Match is finished. Final Score: " + scoreMap.get(team1) + " - " + scoreMap.get(team2));

            finalizeMatch();
        }
    }

    private int getScoringAttemptsPerPeriod() {
        if ("Headball".equalsIgnoreCase(currentSport.getSportName())) {
            return 18;
        }

        if ("Handball".equalsIgnoreCase(currentSport.getSportName())) {
            return 20;
        }

        return 10;
    }

    private double getScoreChance() {
        if ("Headball".equalsIgnoreCase(currentSport.getSportName())) {
            return 0.45;
        }

        if ("Handball".equalsIgnoreCase(currentSport.getSportName())) {
            return 0.50;
        }

        return 0.40;
    }
    
    private double calculateTeamPower(ITeam team) {
        double power = 50.0;


        List<IPlayer> lineup = (team == team1) ? team1Lineup : team2Lineup;

        for (IPlayer player : lineup) {
            if (!player.isInjured()) {
                for (int attributeValue : player.getAttributes().values()) {
                    power += attributeValue;
                }
            }
        }

        
        if (team instanceof Team) {
            String tactic = ((Team) team).getActiveTacticName();
            if (tactic != null) {
                if (tactic.contains("Offensive")) {
                    power *= 1.20; 
                    matchEvents.add(team.getTeamName() + " is playing an Offensive style!");
                } else if (tactic.contains("Defensive")) {
                    power *= 0.85; 
                    matchEvents.add(team.getTeamName() + " is parking the bus (Defensive)!");
                } else {
                    power *= 1.05; 
                }
            }
        }
        return power;
    }

    public void finalizeMatch() {
        updateLeagueTable();
        applyInjuries();
    }

    private void applyInjuries() {
        Random rand = new Random();
        applyInjuriesToTeam(team1, rand);
        applyInjuriesToTeam(team2, rand);
    }

    private void applyInjuriesToTeam(ITeam team, Random rand) {
        for (IPlayer player : team.getRoster()) {
            if (player.isInjured()) {
                player.decreaseInjuryDuration();
            } else if (rand.nextInt(100) < 5) {
                int duration = 1 + rand.nextInt(3);
                player.setInjury(duration);
                matchEvents.add("INJURY: " + player.getName() + " is injured for " + duration + " match(es)!");
            }
        }
    }

    public void updateLeagueTable() {
        int t1Score = scoreMap.get(team1);
        int t2Score = scoreMap.get(team2);

        Map<String, Integer> rules = currentSport.getScoringRules();
        int winPoints = rules.get("Win");
        int drawPoints = rules.get("Draw");
        int lossPoints = rules.get("Loss");

        if (t1Score > t2Score) {

            team1.addMatchResult(winPoints, t1Score, t2Score);
            team2.addMatchResult(lossPoints, t2Score, t1Score);
            team1.recordHeadToHead(team2, winPoints);
            team2.recordHeadToHead(team1, lossPoints);
        } else if (t2Score > t1Score) {

            team2.addMatchResult(winPoints, t2Score, t1Score);
            team1.addMatchResult(lossPoints, t1Score, t2Score);
            team2.recordHeadToHead(team1, winPoints);
            team1.recordHeadToHead(team2, lossPoints);
        } else {

            team1.addMatchResult(drawPoints, t1Score, t2Score);
            team2.addMatchResult(drawPoints, t2Score, t1Score);
            team1.recordHeadToHead(team2, drawPoints);
            team2.recordHeadToHead(team1, drawPoints);
        }
    }

    @Override
    public boolean isMatchFinished() {
        return isFinished;
    }

    @Override
    public Map<ITeam, Integer> getCurrentScore() {
        return scoreMap;
    }

    @Override
    public void applyUserSubstitution(IPlayer outPlayer, IPlayer inPlayer) {
        matchEvents.add("Substitution: " + inPlayer.getName() + " IN, " + outPlayer.getName() + " OUT.");
    }

    @Override
    public List<String> getMatchEvents() {
        return matchEvents;
    }

    public ITeam getTeam1() {
        return team1;
    }

    public ITeam getTeam2() {
        return team2;
    }
}