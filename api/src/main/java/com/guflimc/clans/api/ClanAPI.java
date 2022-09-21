package com.guflimc.clans.api;

import org.jetbrains.annotations.ApiStatus;

public class ClanAPI {

    private ClanAPI() {}

    private static ClanManager clanManager;

    @ApiStatus.Internal
    public static void register(ClanManager manager) {
        clanManager = manager;
    }

    //

    public static ClanManager get() {
        return clanManager;
    }
    
}
