package com.team14.sportsmanager.core;

import java.util.List;

public interface ITeam {
    String getTeamName();
    List<IPlayer> getRoster();
    List<ICoach> getCoachingStaff();
    void addMatchResult(int pointsEarned, int goalsFor, int goalsAgainst);
    int getTotalPoints();
    int getGoalDifference();
    int getHeadToHeadPoints(ITeam opponent);
}
