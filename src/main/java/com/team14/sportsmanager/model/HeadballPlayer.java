package com.team14.sportsmanager.model;


import com.team14.sportsmanager.core.AbstractPlayer;

public class HeadballPlayer extends AbstractPlayer {

    public HeadballPlayer(String name, int headPower, int jumpHeight) {
        super(name);
        this.attributes.put("HeadPower", headPower);
        this.attributes.put("JumpHeight", jumpHeight);
    }
}
