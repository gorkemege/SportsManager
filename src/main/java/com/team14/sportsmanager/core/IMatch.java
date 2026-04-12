package com.team14.sportsmanager.core;

import java.util.List;
import java.util.Map;

public interface IMatch {
    void simulatePeriod();
    boolean isMatchFinished();
    Map<ITeam, Integer> getCurrentScore();
    void applyUserSubstitution(IPlayer outPlayer, IPlayer inPlayer);
    List<String> getMatchEvents();
}
