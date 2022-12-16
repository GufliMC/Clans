package com.guflimc.clans.spigot.api.events;

import com.guflimc.clans.api.domain.Clan;
import org.bukkit.event.Event;

public abstract class ClanEvent extends Event {

    private final Clan clan;

    public ClanEvent(Clan clan) {
        this.clan = clan;
    }

    public Clan clan() {
        return clan;
    }

}
