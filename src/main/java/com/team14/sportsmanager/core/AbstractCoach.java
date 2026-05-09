package com.team14.sportsmanager.core;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCoach implements ICoach {
    protected String name;
    protected Map<String, Integer> specialties = new HashMap<>();

    public AbstractCoach(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Integer> getSpecialties() {
        return specialties;
    }

    @Override
    public void train(IPlayer player) {
        Map<String, Integer> playerAttrs = player.getAttributes();
        java.util.Random rand = new java.util.Random();

        java.util.List<String> commonAttrs = new java.util.ArrayList<>();
        for (String attr : specialties.keySet()) {
            if (playerAttrs.containsKey(attr)) commonAttrs.add(attr);
        }
        if (commonAttrs.isEmpty()) return;

        if (rand.nextInt(100) >= 25) return;

        String attr = commonAttrs.get(rand.nextInt(commonAttrs.size()));
        int current = playerAttrs.get(attr);
        player.updateAttribute(attr, Math.min(100, current + 1));
    }
}
