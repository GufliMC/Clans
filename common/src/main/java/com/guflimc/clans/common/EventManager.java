package com.guflimc.clans.common;

import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Profile;

public abstract class EventManager {

    public static EventManager INSTANCE;

    //

    public abstract void onCreate(Clan clan);

    public abstract void onDelete(Clan clan);

    public abstract void onJoin(Profile profile, Clan clan);

    public abstract void onLeave(Profile profile, Clan clan);

    public abstract void onInvite(Profile profile, Clan clan);

    public abstract void onInviteDelete(Profile profile, Clan clan);

    public abstract void onInviteReject(Profile profile, Clan clan);

}
