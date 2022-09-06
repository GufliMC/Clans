package com.guflimc.lavaclans.api.domain;

import java.time.Instant;

public interface ClanProfile {

    Profile profile();

    Clan clan();

    Instant createdAt();

    float power();

    void setPower(float power);

    void quit();

}
