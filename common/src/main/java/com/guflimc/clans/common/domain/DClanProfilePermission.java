package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.domain.ClanProfile;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clan_profile_permissions")
public class DClanProfilePermission {

    @Id
    private UUID id;

    @ManyToOne(targetEntity = DClanProfile.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DClanProfile clanProfile;

    @Convert
    private String key;

    @WhenCreated
    private Instant createdAt;

    @WhenModified
    private Instant updatedAt;

    //

    private DClanProfilePermission() {
    }

    public DClanProfilePermission(DClanProfile clanProfile, String key) {
        this.clanProfile = clanProfile;
        this.key = key;
    }

    public ClanProfile clanProfile() {
        return clanProfile;
    }

    public String key() {
        return key;
    }

}