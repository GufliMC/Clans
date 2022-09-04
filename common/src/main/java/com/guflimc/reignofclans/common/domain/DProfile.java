package com.guflimc.reignofclans.common.domain;

import com.guflimc.reignofclans.api.domain.ClanProfile;
import com.guflimc.reignofclans.api.domain.Profile;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class DProfile implements Profile {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToOne(targetEntity = ClanProfile.class)
    private DClanProfile clanProfile;

    private Instant lastSeenAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    //

    private DProfile() {}

    public DProfile(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Optional<ClanProfile> clanProfile() {
        return Optional.ofNullable(clanProfile);
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public Instant lastSeenAt() {
        return lastSeenAt;
    }

    @Override
    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}