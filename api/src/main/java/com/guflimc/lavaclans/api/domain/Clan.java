package com.guflimc.lavaclans.api.domain;

import com.guflimc.brick.maths.api.geo.pos.Location;

import java.util.Optional;
import java.util.UUID;

public interface Clan {

    UUID id();

    String name();

    String tag();

    Optional<Nexus> nexus();

    void setNexus(Nexus nexus);

    int rgbColor();

    void setRGBColor(int rgbColor);

    void setNexus(UUID regionId, Location location);

}
