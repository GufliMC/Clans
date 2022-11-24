package com.guflimc.clans.spigot.listeners;

import com.guflimc.brick.chat.spigot.api.event.SpigotPlayerChannelChatEvent;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.spigot.chat.ClanChatChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(SpigotPlayerChannelChatEvent event) {
        if ( !(event.chatChannel() instanceof ClanChatChannel) ) {
            return;
        }

        Clan clan = ClanAPI.get().findCachedProfile(event.player().getUniqueId())
                .flatMap(Profile::clanProfile).orElseThrow().clan();

        event.recipients().removeIf(p ->
                !ClanAPI.get().findCachedProfile(p.getUniqueId())
                .flatMap(Profile::clanProfile).orElseThrow()
                .clan().equals(clan));
    }

}
