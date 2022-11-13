package com.guflimc.clans.api.domain;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public interface ClanAttackShield {

    UUID id();

    Clan clan();

    Instant createdAt();

    Duration duration();

    @Nullable Instant activateAt();

    @Nullable Instant expireAt();

}
