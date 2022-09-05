package com.guflimc.lavaclans.api.domain;

import java.time.Instant;
import java.util.UUID;

public interface ClanInvite {

    UUID id();

    Profile sender();

    Profile target();

    Clan clan();

    Instant createdAt();

}
