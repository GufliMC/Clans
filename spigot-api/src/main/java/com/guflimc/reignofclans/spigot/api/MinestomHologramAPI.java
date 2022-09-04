package com.guflimc.reignofclans.spigot.api;

import com.guflimc.reignofclans.api.ClanAPI;
import org.jetbrains.annotations.ApiStatus;

public class MinestomHologramAPI {

    private MinestomHologramAPI() {}

    private static MinestomHologramManager creatureManager;

    @ApiStatus.Internal
    public static void setHologramManager(MinestomHologramManager manager) {
        ClanAPI.setClanManager(manager);
        creatureManager = manager;
    }

    //

    public static MinestomHologramManager get() {
        return creatureManager;
    }

}
