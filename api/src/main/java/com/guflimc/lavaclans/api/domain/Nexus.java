package com.guflimc.lavaclans.api.domain;

import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.regions.api.domain.AreaRegion;
import com.guflimc.lavaclans.api.cosmetic.NexusSkin;

import java.time.Instant;
import java.util.UUID;

public interface Nexus extends AreaRegion {

    UUID id();

    Location location();

    Clan clan();

    NexusSkin skin();

    Instant createdAt();

}
