package com.guflimc.lavaclans.api.domain;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public interface ClanShield {

    UUID id();

    Clan clan();

    Instant createdAt();

    Duration duration();

    @Nullable Instant activateAt();

    @Nullable Instant expireAt();

}
