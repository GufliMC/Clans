package com.guflimc.lavaclans.common.domain;

import com.guflimc.lavaclans.api.ClanAPI;
import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.ClanProfile;
import com.guflimc.lavaclans.api.domain.Profile;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
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
        profile.clanProfile = null;
        profile.sentInvites.clear();

        this.active = false;
        ClanAPI.get().update(this);

        // TODO events
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