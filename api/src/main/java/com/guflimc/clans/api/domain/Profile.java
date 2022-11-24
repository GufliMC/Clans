package com.guflimc.clans.api.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface Profile {

    UUID id();

    String name();

    Optional<ClanProfile> clanProfile();

    ClanInvite addInvite(@NotNull Profile sender, @NotNull Clan clan);

    Optional<ClanInvite> mostRecentInvite(@NotNull Clan clan);

    // attributes

    <T> void setAttribute(AttributeKey<T> key, T value);

    <T> void removeAttribute(AttributeKey<T> key);

    <T> Optional<T> attribute(AttributeKey<T> key);

    // timestamps

    Instant lastSeenAt();

    Instant createdAt();

}
