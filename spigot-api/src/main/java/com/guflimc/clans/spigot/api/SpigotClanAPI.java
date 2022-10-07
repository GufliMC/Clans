package com.guflimc.clans.spigot.api;

import com.guflimc.clans.api.ClanAPI;
import org.jetbrains.annotations.ApiStatus;

public class SpigotClanAPI {

    private SpigotClanAPI() {
    }

    private static SpigotClanManager clanManager;

    @ApiStatus.Internal
    public static void register(SpigotClanManager manager) {
        clanManager = manager;
        ClanAPI.register(manager);
    }

    //

    public static SpigotClanManager get() {
        return clanManager;
    }

}
