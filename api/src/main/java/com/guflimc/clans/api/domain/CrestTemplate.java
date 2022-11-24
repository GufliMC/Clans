package com.guflimc.clans.api.domain;

import com.guflimc.clans.api.crest.CrestType;

import java.util.UUID;

public interface CrestTemplate {

    UUID id();

    String name();

    CrestType type();

    boolean restricted();

}
