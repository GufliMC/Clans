package com.guflimc.lavaclans.api;

import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.ClanInvite;
import com.guflimc.lavaclans.api.domain.ClanJoinRequest;
import com.guflimc.lavaclans.api.domain.Profile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanManager {

    void reload();

    // clans

    Collection<Clan> clans();

    Optional<Clan> findClan(@NotNull String name);

    Optional<Clan> findClan(@NotNull UUID id);

    Optional<Clan> findClanByTag(@NotNull String tag);

    // edit clans

    CompletableFuture<Clan> create(@NotNull String name, @NotNull String tag);

    CompletableFuture<Void> remove(@NotNull Clan clan);

    CompletableFuture<Void> update(@NotNull Clan clan);

    // profiles

    CompletableFuture<Profile> findProfile(@NotNull UUID id);

    CompletableFuture<Profile> findProfile(@NotNull String name);

    Profile findCachedProfile(@NotNull UUID id);

    // edit profiles

    CompletableFuture<Void> joinClan(@NotNull Profile profile, @NotNull Clan clan);

    CompletableFuture<Void> quitClan(@NotNull Profile profile);

    CompletableFuture<Void> update(@NotNull Profile profile);

}
