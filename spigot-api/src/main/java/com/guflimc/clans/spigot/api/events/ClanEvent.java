package com.guflimc.clans.spigot.api.events;

import com.guflimc.clans.api.domain.Clan;
import org.bukkit.event.Event;

public abstract class ClanEvent extends Event {

    private final Clan clan;

    public ClanEvent(Clan clan, boolean async) {
        super(async);
        this.clan = clan;
    }

    public Clan clan() {
        return clan;
    }

}
