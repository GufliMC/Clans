package com.guflimc.clans.api.domain;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public interface ClanProfile {

    Profile profile();

    Clan clan();

    void quit();

    boolean isLeader();

    boolean hasPermission(@NotNull ClanPermission permission);

    void addPermission(@NotNull ClanPermission permission);

    void removePermission(@NotNull ClanPermission permission);

    Instant createdAt();

}
