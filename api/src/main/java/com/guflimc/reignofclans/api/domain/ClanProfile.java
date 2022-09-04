package com.guflimc.reignofclans.api.domain;

import java.time.Instant;

public interface ClanProfile {

    Profile profile();

    Clan clan();

    Instant createdAt();

    boolean isActive();

    void setActive(boolean active);

    float power();

    void setPower(float power);

}
