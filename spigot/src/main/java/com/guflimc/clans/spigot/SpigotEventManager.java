package com.guflimc.clans.spigot;

import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.common.EventManager;
import com.guflimc.clans.spigot.api.events.*;
import org.bukkit.Bukkit;

public class SpigotEventManager extends EventManager {

    @Override
    public void onCreate(Clan clan) {
        Bukkit.getServer().getPluginManager().callEvent(new ClanCreateEvent(clan));
    }

    @Override
    public void onDelete(Clan clan) {
        Bukkit.getServer().getPluginManager().callEvent(new ClanDeleteEvent(clan));
    }

    @Override
    public void onJoin(Profile profile, Clan clan) {
        Bukkit.getServer().getPluginManager().callEvent(new ProfileClanJoinEvent(clan, profile));
    }

    @Override
    public void onLeave(Profile profile, Clan clan) {
        Bukkit.getServer().getPluginManager().callEvent(new ProfileClanLeaveEvent(clan, profile));
    }

    @Override
    public void onInvite(Profile profile, Clan clan) {
        Bukkit.getServer().getPluginManager().callEvent(new ProfileClanInviteEvent(clan, profile));
    }

    @Override
    public void onInviteDelete(Profile profile, Clan clan) {
        Bukkit.getServer().getPluginManager().callEvent(new ProfileClanInviteDeleteEvent(clan, profile));
    }

    @Override
    public void onInviteReject(Profile profile, Clan clan) {
        Bukkit.getServer().getPluginManager().callEvent(new ProfileClanInviteRejectEvent(clan, profile));
    }

}
