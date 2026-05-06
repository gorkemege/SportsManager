package com.team14.sportsmanager.model;

import com.team14.sportsmanager.core.AbstractPlayer;

public class HandballPlayer extends AbstractPlayer {

    public HandballPlayer(String name, int throwPower, int speed, int agility, int goalkeepingSkill) {
        super(name);
        this.attributes.put("ThrowPower", throwPower);
        this.attributes.put("Speed", speed);
        this.attributes.put("Agility", agility);
        this.attributes.put("GoalkeepingSkill", goalkeepingSkill);
    }
}
