package com.guflimc.clans.spigot.api;

import com.guflimc.clans.api.ClanManager;
import com.guflimc.clans.api.domain.SigilType;
import org.bukkit.block.banner.PatternType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SpigotClanManager extends ClanManager {

    CompletableFuture<SigilType> addSigilType(@NotNull String name, @NotNull List<PatternType> pattern,
                                              boolean restricted);

    CompletableFuture<SigilType> addSigilType(@NotNull String name, @NotNull List<PatternType> foreground,
                                              @NotNull List<PatternType> negative, boolean restricted);

}
