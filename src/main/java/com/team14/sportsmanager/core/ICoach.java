package com.team14.sportsmanager.core;

import java.util.Map;

public interface ICoach {
    String getName();
    Map<String, Integer> getSpecialties();
    void train(IPlayer player);
}