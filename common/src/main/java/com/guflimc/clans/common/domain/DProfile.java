package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanInvite;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.Profile;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.*;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "profiles")
public class DProfile implements Profile {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToOne(targetEntity = DClanProfile.class, orphanRemoval = true, cascade = CascadeType.ALL)
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    DClanProfile clanProfile;

    @OneToMany(targetEntity = DClanInvite.class, mappedBy = "target", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DClanInvite> invites = new ArrayList<>();

    @OneToMany(targetEntity = DClanInvite.class, mappedBy = "sender", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    List<DClanInvite> sentInvites = new ArrayList<>();

    @DbDefault("0")
    private int power = 0;

    @DbDefault("0")
    private long playTime = 0;

    private Instant lastSeenAt;

    @WhenCreated
    private Instant createdAt;

    @WhenModified
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
        ((DClan) clan).memberCount++;

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
    public int power() {
        return power;
    }

    @Override
    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public long playTime() {
        return playTime;
    }

    @Override
    public void setPlayTime(long playTime) {
        this.playTime = playTime;
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