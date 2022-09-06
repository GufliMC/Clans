package com.guflimc.lavaclans.api.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public interface ClanInvite {

    UUID id();

    Profile sender();

    Profile target();

    Clan clan();

    void reject();

    void accept();

    boolean isValid();

    boolean isExpired();

}
