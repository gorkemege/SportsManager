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
    }

    public void updateLeagueTable() {
        int t1Score = scoreMap.get(team1);
        int t2Score = scoreMap.get(team2);

        Map<String, Integer> rules = currentSport.getScoringRules();
        int winPoints = rules.get("Win");
        int drawPoints = rules.get("Draw");
        int lossPoints = rules.get("Loss");

        Team t1 = (Team) team1;
        Team t2 = (Team) team2;

        if (t1Score > t2Score) {

            t1.addMatchResult(winPoints, t1Score, t2Score);
            t2.addMatchResult(lossPoints, t2Score, t1Score);
            t1.recordHeadToHead(t2, winPoints);
            t2.recordHeadToHead(t1, lossPoints);
        } else if (t2Score > t1Score) {

            t2.addMatchResult(winPoints, t2Score, t1Score);
            t1.addMatchResult(lossPoints, t1Score, t2Score);
            t2.recordHeadToHead(t1, winPoints);
            t1.recordHeadToHead(t2, lossPoints);
        } else {

            t1.addMatchResult(drawPoints, t1Score, t2Score);
            t2.addMatchResult(drawPoints, t2Score, t1Score);
            t1.recordHeadToHead(t2, drawPoints);
            t2.recordHeadToHead(t1, drawPoints);
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
}