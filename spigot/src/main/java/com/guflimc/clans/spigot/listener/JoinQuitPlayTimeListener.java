package com.guflimc.clans.spigot.listener;

import com.guflimc.brick.scheduler.api.Scheduler;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.spigot.SpigotClans;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class JoinQuitPlayTimeListener implements Listener {

    private final SpigotClans plugin;

    private final Map<Profile, Instant> tracker = new ConcurrentHashMap<>();

    public JoinQuitPlayTimeListener(SpigotClans plugin, Scheduler scheduler) {
        this.plugin = plugin;

        scheduler.asyncRepeating(() -> {
            tracker.keySet().forEach(profile -> {
                Duration diff = Duration.between(tracker.get(profile), Instant.now());
                profile.setPlayTime(profile.playTime() + (int) diff.getSeconds());
                plugin.clanManager.update(profile);
                tracker.put(profile, Instant.now());
            });
        }, 1, TimeUnit.MINUTES);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Profile profile = plugin.clanManager.findCachedProfile(event.getPlayer().getUniqueId()).orElseThrow();
        tracker.put(profile, Instant.now());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Profile profile = plugin.clanManager.findCachedProfile(event.getPlayer().getUniqueId()).orElseThrow();
        Duration diff = Duration.between(tracker.remove(profile), Instant.now());
        profile.setPlayTime(profile.playTime() + (int) diff.getSeconds());
        plugin.clanManager.update(profile);
    }

}
