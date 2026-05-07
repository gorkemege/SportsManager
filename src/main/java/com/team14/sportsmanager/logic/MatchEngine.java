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
    }

    public void simulateMatch() {
        while (!isFinished) {
            simulatePeriod();
        }
    }

    @Override
    public void simulatePeriod() {
        if (isFinished) return;

        currentPeriod++;
        matchEvents.add("Period " + currentPeriod + " has started.");

        Random rand = new Random();
        int t1Goals = rand.nextInt(5) + 2;
        int t2Goals = rand.nextInt(5) + 2;

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
            if (!player.isInjured() && rand.nextInt(100) < 5) {
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