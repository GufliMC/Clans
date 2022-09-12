package com.guflimc.lavaclans.api.domain;

import com.guflimc.brick.maths.api.geo.pos.Location;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public interface Nexus {

    UUID id();

    Location location();

    UUID regionId();

    Clan clan();

    int level();

    void setLevel(int level);

    boolean hasShield();

    Instant shieldExpireAt();

    void activateShield(@NotNull Instant expireAt);

    Instant createdAt();

}
