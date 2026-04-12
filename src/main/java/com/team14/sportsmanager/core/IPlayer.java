package com.team14.sportsmanager.core;

import java.util.Comparator;
import java.util.Map;

public interface IPlayer {
    String getName();
    Map<String, Integer> getAttributes();
    boolean isInjured();
    int getRemainingInjuryDuration();
    void updateAttribute(String attrName, int value);
    void setInjury(int matchCount);
}
