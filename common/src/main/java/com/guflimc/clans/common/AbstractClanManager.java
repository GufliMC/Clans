package com.guflimc.clans.common;

import com.guflimc.clans.api.ClanManager;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.api.domain.SigilType;
import com.guflimc.clans.common.domain.DClan;
import com.guflimc.clans.common.domain.DClanProfile;
import com.guflimc.clans.common.domain.DProfile;
import com.guflimc.clans.common.domain.DSigilType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

public abstract class AbstractClanManager implements ClanManager {

    private final Logger logger = LoggerFactory.getLogger(AbstractClanManager.class);

    protected final ClansDatabaseContext databaseContext;

    private final Set<DClan> clans = new CopyOnWriteArraySet<>();
    private final Set<DProfile> profiles = new CopyOnWriteArraySet<>();

    private final Set<DSigilType> sigilTypes = new CopyOnWriteArraySet<>();

    public AbstractClanManager(ClansDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
        reload();
    }

    @Override
    public void reload() {
        logger.info("Reloading clan manager...");

        clans.clear();
        clans.addAll(databaseContext.findAllAsync(DClan.class).join());

        sigilTypes.clear();
        sigilTypes.addAll(databaseContext.findAllAsync(DSigilType.class).join());
    }

    // clans

    @Override
    public Collection<Clan> clans() {
        return new ArrayList<>(clans);
    }

    @Override
    public Optional<Clan> findClan(@NotNull String name) {
        return clans.stream()
                .filter(c -> c.name().equalsIgnoreCase(name))
                .findFirst().map(c -> c);
    }

    @Override
    public Optional<Clan> findClan(@NotNull UUID id) {
        return clans.stream()
                .filter(c -> c.id().equals(id))
                .findFirst().map(c -> c);
    }

    @Override
    public Optional<Clan> findClanByTag(@NotNull String tag) {
        return clans.stream()
                .filter(c -> c.tag().equalsIgnoreCase(tag))
                .findFirst().map(c -> c);
    }

    // edit clans

    @Override
    public CompletableFuture<Clan> create(@NotNull Profile leader, @NotNull String name, @NotNull String tag) {
        if (name.length() < 3) {
            throw new IllegalArgumentException("Clan name must be at least 3 characters.");
        }
        if (tag.length() < 2 || tag.length() > 3) {
            throw new IllegalArgumentException("Clan tag must be exactly 2 or 3 characters.");
        }
        if (clans.stream().anyMatch(c -> c.name().equalsIgnoreCase(name)
                || c.tag().equalsIgnoreCase(tag))) {
            throw new IllegalArgumentException("A clan with that name or tag already exists.");
        }

        logger.debug("Created new clan '" + name + "' with tag '" + tag + "'.");

        DClan clan = new DClan(name, tag);
        clans.add(clan);

        return databaseContext.persistAsync(clan).thenCompose(n -> {
            // TODO call create event

            ((DProfile) leader).joinClan(clan);
            ((DClanProfile) leader.clanProfile().orElseThrow()).leader = true;

            return update(leader);
        }).thenApply(n -> clan);
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Clan clan) {
        logger.debug("Deleting clan '" + clan.name() + "'.");
        clans.remove((DClan) clan);

        Set<CompletableFuture<?>> futures = new HashSet<>();

        // online players will leave clan
        profiles.stream().filter(p -> p.clanProfile().map(cp -> cp.clan().equals(clan)).orElse(false))
                .forEach(p -> {
                    p.clanProfile().get().quit();
                    futures.add(update(p));
                });

        // TODO consistency check

        return databaseContext.removeAsync(clan).thenCompose(n ->
                CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)));
    }

    @Override
    public CompletableFuture<Void> update(@NotNull Clan clan) {
        return databaseContext.mergeAsync(clan).thenRun(() -> {
        });
    }

    // profiles

    private CompletableFuture<Profile> findProfileBy(Predicate<Profile> predicate, String field, Object value) {
        Profile profile = profiles.stream().filter(predicate).findFirst().orElse(null);
        if (profile != null) {
            return CompletableFuture.completedFuture(profile);
        }
        return databaseContext.findAllWhereAsync(DProfile.class, field, value).thenCompose(pl -> {
            if (!pl.isEmpty()) {
                return CompletableFuture.completedFuture(pl.get(0));
            }
            return CompletableFuture.failedFuture(new NullPointerException("Profile does not exist."));
        });
    }

    @Override
    public CompletableFuture<List<Profile>> profiles(@NotNull Clan clan) {
        return databaseContext.findAllWhereAsync(DProfile.class, (cb, root) -> {
                    return cb.equal(root.get("clanProfile").get("clan"), clan);
                })
                .thenCompose(CompletableFuture::completedFuture)
                .thenApply(list -> list.stream().map(p -> (Profile) p).toList());
    }

    @Override
    public CompletableFuture<Profile> findProfile(@NotNull UUID id) {
        return findProfileBy(p -> p.id().equals(id), "id", id);
    }

    @Override
    public CompletableFuture<Profile> findProfile(@NotNull String name) {
        return findProfileBy(p -> p.name().equalsIgnoreCase(name), "name", name);
    }

    @Override
    public Optional<Profile> findCachedProfile(@NotNull UUID id) {
        return profiles.stream().filter(p -> p.id().equals(id)).map(p -> (Profile) p).findFirst();
    }

    @Override
    public Collection<Profile> cachedProfiles() {
        return Collections.unmodifiableSet(profiles);
    }

    // edit profiles

    public CompletableFuture<Profile> load(@NotNull UUID id, @NotNull String name) {
        return findProfile(id).thenApply(p -> {
            // update name change
            ((DProfile) p).setName(name);
            databaseContext.mergeAsync(p);
            return p;
        }).exceptionally(ex -> {
            // not found, create new profile
            DProfile profile = new DProfile(id, name);
            databaseContext.persistAsync(profile);
            return profile;
        }).thenApply(p -> {
            // add to cache
            DProfile dp = (DProfile) p;
            dp.setLastSeenAt(Instant.now());
            profiles.add(dp);

            // TODO call events
            return p;
        });
    }

    public void unload(@NotNull UUID id) {
        profiles.removeIf(p -> p.id().equals(id));
    }

    @Override
    public CompletableFuture<Void> update(@NotNull Profile profile) {
        return databaseContext.mergeAsync(profile).thenRun(() -> {
        });
    }

    @Override
    public CompletableFuture<Void> update(@NotNull ClanProfile clanProfile) {
        return databaseContext.mergeAsync(clanProfile).thenRun(() -> {
        });
    }

    //


    @Override
    public Collection<SigilType> sigilTypes() {
        return Collections.unmodifiableSet(sigilTypes);
    }

    public CompletableFuture<SigilType> addSigilType(@NotNull String name, @NotNull String data, boolean restricted) {
        DSigilType pattern = new DSigilType(name, data, restricted);
        return databaseContext.persistAsync(pattern).thenApply(n -> {
            sigilTypes.add(pattern);
            return pattern;
        });
    }

    @Override
    public CompletableFuture<Void> removeSigilType(@NotNull SigilType sigilType) {
        sigilTypes.remove((DSigilType) sigilType);
        return databaseContext.removeAsync(sigilType);
    }
}
