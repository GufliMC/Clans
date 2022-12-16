package com.guflimc.clans.spigot.api.events;

import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Profile;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ProfileClanLeaveEvent extends ProfileClanEvent {

    public ProfileClanLeaveEvent(Clan clan, Profile profile) {
        super(clan, profile);
    }

    //

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
