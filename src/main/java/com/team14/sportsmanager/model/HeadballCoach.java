package com.team14.sportsmanager.model;

import com.team14.sportsmanager.core.AbstractCoach;

public class HeadballCoach extends AbstractCoach {

    public HeadballCoach(String name, int headPowerSkill, int jumpSkill) {
        super(name);
        this.specialties.put("HeadPower", headPowerSkill);
        this.specialties.put("JumpHeight", jumpSkill);
    }
}