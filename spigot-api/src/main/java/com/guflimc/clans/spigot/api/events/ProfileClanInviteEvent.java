package com.guflimc.clans.spigot.api.events;

import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Profile;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ProfileClanInviteEvent extends ProfileClanEvent {

    public ProfileClanInviteEvent(Clan clan, Profile profile, boolean async) {
        super(clan, profile, async);
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
