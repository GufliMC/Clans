package com.guflimc.lavaclans.api.domain;

import java.util.Optional;
import java.util.UUID;

public interface Clan {

    UUID id();

    String name();

    String tag();

    Optional<nexus> nexus();

    void setNexus(nexus nexus);

    int rgbColor();

    void setRGBColor(int rgbColor);

}
