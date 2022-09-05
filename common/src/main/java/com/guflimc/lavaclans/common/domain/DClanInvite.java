package com.guflimc.lavaclans.common.domain;

import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.ClanInvite;
import com.guflimc.lavaclans.api.domain.Profile;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clan_invites")
public class DClanInvite implements ClanInvite {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    private Profile sender;

    @ManyToOne(targetEntity = DProfile.class, optional = false)
    private Profile target;

    @ManyToOne(targetEntity = DClan.class, optional = false)
    private Clan clan;

    @CreationTimestamp
    private Instant createdAt;

    //

    public DClanInvite() {}

    public DClanInvite(Profile sender, Profile target, Clan clan) {
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
    public Instant createdAt() {
        return createdAt;
    }

}