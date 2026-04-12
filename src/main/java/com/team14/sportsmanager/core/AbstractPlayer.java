package com.team14.sportsmanager.core;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPlayer implements IPlayer {
    protected String name;
    protected boolean injured = false;
    protected int injuryDuration = 0;
    protected Map<String, Integer> attributes = new HashMap<>();

    public AbstractPlayer(String name) { this.name = name; }
    @Override public String getName() { return name; }
    @Override public Map<String, Integer> getAttributes() { return attributes; }
    @Override public boolean isInjured() { return injured; }
    @Override public int getRemainingInjuryDuration() { return injuryDuration; }
    @Override public void updateAttribute(String name, int val) { attributes.put(name, val); }
    @Override public void setInjury(int count) { this.injured = true; this.injuryDuration = count; }
}
