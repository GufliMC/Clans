package com.guflimc.lavaclans.spigot.listener;

import com.guflimc.lavaclans.api.domain.Profile;
import com.guflimc.lavaclans.spigot.SpigotLavaClans;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class JoinQuitListener implements Listener {

    private final SpigotLavaClans lavaClans;

    private final Map<UUID, CompletableFuture<Profile>> loading = new ConcurrentHashMap<>();

    public JoinQuitListener(SpigotLavaClans lavaClans) {
        this.lavaClans = lavaClans;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if ( event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED ) {
            return;
        }

        loading.put(event.getUniqueId(), lavaClans.manager.load(event.getUniqueId(), event.getName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        CompletableFuture<Profile> future = loading.remove(event.getPlayer().getUniqueId());
        if ( future == null ) {
            System.err.println("Player joined before plugin data was loaded.");
            event.getPlayer().kickPlayer(ChatColor.RED + "A fatal error occured. Try to reconnect.");
            return; // race condition, player is loaded on the server before plugin data is loaded
        }

        future.thenRun(() -> {
            // TODO call join events
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        loading.remove(event.getPlayer().getUniqueId());
        lavaClans.manager.unload(event.getPlayer().getUniqueId());
    }

}
