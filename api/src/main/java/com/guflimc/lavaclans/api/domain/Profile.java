package com.guflimc.lavaclans.api.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface Profile {

    UUID id();

    String name();

    Optional<ClanProfile> clanProfile();

    Instant createdAt();

    Instant lastSeenAt();

    void addJoinRequest(Clan clan);

    void addInvite(Profile sender, Clan clan);

}
