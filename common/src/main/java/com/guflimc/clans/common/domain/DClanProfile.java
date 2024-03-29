package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanPermission;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.common.EventManager;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.*;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clan_profiles")
public class DClanProfile implements ClanProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DProfile profile;

    @ManyToOne(targetEntity = DClan.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DClan clan;

    @DbDefault("false")
    public boolean leader = false;

    @OneToMany(targetEntity = DClanProfilePermission.class, orphanRemoval = true, mappedBy = "clanProfile", fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<DClanProfilePermission> permissions = new ArrayList<>();

    @DbDefault("true")
    private boolean active = true;

    @WhenCreated
    private Instant createdAt;

    @WhenModified
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
    public void quit() {
        clan.memberCount--;

        profile.clanProfile = null;
        profile.sentInvites.clear();

        this.active = false;
        ClanAPI.get().update(this);

        EventManager.INSTANCE.onLeave(profile, clan);
    }

    @Override
    public boolean isLeader() {
        return leader;
    }

    @Override
    public boolean hasPermission(@NotNull ClanPermission permission) {
        return leader || permissions.stream().anyMatch(p -> p.key().equals(permission.key()));
    }

    @Override
    public void addPermission(@NotNull ClanPermission permission) {
        if (hasPermission(permission)) {
            return;
        }
        permissions.add(new DClanProfilePermission(this, permission.key()));
    }

    @Override
    public void removePermission(@NotNull ClanPermission permission) {
        permissions.removeIf(p -> p.key().equals(permission.key()));
    }

}