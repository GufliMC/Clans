package com.guflimc.reignofclans.api.domain;

import java.util.Optional;
import java.util.UUID;

public interface Clan {

    UUID id();

    String name();

    String tag();

    Optional<Crest> nexus();

    void setNexus(Crest nexus);

    int rgbColor();

    void setRGBColor(int rgbColor);

}
