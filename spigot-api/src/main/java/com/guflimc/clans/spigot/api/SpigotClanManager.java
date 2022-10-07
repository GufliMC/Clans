package com.guflimc.clans.spigot.api;

import com.guflimc.clans.api.ClanManager;
import com.guflimc.clans.api.domain.Clan;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

public interface SpigotClanManager extends ClanManager {

    Collection<Player> onlinePlayers(Clan clan);

    Optional<Clan> clan(Player player);

}
