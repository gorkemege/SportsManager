package com.team14.sportsmanager.model;

import com.team14.sportsmanager.core.AbstractCoach;

public class HandballCoach extends AbstractCoach {

    public HandballCoach(String name, int throwPowerSkill, int speedSkill, int agilitySkill) {
        super(name);
        this.specialties.put("ThrowPower", throwPowerSkill);
        this.specialties.put("Speed", speedSkill);
        this.specialties.put("Agility", agilitySkill);
    }
}
