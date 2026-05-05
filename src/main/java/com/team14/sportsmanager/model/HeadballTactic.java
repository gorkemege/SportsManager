package com.team14.sportsmanager.model;

import com.team14.sportsmanager.core.IPosition;
import com.team14.sportsmanager.core.ITactic;

import java.util.HashMap;
import java.util.Map;

public class HeadballTactic implements ITactic {

    private String name;
    private Map<IPosition, Integer> formation;

    public HeadballTactic(String name) {
        this.name = name;
        this.formation = new HashMap<>();
        applyFormation(name);
    }

    private void applyFormation(String name) {
        switch (name) {
            case "4-2-1":
                formation.put(HeadballPosition.GOALKEEPER, 1);
                formation.put(HeadballPosition.DEFENDER, 4);
                formation.put(HeadballPosition.MIDFIELDER, 2);
                formation.put(HeadballPosition.STRIKER, 1);
                break;
            case "3-3-1":
                formation.put(HeadballPosition.GOALKEEPER, 1);
                formation.put(HeadballPosition.DEFENDER, 3);
                formation.put(HeadballPosition.MIDFIELDER, 3);
                formation.put(HeadballPosition.STRIKER, 1);
                break;
            default:
                formation.put(HeadballPosition.GOALKEEPER, 1);
                formation.put(HeadballPosition.DEFENDER, 4);
                formation.put(HeadballPosition.MIDFIELDER, 2);
                formation.put(HeadballPosition.STRIKER, 1);
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