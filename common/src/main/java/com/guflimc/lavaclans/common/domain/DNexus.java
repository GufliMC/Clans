package com.guflimc.lavaclans.common.domain;

import com.guflimc.brick.maths.api.geo.Location;
import com.guflimc.brick.maths.database.api.LocationConverter;
import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.nexus;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "nexuses")
public class DNexus implements nexus {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @OneToOne(targetEntity = DClan.class, optional = false)
    private DClan clan;

    @Convert(converter = LocationConverter.class)
    @Column(nullable = false)
    private Location location = new Location(null, 0, 0, 0, 0, 0);

    @ColumnDefault("1")
    private int level = 1;

    private Instant shieldExpireAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    //

    public DNexus() {
    }

    public DNexus(Clan clan, Location location) {
        this.clan = (DClan) clan;
        this.location = location;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public Clan clan() {
        return clan;
    }

    @Override
    public int level() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean hasShield() {
        return shieldExpireAt != null && Instant.now().isBefore(shieldExpireAt);
    }

    @Override
    public Instant shieldExpireAt() {
        return shieldExpireAt;
    }

    @Override
    public void activateShield(Instant expireAt) {
        this.shieldExpireAt = expireAt;
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }
}