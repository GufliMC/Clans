package com.guflimc.clans.spigot.listener;

import com.guflimc.brick.chat.spigot.api.event.SpigotPlayerChannelChatEvent;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.spigot.chat.ClanChatChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(SpigotPlayerChannelChatEvent event) {
        if ( !(event.chatChannel() instanceof ClanChatChannel) ) {
            return;
        }

        Clan clan = ClanAPI.get().findCachedProfile(event.player().getUniqueId()).clanProfile().get().clan();
        event.recipients().removeIf(p -> !ClanAPI.get().findCachedProfile(p.getUniqueId()).clanProfile().get().clan().equals(clan));
    }

}
