package com.guflimc.clans.api.domain;

import java.util.UUID;

public interface ClanInvite {

    UUID id();

    Profile sender();

    Profile target();

    Clan clan();

    void reject();

    void accept();

    void cancel();

    boolean isExpired();

    boolean isRejected();

    boolean isAccepted();

    boolean isCancelled();

    default boolean isAnswered() {
        return isRejected() || isAccepted();
    }

    default boolean isActive() {
        return !isAnswered() && !isExpired() && !isCancelled();
    }

}
