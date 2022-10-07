package com.guflimc.clans.common.domain;

import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Nexus;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "clans")
public class DClan implements Clan {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String tag;

    @OneToOne(targetEntity = DNexus.class, mappedBy = "clan", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(foreignKeyDefinition =
            "foreign key (nexus_id) references nexuses (id) on delete set null"))
    private DNexus nexus;

    @ColumnDefault("16581375")
    private int rgbColor = 16581375;

    @ColumnDefault("1")
    private int level = 1;

    @ColumnDefault("10")
    private int maxMembers = 10;

    @Formula("(select count(cp.id) from clan_profiles cp where cp.clan_id = id)")
    public int memberCount;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    //

    public DClan() {
    }

    public DClan(String name, String tag) {
        this.name = name;
        this.tag = tag;
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
    public String tag() {
        return tag;
    }

    @Override
    public Optional<Nexus> nexus() {
        return Optional.ofNullable(nexus);
    }

    public void setNexus(Location loc) {
        nexus = new DNexus(this, loc);
        updateNexusArea();
    }

    private void updateNexusArea() {
        nexus().ifPresent(nexus -> ((DNexus) nexus).setAreaSize(level * 40));
    }

    @Override
    public int color() {
        return rgbColor;
    }

    @Override
    public void setColor(int color) {
        this.rgbColor = color;
    }

    @Override
    public int level() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
        updateNexusArea();
    }

    @Override
    public int maxMembers() {
        return maxMembers;
    }

    @Override
    public void setMaxMembers(int value) {
        this.maxMembers = value;
    }

    @Override
    public int memberCount() {
        return memberCount;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DClan other && other.id.equals(id);
    }
}