package com.guflimc.clans.spigot.api;

import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.ClanManager;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface SpigotClanManager extends ClanManager {

    default Collection<Player> onlinePlayers(@NotNull Clan clan) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> ClanAPI.get().findCachedProfile(p.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(ClanProfile::clan)
                        .filter(c -> c.equals(clan))
                        .isPresent()
                ).map(Player.class::cast).toList();
    }

    default Optional<Clan> findClan(@NotNull Player player) {
        return ClanAPI.get()
                .findCachedProfile(player.getUniqueId())
                .flatMap(Profile::clanProfile)
                .map(ClanProfile::clan);
    }

    default Optional<ClanProfile> clanProfile(@NotNull Player player) {
        return ClanAPI.get().findCachedProfile(player.getUniqueId())
                .flatMap(Profile::clanProfile);
    }

    ItemStack crest(@NotNull Clan clan);

}
