package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanInvite;
import com.guflimc.clans.api.domain.Profile;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbDefault;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "clan_invites")
public class DClanInvite implements ClanInvite {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DProfile sender;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DProfile target;

    @ManyToOne(targetEntity = DClan.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DClan clan;

    @DbDefault("false")
    private boolean rejected;

    @DbDefault("false")
    private boolean accepted;

    @WhenCreated
    private Instant createdAt = Instant.now();

    //

    public DClanInvite() {
    }

    public DClanInvite(DProfile sender, DProfile target, DClan clan) {
        this.sender = sender;
        this.target = target;
        this.clan = clan;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public Profile sender() {
        return sender;
    }

    @Override
    public Profile target() {
        return target;
    }

    @Override
    public Clan clan() {
        return clan;
    }

    @Override
    public void reject() {
        this.rejected = true;
    }

    @Override
    public void accept() {
        if (!isValid()) {
            throw new IllegalStateException("This invite is not valid.");
        }

        this.accepted = true;
        target.joinClan(clan);
    }

    @Override
    public boolean isValid() {
        return !rejected && !isExpired();
    }

    @Override
    public boolean isExpired() {
        return Instant.now().isAfter(createdAt.plus(24, ChronoUnit.HOURS));
    }

    Instant createdAt() {
        return createdAt;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DClanInvite other && other.id.equals(id);
    }

}