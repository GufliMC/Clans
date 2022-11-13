package com.guflimc.clans.api.cosmetic;

public record CrestConfig(CrestType.Color color, ColorTarget target) {

    public CrestConfig withColor(CrestType.Color color) {
        return new CrestConfig(color, target);
    }

    public CrestConfig withTarget(ColorTarget target) {
        return new CrestConfig(color, target);
    }

    public enum ColorTarget {
        FOREGROUND,
        BACKGROUND
    }

}
