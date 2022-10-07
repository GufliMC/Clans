package com.guflimc.clans.api;

import org.jetbrains.annotations.ApiStatus;

public class AttackAPI {

    private AttackAPI() {}

    private static AttackManager attackManager;

    @ApiStatus.Internal
    public static void register(AttackManager manager) {
        attackManager = manager;
    }

    //

    public static AttackManager get() {
        return attackManager;
    }
    
}
