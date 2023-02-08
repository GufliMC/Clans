package com.guflimc.clans.spigot;

import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.common.EventManager;
import com.guflimc.clans.spigot.api.events.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.function.Supplier;

public class SpigotEventManager extends EventManager {

    private void wrap(Supplier<Event> supplier) {
        try {
            Bukkit.getServer().getPluginManager().callEvent(supplier.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Clan clan) {
        wrap(() -> new ClanCreateEvent(clan, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onDelete(Clan clan) {
        wrap(() -> new ClanDeleteEvent(clan, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onJoin(Profile profile, Clan clan) {
        wrap(() -> new ProfileClanJoinEvent(clan, profile, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onLeave(Profile profile, Clan clan) {
        wrap(() -> new ProfileClanLeaveEvent(clan, profile, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onInvite(Profile profile, Clan clan) {
        wrap(() -> new ProfileClanInviteEvent(clan, profile, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onInviteDelete(Profile profile, Clan clan) {
        wrap(() -> new ProfileClanInviteDeleteEvent(clan, profile, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onInviteReject(Profile profile, Clan clan) {
        wrap(() -> new ProfileClanInviteRejectEvent(clan, profile, !Bukkit.isPrimaryThread()));
    }

}
