package com.guflimc.lavaclans.spigot.listener;

import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsMoveEvent;
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

public class RegionEnterLeaveListener implements Listener {

    private final LavaClans plugin;

    private final Title.Times defaultTimes = Title.Times.times(
            Duration.of(250, ChronoUnit.MILLIS),
            Duration.of(1500, ChronoUnit.MILLIS),
            Duration.of(250, ChronoUnit.MILLIS)
    );

    public RegionEnterLeaveListener(LavaClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerRegionsMoveEvent event) {
        Audience audience = plugin.adventure.player(event.player());
        if (event.to().isEmpty()) {
            // Player walks into the wilderness
            audience.sendTitlePart(TitlePart.TITLE, Component.text("Wilderness", NamedTextColor.GREEN));
            audience.sendTitlePart(TitlePart.TIMES, defaultTimes);
            return;
        }

        event.uniqueTo().stream()
                .filter(rg -> rg instanceof Nexus)
                .map(rg -> (Nexus) rg)
                .findFirst().ifPresent(nexus -> {
                    audience.sendTitlePart(TitlePart.TITLE, Component.text(nexus.clan().name(), TextColor.color(nexus.clan().rgbColor())));
                    audience.sendTitlePart(TitlePart.TIMES, defaultTimes);
                });
    }

}