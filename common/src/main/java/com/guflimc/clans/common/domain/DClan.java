package com.guflimc.clans.common.domain;

import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.clans.api.cosmetic.CrestConfig;
import com.guflimc.clans.api.cosmetic.CrestType;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.CrestTemplate;
import com.guflimc.clans.api.domain.Nexus;
import com.guflimc.clans.common.converters.CrestConfigConverter;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "clans")
public class DClan implements Clan {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String tag;

    @OneToOne(targetEntity = DNexus.class, mappedBy = "clan", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    private DNexus nexus;

    @DbDefault("16581375")
    private int rgbColor = 16581375;

    @DbDefault("1")
    private int level = 1;

    @DbDefault("10")
    private int maxMembers = 10;

    @Formula(select = "aggr.member_count", join = "join (select count(cp.id) as member_count from clan_profiles cp where cp.clan_id = id) as aggr")
    public int memberCount;

    @ManyToOne(cascade = {CascadeType.ALL})
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    private DCrestTemplate crestTemplate;

    @Convert(converter = CrestConfigConverter.class)
    @Column(length = 8192)
    private CrestConfig crestConfig;

    @WhenCreated
    private Instant createdAt;

    @WhenModified
    private Instant updatedAt;

    //

    public DClan() {
    }

    public DClan(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }

    @PostLoad
    private void onLoad() {
        updateNexusArea();
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

    @Override
    public int nexusRadius() {
        return level * 40; // TODO: Configurable
    }

    public void setNexus(Location loc) {
        nexus = new DNexus(this, loc);
        updateNexusArea();
    }

    private void updateNexusArea() {
        if (nexus == null) {
            return;
        }

        nexus.setCubeRadius(nexusRadius());
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
    public void setCrestTemplate(CrestTemplate template) {
        this.crestTemplate = (DCrestTemplate) template;
    }

    @Override
    public CrestTemplate crestTemplate() {
        return crestTemplate;
    }

    @Override
    public void setCrestConfig(CrestConfig config) {
        this.crestConfig = config;
    }

    @Override
    public CrestConfig crestConfig() {
        if (crestConfig == null) {
            return new CrestConfig(CrestType.Color.WHITE, CrestConfig.ColorTarget.BACKGROUND);
        }
        return crestConfig;
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }

}