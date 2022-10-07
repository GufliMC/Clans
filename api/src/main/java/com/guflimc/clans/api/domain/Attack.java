package com.guflimc.clans.api.domain;

import java.time.Instant;

public interface Attack {

    Clan defender();

    Clan attacker();

    Instant createdAt();

    Instant endedAt();

    void setEndedAt(Instant endedAt);

    int nexusHealth();

    void setNexusHealth(int health);

    boolean isNexusDestroyed();
}
