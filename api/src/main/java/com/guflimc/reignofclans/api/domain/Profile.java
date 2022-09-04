package com.guflimc.reignofclans.api.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface Profile {

    UUID id();

    String name();

    Optional<ClanProfile> clanProfile();

    Instant createdAt();

    Instant lastSeenAt();

    void setLastSeenAt(Instant lastSeenAt);

}
