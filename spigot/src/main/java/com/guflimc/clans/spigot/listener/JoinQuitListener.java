package com.guflimc.clans.spigot.listener;

import com.guflimc.clans.spigot.SpigotClans;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final SpigotClans lavaClans;


    public JoinQuitListener(SpigotClans lavaClans) {
        this.lavaClans = lavaClans;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        lavaClans.clanManager.load(event.getUniqueId(), event.getName()).join();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        lavaClans.clanManager.unload(event.getPlayer().getUniqueId());
    }

}
