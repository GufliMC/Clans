package com.guflimc.clans.api;

import com.guflimc.clans.api.crest.CrestType;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.CrestTemplate;
import com.guflimc.clans.api.domain.Profile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
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

    CompletableFuture<Clan> create(@NotNull Profile leader, @NotNull String name, @NotNull String tag);

    CompletableFuture<Void> remove(@NotNull Clan clan);

    CompletableFuture<Void> update(@NotNull Clan clan);

    // profiles

    CompletableFuture<Profile> findProfile(@NotNull UUID id);

    CompletableFuture<Profile> findProfile(@NotNull String name);

    CompletableFuture<List<Profile>> profiles(@NotNull Clan clan);

    Optional<Profile> findCachedProfile(@NotNull UUID id);

    Collection<Profile> cachedProfiles();

    // edit profiles

    CompletableFuture<Void> update(@NotNull Profile profile);

    CompletableFuture<Void> update(@NotNull ClanProfile clanProfile);

    // banner patterns

    Collection<CrestTemplate> crestTemplates();

    CompletableFuture<CrestTemplate> addCrestTemplate(@NotNull String name, @NotNull CrestType type, boolean restricted);

    CompletableFuture<Void> removeCrestTemplate(@NotNull CrestTemplate crestTemplate);
}
