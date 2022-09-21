package com.guflimc.clans.api.domain;

import java.time.Instant;

public interface ClanProfile {

    Profile profile();

    Clan clan();

    void quit();

    boolean isLeader();

    float power();

    void setPower(float power);

    boolean hasPermission(ClanPermission permission);

    void addPermission(ClanPermission permission);

    void removePermission(ClanPermission permission);

    Instant createdAt();

}
