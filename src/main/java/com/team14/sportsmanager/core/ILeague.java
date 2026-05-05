package com.team14.sportsmanager.core;

import java.util.List;

public interface ILeague {
    void generateFixtures();
    List<List<IMatch>> getFixtures();
    int getCurrentWeek();
    void advanceWeek();
    List<ITeam> getStandings();
    boolean isLeagueFinished();
}
