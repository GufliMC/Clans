package com.guflimc.lavaclans.api.domain;

import java.util.Optional;
import java.util.UUID;

public interface Clan {

    UUID id();

    String name();

    String tag();

    Optional<Nexus> nexus();

    int rgbColor();

    void setRGBColor(int rgbColor);

    int level();

    void setLevel(int level);

    int maxMembers();

    void setMaxMembers(int value);

    int memberCount();

}
