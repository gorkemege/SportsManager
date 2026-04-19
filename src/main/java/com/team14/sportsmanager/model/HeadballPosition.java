package com.team14.sportsmanager.model;

import com.team14.sportsmanager.core.IPosition;

public enum HeadballPosition implements IPosition{
    GOALKEEPER,
    DEFENDER,
    MIDFIELDER,
    STRIKER;

    @Override
    public String getName() {
        return name();
    }
}
