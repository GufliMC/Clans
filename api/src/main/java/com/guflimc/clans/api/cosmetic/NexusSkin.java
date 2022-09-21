package com.guflimc.clans.api.cosmetic;

public enum NexusSkin {
    DEFAULT("default.schem");

    private final String fileName;

    NexusSkin(String fileName) {
        this.fileName = fileName;
    }

    public String fileName() {
        return fileName;
    }

}
