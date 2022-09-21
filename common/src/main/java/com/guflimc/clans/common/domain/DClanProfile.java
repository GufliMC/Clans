package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanPermission;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.Profile;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clan_profiles")
public class DClanProfile implements ClanProfile {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DProfile profile;

    @ManyToOne(targetEntity = DClan.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DClan clan;

    @ColumnDefault("false")
    public boolean leader;

    @OneToMany(targetEntity = DClanProfilePermission.class, orphanRemoval = true, mappedBy = "clanProfile", fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<DClanProfilePermission> permissions = new ArrayList<>();

    @ColumnDefault("true")
    private boolean active;

    @ColumnDefault("0")
    private float power;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    //

    private DClanProfile() {
    }

    public DClanProfile(DProfile profile, DClan clan) {
        this.profile = profile;
        this.clan = clan;
    }

    @Override
    public Profile profile() {
        return profile;
    }

    @Override
    public Clan clan() {
        return clan;
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public float power() {
        return power;
    }

    @Override
    public void setPower(float power) {
        this.power = power;
    }

    @Override
    public void quit() {
        clan.memberCount--;

        profile.clanProfile = null;
        profile.sentInvites.clear();

        this.active = false;
        ClanAPI.get().update(this);

        // TODO events
    }

    @Override
    public boolean isLeader() {
        return leader;
    }

    @Override
    public boolean hasPermission(ClanPermission permission) {
        return leader || permissions.stream().anyMatch(p -> p.permission().equals(permission));
    }

    @Override
    public void addPermission(ClanPermission permission) {
        if ( hasPermission(permission) ) {
            return;
        }
        permissions.add(new DClanProfilePermission(this, permission));
    }

    @Override
    public void removePermission(ClanPermission permission) {
        permissions.removeIf(p -> p.permission().equals(permission));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DClanProfile other && other.id.equals(id);
    }
}