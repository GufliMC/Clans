package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanInvite;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.common.EventManager;
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

    @DbDefault("false")
    private boolean cancelled;

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
        if ( isAnswered() ) {
            throw new IllegalStateException("This invite is already answered.");
        }

        this.rejected = true;
        EventManager.INSTANCE.onInviteReject(target, clan);
    }

    @Override
    public void accept() {
        if ( isAnswered() ) {
            throw new IllegalStateException("This invite is already answered.");
        }

        this.accepted = true;
        target.join(clan);
    }

    @Override
    public void cancel() {
        if ( isAnswered() ) {
            throw new IllegalStateException("This invite is already answered.");
        }

        this.cancelled = true;
        EventManager.INSTANCE.onInviteDelete(target, clan);
    }

    @Override
    public boolean isExpired() {
        return Instant.now().isAfter(createdAt.plus(24, ChronoUnit.HOURS));
    }

    @Override
    public boolean isRejected() {
        return rejected;
    }

    @Override
    public boolean isAccepted() {
        return accepted;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }


    Instant createdAt() {
        return createdAt;
    }

}