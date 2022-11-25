package com.guflimc.clans.api.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public interface Profile {

    UUID id();

    String name();

    Optional<ClanProfile> clanProfile();

    ClanInvite addInvite(@NotNull Profile sender, @NotNull Clan clan);

    Optional<ClanInvite> mostRecentInvite(@NotNull Clan clan);

    // attributes

    <T> void setAttribute(ProfileAttributeKey<T> key, T value);

    <T> void removeAttribute(ProfileAttributeKey<T> key);

    <T> Optional<T> attribute(ProfileAttributeKey<T> key);

    // timestamps

    Instant lastSeenAt();

    Instant createdAt();

    //

    class ProfileAttributeKey<T> extends AttributeKey<T> {

        public ProfileAttributeKey(String name, Class<T> type, Function<T, String> serializer, Function<String, T> deserializer) {
            super(name, type, serializer, deserializer);
        }

    }

}
