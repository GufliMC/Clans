package com.guflimc.lavaclans.api.domain;

import java.time.Instant;
import java.util.UUID;

public interface ClanJoinRequest {

    UUID id();

    Profile sender();

    Clan clan();

    Instant createdAt();

}
