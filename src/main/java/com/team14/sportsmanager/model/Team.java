package com.team14.sportsmanager.model;

import com.team14.sportsmanager.core.IPlayer;
import com.team14.sportsmanager.core.ITeam;
import com.team14.sportsmanager.core.ICoach;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team implements ITeam {
    private String teamName;
    private List<IPlayer> roster;
    private List<ICoach> coaches;
    private int totalPoints;
    private int goalsScored;
    private int goalsConceded;

    private Map<ITeam, Integer> h2hPointsMap;

    public Team(String teamName) {
        this.teamName = teamName;
        this.roster = new ArrayList<>();
        this.coaches = new ArrayList<>();
        this.totalPoints = 0;
        this.goalsScored = 0;
        this.goalsConceded = 0;
        this.h2hPointsMap = new HashMap<>();
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override
    public List<IPlayer> getRoster() {
        return roster;
    }

    @Override
    public void addMatchResult(int pointsEarned, int goalsFor, int goalsAgainst) {
        this.totalPoints += pointsEarned;
        this.goalsScored += goalsFor;
        this.goalsConceded += goalsAgainst;
    }

    @Override
    public List<ICoach> getCoachingStaff() {
        return coaches;
    }

    public void addCoach(ICoach coach) {
        this.coaches.add(coach);
    }

    @Override
    public int getTotalPoints() {
        return totalPoints;
    }

    @Override
    public int getGoalDifference() {
        return goalsScored - goalsConceded;
    }

    @Override
    public int getHeadToHeadPoints(ITeam opponent) {
        return h2hPointsMap.getOrDefault(opponent, 0);
    }

    public void recordHeadToHead(ITeam opponent, int points) {
        int currentH2H = h2hPointsMap.getOrDefault(opponent, 0);
        h2hPointsMap.put(opponent, currentH2H + points);
    }

    public void addPlayer(IPlayer player) {
        this.roster.add(player);
    }
}