package com.team14.sportsmanager.core;

import java.util.List;

public interface ITeam {String getTeamName();
    List<IPlayer> getRoster();
    void addMatchResult(int pointsEarned);
    int getTotalPoints();
}
