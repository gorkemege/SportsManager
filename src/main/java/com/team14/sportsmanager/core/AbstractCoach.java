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
        for (Map.Entry<String, Integer> entry : specialties.entrySet()) {
            String attr = entry.getKey();
            int quality = entry.getValue();

            if (!playerAttrs.containsKey(attr)) continue;

            int gain = quality / 40;
            if (gain < 1) gain = 1;

            int current = playerAttrs.get(attr);
            int updated = Math.min(100, current + gain);
            player.updateAttribute(attr, updated);
        }
    }
}
