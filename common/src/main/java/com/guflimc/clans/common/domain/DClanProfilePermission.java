package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.domain.ClanPermission;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.ClanProfilePermission;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clan_profile_permissions")
public class DClanProfilePermission implements ClanProfilePermission {

    @Id
    private UUID id;

    @ManyToOne(targetEntity = DClanProfile.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DClanProfile clanProfile;

    @Enumerated(EnumType.STRING)
    private ClanPermission permission;

    @WhenCreated
    private Instant createdAt;

    @WhenModified
    private Instant updatedAt;

    //

    private DClanProfilePermission() {
    }

    public DClanProfilePermission(DClanProfile clanProfile, ClanPermission permission) {
        this.clanProfile = clanProfile;
        this.permission = permission;
    }

    @Override
    public ClanProfile clanProfile() {
        return clanProfile;
    }

    @Override
    public ClanPermission permission() {
        return permission;
    }

}