package com.guflimc.clans.api.domain;

import java.util.UUID;

public interface ClanInvite {

    UUID id();

    Profile sender();

    Profile target();

    Clan clan();

    void reject();

    void accept();

    boolean isExpired();

    boolean isRejected();

    boolean isAccepted();

    default boolean isAnswered() {
        return isRejected() || isAccepted();
    }

}
