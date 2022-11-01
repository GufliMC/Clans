package com.guflimc.clans.api.domain;

import java.util.Optional;
import java.util.UUID;

public interface Clan {

    UUID id();

    String name();

    String tag();

    Optional<Nexus> nexus();

    int nexusRadius();

    int color();

    void setColor(int color);

    int level();

    void setLevel(int level);

    int maxMembers();

    void setMaxMembers(int value);

    int memberCount();

}
