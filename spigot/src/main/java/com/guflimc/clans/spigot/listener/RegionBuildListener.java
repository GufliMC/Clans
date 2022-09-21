package com.guflimc.clans.spigot.listener;

import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsBuildEvent;
import com.guflimc.clans.api.domain.Nexus;
import com.guflimc.clans.spigot.SpigotClans;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegionBuildListener implements Listener {

    private final SpigotClans plugin;

    public RegionBuildListener(SpigotClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBuild(PlayerRegionsBuildEvent event) {
        Audience audience = plugin.adventure.player(event.player());

        event.regions().stream()
                .filter(rg -> rg instanceof Nexus)
                .map(rg -> (Nexus) rg)
                .findFirst().ifPresent(nexus -> {
                    double dist = nexus.location().distanceSquared(SpigotMaths.toBrickLocation(event.block().getLocation().add(0.5, 0.5, 0.5)));
                    if ( dist <= 3 ) {
                        event.setCancelled(true);
                        audience.sendMessage(Component.text("You cannot build near your nexus!", NamedTextColor.RED));
                    }
                });
    }

}
