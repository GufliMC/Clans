package com.guflimc.reignofclans.common.domain;

import com.guflimc.reignofclans.api.domain.Clan;
import com.guflimc.reignofclans.api.domain.ClanProfile;
import com.guflimc.reignofclans.api.domain.Profile;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
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

    @OneToOne(targetEntity = DProfile.class, optional = false)
    private DProfile profile;

    @OneToOne(targetEntity = DClan.class, optional = false)
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

    private DClanProfile() {}

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
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public float power() {
        return power;
    }

    @Override
    public void setPower(float power) {
        this.power = power;
    }
}