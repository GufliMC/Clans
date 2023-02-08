package com.guflimc.clans.spigot.api.events;

import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Profile;

public abstract class ProfileClanEvent extends ClanEvent {

    private final Profile profile;

    public ProfileClanEvent(Clan clan, Profile profile, boolean async) {
        super(clan, async);
        this.profile = profile;
    }

    public Profile profile() {
        return profile;
    }

}
