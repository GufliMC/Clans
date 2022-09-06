package com.guflimc.lavaclans.common.domain;

import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.ClanInvite;
import com.guflimc.lavaclans.api.domain.ClanProfile;
import com.guflimc.lavaclans.api.domain.Profile;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "profiles")
public class DProfile implements Profile {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToOne(targetEntity = DClanProfile.class, orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    @JoinColumn(foreignKey = @ForeignKey(foreignKeyDefinition =
            "foreign key (clan_profile_id) references clan_profiles (id) on delete set null"))
    DClanProfile clanProfile;

    @OneToMany(targetEntity = DClanInvite.class, mappedBy = "target", fetch = FetchType.EAGER, orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    private List<DClanInvite> invites = new ArrayList<>();

    @OneToMany(targetEntity = DClanInvite.class, mappedBy = "sender", fetch = FetchType.EAGER, orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    List<DClanInvite> sentInvites = new ArrayList<>();

    private Instant lastSeenAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    //

    private DProfile() {
    }

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

    public void joinClan(Clan clan) {
        // leave previous clan
        clanProfile().ifPresent(ClanProfile::quit);

        // join new clan
        clanProfile = new DClanProfile(this, (DClan) clan);

        // TODO events
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
    public ClanInvite addInvite(@NotNull Profile sender, @NotNull Clan clan) {
        DClanInvite invite = new DClanInvite((DProfile) sender, this, (DClan) clan);
        invites.add(invite);
        return invite;
    }

    @Override
    public Optional<ClanInvite> mostRecentInvite(@NotNull Clan clan) {
        return invites.stream().filter(inv -> inv.clan().equals(clan))
                .max(Comparator.comparing(DClanInvite::createdAt))
                .map(inv -> inv);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DProfile other && other.id.equals(id);
    }
}