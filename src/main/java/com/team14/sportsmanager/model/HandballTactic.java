package com.team14.sportsmanager.model;

import com.team14.sportsmanager.core.IPosition;
import com.team14.sportsmanager.core.ITactic;

import java.util.HashMap;
import java.util.Map;

public class HandballTactic implements ITactic {

    private String name;
    private Map<IPosition, Integer> formation;

    public HandballTactic(String name) {
        this.name = name;
        this.formation = new HashMap<>();
        applyFormation(name);
    }

    private void applyFormation(String name) {
        switch (name) {
            case "6-0":
                formation.put(HandballPosition.GOALKEEPER, 1);
                formation.put(HandballPosition.LEFT_WING, 1);
                formation.put(HandballPosition.RIGHT_WING, 1);
                formation.put(HandballPosition.LEFT_BACK, 1);
                formation.put(HandballPosition.RIGHT_BACK, 1);
                formation.put(HandballPosition.CENTER_BACK, 1);
                formation.put(HandballPosition.PIVOT, 1);
                break;
            case "5-1":
                formation.put(HandballPosition.GOALKEEPER, 1);
                formation.put(HandballPosition.LEFT_WING, 1);
                formation.put(HandballPosition.RIGHT_WING, 1);
                formation.put(HandballPosition.LEFT_BACK, 1);
                formation.put(HandballPosition.RIGHT_BACK, 1);
                formation.put(HandballPosition.CENTER_BACK, 1);
                formation.put(HandballPosition.PIVOT, 1);
                break;
            default:
                formation.put(HandballPosition.GOALKEEPER, 1);
                formation.put(HandballPosition.LEFT_WING, 1);
                formation.put(HandballPosition.RIGHT_WING, 1);
                formation.put(HandballPosition.LEFT_BACK, 1);
                formation.put(HandballPosition.RIGHT_BACK, 1);
                formation.put(HandballPosition.CENTER_BACK, 1);
                formation.put(HandballPosition.PIVOT, 1);
                break;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<IPosition, Integer> getFormation() {
        return formation;
    }

    @Override
    public int getTotalFieldPlayers() {
        return formation.values().stream().mapToInt(Integer::intValue).sum();
    }
}
