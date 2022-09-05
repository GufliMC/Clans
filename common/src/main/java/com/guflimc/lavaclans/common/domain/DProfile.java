package com.guflimc.lavaclans.common.domain;

import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.ClanProfile;
import com.guflimc.lavaclans.api.domain.Profile;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

    @OneToOne(targetEntity = DClanProfile.class, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private DClanProfile clanProfile;

    @OneToMany(targetEntity = DClanInvite.class, mappedBy = "target", fetch = FetchType.EAGER, orphanRemoval = true,
            cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private List<DClanInvite> invites = new ArrayList<>();

    @OneToMany(targetEntity = DClanInvite.class, mappedBy = "sender", fetch = FetchType.EAGER, orphanRemoval = true,
            cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private List<DClanJoinRequest> joinRequests = new ArrayList<>();

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

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Optional<ClanProfile> clanProfile() {
        return Optional.ofNullable(clanProfile);
    }

    public void setClanProfile(ClanProfile profile) {
        this.clanProfile = (DClanProfile) profile;
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public Instant lastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    @Override
    public void addJoinRequest(Clan clan) {
        // TODO call events

        joinRequests.add(new DClanJoinRequest(this, clan));
    }

    @Override
    public void addInvite(Profile sender, Clan clan) {
        // TODO call events

        invites.add(new DClanInvite(sender, this, clan));
    }
}