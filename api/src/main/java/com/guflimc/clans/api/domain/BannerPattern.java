package com.guflimc.clans.api.domain;

import java.util.UUID;

public interface BannerPattern {

    UUID id();

    String name();

    String data();

    boolean restricted();

}
