package com.team14.sportsmanager.core;

import java.util.Map;

public interface ITactic {
    String getName();
    Map<IPosition, Integer> getFormation();
    int getTotalFieldPlayers();
}
