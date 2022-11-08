package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.domain.BannerPattern;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanInvite;
import com.guflimc.clans.api.domain.Profile;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "banner_patterns")
public class DBannerPattern implements BannerPattern {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String data;

    @ColumnDefault("false")
    private boolean restricted = false;

    //

    public DBannerPattern() {
    }

    public DBannerPattern(String name, String data, boolean restricted) {
        this.name = name;
        this.data = data;
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
    public String data() {
        return data;
    }

    @Override
    public boolean restricted() {
        return restricted;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DBannerPattern other && other.id.equals(id);
    }

}