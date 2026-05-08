package com.team14.sportsmanager.logic;

import com.team14.sportsmanager.core.ILeague;
import com.team14.sportsmanager.core.IMatch;
import com.team14.sportsmanager.core.ISport;
import com.team14.sportsmanager.core.ITeam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class League implements ILeague {

    private ISport sport;
    private List<ITeam> teams;
    private List<List<IMatch>> fixtures;
    private int currentWeek;

    public League(ISport sport, List<ITeam> teams) {
        this.sport = sport;
        this.teams = teams;
        this.fixtures = new ArrayList<>();
        this.currentWeek = 0;
    }

    @Override
    public void generateFixtures() {
        fixtures.clear();

        List<ITeam> teamList = new ArrayList<>(teams);
        if (teamList.size() % 2 != 0) {
            teamList.add(null);
        }

        int rounds = teamList.size() - 1;
        int half = teamList.size() / 2;

        List<List<IMatch>> firstHalf = new ArrayList<>();

        for (int round = 0; round < rounds; round++) {
            List<IMatch> weekMatches = new ArrayList<>();
            for (int i = 0; i < half; i++) {
                ITeam home = teamList.get(i);
                ITeam away = teamList.get(teamList.size() - 1 - i);
                if (home != null && away != null) {
                    weekMatches.add(new MatchEngine(sport, home, away));
                }
            }
            firstHalf.add(weekMatches);

            ITeam last = teamList.remove(teamList.size() - 1);
            teamList.add(1, last);
        }

        List<List<IMatch>> secondHalf = new ArrayList<>();
        for (List<IMatch> week : firstHalf) {
            List<IMatch> reversedWeek = new ArrayList<>();
            for (IMatch match : week) {
                MatchEngine original = (MatchEngine) match;
                reversedWeek.add(new MatchEngine(sport, original.getTeam2(), original.getTeam1()));
            }
            secondHalf.add(reversedWeek);
        }

        fixtures.addAll(firstHalf);
        fixtures.addAll(secondHalf);
    }

    @Override
    public List<List<IMatch>> getFixtures() {
        return fixtures;
    }

    @Override
    public int getCurrentWeek() {
        return currentWeek;
    }

    @Override
    public void advanceWeek() {
        if (isLeagueFinished()) return;

        List<IMatch> weekMatches = fixtures.get(currentWeek);
        for (IMatch match : weekMatches) {
            MatchEngine engine = (MatchEngine) match;
            engine.simulateMatch();
        }
        currentWeek++;
    }

    @Override
    public List<ITeam> getStandings() {
        List<ITeam> sorted = new ArrayList<>(teams);
        sorted.sort(sport.getStandingLogic());
        return sorted;
    }

    @Override
    public boolean isLeagueFinished() {
        return currentWeek >= fixtures.size();
    }
    public void advanceWeekForLoad() {
    if (currentWeek < fixtures.size()) {
        currentWeek++;
    }
}

    public void advanceWeekExcluding(IMatch playerMatch) {
        if (isLeagueFinished()) return;

        List<IMatch> weekMatches = fixtures.get(currentWeek);
        for (IMatch match : weekMatches) {
            if (match != playerMatch) {
                MatchEngine engine = (MatchEngine) match;
                engine.simulateMatch();
            }
        }
        currentWeek++;
    }
}