package com.guflimc.reignofclans.common;

import com.guflimc.reignofclans.api.ClanManager;
import com.guflimc.reignofclans.api.domain.Clan;
import com.guflimc.reignofclans.common.domain.DClan;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class ReignOfClansManager implements ClanManager {

    private final Logger logger = LoggerFactory.getLogger(ReignOfClansManager.class);

    private final ReignOfClansDatabaseContext databaseContext;

    private final Set<DClan> clans = new CopyOnWriteArraySet<>();

    public ReignOfClansManager(ReignOfClansDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
        reload();
    }

    @Override
    public void reload() {
        clans.clear();
        databaseContext.findAllAsync(DClan.class).join().addAll(clans);
    }

    @Override
    public Collection<Clan> clans() {
        return new ArrayList<>(clans);
    }

    @Override
    public Optional<Clan> find(String name) {
        return clans.stream()
                .filter(c -> c.name().equalsIgnoreCase(name))
                .findFirst().map(c -> c);
    }

    @Override
    public Optional<Clan> find(UUID id) {
        return clans.stream()
                .filter(c -> c.id().equals(id))
                .findFirst().map(c -> c);
    }

    @Override
    public Clan create(@NotNull String name, @NotNull String tag) {
        if ( name.length() < 3 ) {
            throw new IllegalArgumentException("Clan name must be at least 3 characters.");
        }
        if ( tag.length() < 2 || tag.length() > 3 ) {
            throw new IllegalArgumentException("Clan tag must be exactly 2 or 3 characters.");
        }
        if ( clans.stream().anyMatch(c -> c.name().equalsIgnoreCase(name)
                || c.tag().equalsIgnoreCase(tag)) ) {
            throw new IllegalArgumentException("A clan with that name or tag already exists.");
        }

        logger.debug("Created new clan '" + name + "' with tag '" + tag + "'.");

        DClan clan = new DClan(name, tag);
        clans.add(clan);
        databaseContext.persistAsync(clan);
        return clan;
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Clan clan) {
        logger.debug("Deleting clan '" + clan.name() + "'.");

        // TODO consistency check

        clans.remove((DClan) clan);
        return databaseContext.removeAsync(clan);
    }

    @Override
    public CompletableFuture<Void> update(@NotNull Clan clan) {
        return databaseContext.mergeAsync(clan).thenRun(() -> {});
    }
}
