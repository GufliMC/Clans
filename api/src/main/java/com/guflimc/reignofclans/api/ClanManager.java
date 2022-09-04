package com.guflimc.reignofclans.api;

import com.guflimc.reignofclans.api.domain.Clan;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanManager {

    void reload();

    Collection<Clan> clans();

    Optional<Clan> find(String name);

    Optional<Clan> find(UUID id);

    Clan create(@NotNull String name, @NotNull String tag);

    CompletableFuture<Void> remove(@NotNull Clan clan);

    CompletableFuture<Void> update(@NotNull Clan clan);

}
