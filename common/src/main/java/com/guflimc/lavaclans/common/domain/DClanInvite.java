package com.guflimc.lavaclans.common.domain;

import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.ClanInvite;
import com.guflimc.lavaclans.api.domain.Profile;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "clan_invites")
public class DClanInvite implements ClanInvite {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DProfile sender;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DProfile target;

    @ManyToOne(targetEntity = DClan.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DClan clan;

    @ColumnDefault("false")
    private boolean rejected;

    @ColumnDefault("false")
    private boolean accepted;

    @CreationTimestamp
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