package com.guflimc.clans.spigot.api.events;

import com.guflimc.clans.api.domain.Clan;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClanDeleteEvent extends ClanEvent {

    public ClanDeleteEvent(Clan clan, boolean async) {
        super(clan, async);
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
