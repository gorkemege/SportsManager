package com.team14.sportsmanager.core;
import java.util.Comparator;
import java.util.Map;

public interface ISport {
    String getSportName();
    int getPeriodCount();
    Comparator<ITeam> getStandingLogic();
    Map<String, Integer> getScoringRules();
}
