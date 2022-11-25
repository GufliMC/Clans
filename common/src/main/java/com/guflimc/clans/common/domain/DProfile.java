package com.guflimc.clans.common.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;
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

    @OneToOne(targetEntity = DClanProfile.class, cascade = CascadeType.ALL)
    @Where(clause = "active = 1")
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    DClanProfile clanProfile;

    @OneToMany(targetEntity = DClanInvite.class, mappedBy = "target", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DClanInvite> invites = new ArrayList<>();

    @OneToMany(targetEntity = DClanInvite.class, mappedBy = "sender", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    List<DClanInvite> sentInvites = new ArrayList<>();

    @OneToMany(targetEntity = DProfileAttribute.class, mappedBy = "profile",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DProfileAttribute> attributes = new ArrayList<>();

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

    // invites

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

    // attributes

    @Override
    public <T> void setAttribute(ProfileAttributeKey<T> key, T value) {
        if (value == null) {
            removeAttribute(key);
            return;
        }

        DAttribute attribute = attributes.stream()
                .filter(attr -> attr.name().equals(key.name()))
                .findFirst().orElse(null);

        if ( attribute == null ) {
            attributes.add(new DProfileAttribute(this, key, value));
            return;
        }

        attribute.setValue(key, value);
    }

    @Override
    public <T> void removeAttribute(ProfileAttributeKey<T> key) {
        attributes.removeIf(attr -> attr.name().equals(key.name()));
    }

    @Override
    public <T> Optional<T> attribute(ProfileAttributeKey<T> key) {
        return attributes.stream().filter(attr -> attr.name().equals(key.name()))
                .findFirst().map(ra -> ra.value(key));
    }

    // timestamps

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

}