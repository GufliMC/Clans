package com.guflimc.lavaclans.spigot.listener;

import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsBuildEvent;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsMoveEvent;
import com.guflimc.lavaclans.api.attributes.RegionAttributes;
import com.guflimc.lavaclans.api.domain.Nexus;
import com.guflimc.lavaclans.spigot.LavaClans;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class RegionBuildListener implements Listener {

    private final LavaClans plugin;

    public RegionBuildListener(LavaClans plugin) {
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
                    System.out.println(dist);
                    if ( dist <= 3 ) {
                        event.setCancelled(true);
                        audience.sendMessage(Component.text("You cannot build near your nexus!", NamedTextColor.RED));
                    }
                });
    }

}
