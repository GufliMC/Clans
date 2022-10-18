package com.guflimc.clans.spigot.listener;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsBlockBreakEvent;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsBlockPlaceEvent;
import com.guflimc.clans.api.AttackAPI;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.*;
import com.guflimc.clans.spigot.SpigotClans;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.Optional;

public class RegionBuildListener implements Listener {

    private final SpigotClans plugin;

    public RegionBuildListener(SpigotClans plugin) {
        this.plugin = plugin;
    }

    private Optional<Nexus> nexus(Collection<Region> regions) {
        return regions.stream()
                .filter(rg -> rg instanceof Nexus)
                .map(rg -> (Nexus) rg)
                .findFirst();
    }

    @EventHandler
    public void onPlace(PlayerRegionsBlockPlaceEvent event) {
        nexus(event.regions()).ifPresent(nexus ->
                build(event.player(), event.block(), nexus, event));
    }

    @EventHandler
    public void onBreak(PlayerRegionsBlockBreakEvent event) {
        nexus(event.regions()).ifPresent(nexus -> {
            if (SpigotMaths.toSpigotLocation(nexus.location()).getBlock().equals(event.block())) {
                breakNexus(event.player(), event.block(), nexus);
                event.setCancelled(true);
                return;
            }

            build(event.player(), event.block(), nexus, event);
        });
    }

    private void build(Player player, Block block, Nexus nexus, Cancellable event) {
        Location bloc = SpigotMaths.toBrickLocation(block.getLocation().add(0.5, 0.5, 0.5));
        double dist = nexus.location().distanceSquared(bloc);

        if (dist <= 3) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(player, "protection.nexus.build.too-close");
        }

        // TODO check for player clan and use power
    }

    private void breakNexus(Player player, Block block, Nexus nexus) {
        player.sendMessage("You just broke a nexus.");

        Clan clan = ClanAPI.get().findCachedProfile(player.getUniqueId()).flatMap(Profile::clanProfile).map(ClanProfile::clan).orElse(null);
        if ( clan == null ) {
            return; // players without a clan cannot attack
        }

        if ( clan.equals(nexus.clan()) ) {
            return; // can't attack your own nexus
        }

        plugin.attackManager.attack(player, clan, nexus, block);
    }

}
