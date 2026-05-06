package com.team14.sportsmanager.model;

import com.team14.sportsmanager.core.IPosition;

public enum HandballPosition implements IPosition {
    GOALKEEPER,
    LEFT_WING,
    RIGHT_WING,
    LEFT_BACK,
    RIGHT_BACK,
    CENTER_BACK,
    PIVOT;

    @Override
    public String getName() {
        return name();
    }
}
