package com.guflimc.lavaclans.api.domain;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface Profile {

    UUID id();

    String name();

    Optional<ClanProfile> clanProfile();

    Instant createdAt();

    Instant lastSeenAt();

    ClanInvite addInvite(@NotNull Profile sender, @NotNull Clan clan);

    Optional<ClanInvite> mostRecentInvite(@NotNull Clan clan);

}
