package com.guflimc.lavaclans.api.domain;

import com.guflimc.brick.maths.api.geo.Location;

import java.time.Instant;
import java.util.UUID;

public interface nexus {

    UUID id();

    Location location();

    Clan clan();

    int level();

    void setLevel(int level);

    boolean hasShield();

    Instant shieldExpireAt();

    void activateShield(Instant expireAt);

    Instant createdAt();

}
