package com.guflimc.lavaclans.common.domain;

import com.guflimc.lavaclans.api.domain.ClanPermission;
import com.guflimc.lavaclans.api.domain.ClanProfile;
import com.guflimc.lavaclans.api.domain.ClanProfilePermission;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clan_profile_permissions")
public class DClanProfilePermission implements ClanProfilePermission {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @ManyToOne(targetEntity = DClanProfile.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DClanProfile clanProfile;

    @Enumerated(EnumType.STRING)
    private ClanPermission permission;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
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

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DClanProfilePermission other && other.id.equals(id);
    }
}