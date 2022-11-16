package com.guflimc.clans.common.domain;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.area.CuboidArea;
import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.maths.database.api.AreaConverter;
import com.guflimc.brick.maths.database.api.LocationConverter;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.clans.api.cosmetic.NexusSkin;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Nexus;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "nexuses")
public class DNexus implements Nexus {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(targetEntity = DClan.class, optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DClan clan;

    @Convert(converter = LocationConverter.class)
    @Column(nullable = false)
    private Location location = new Location(null, 0, 0, 0, 0, 0);

    @Column(nullable = false, unique = true, length = 8192)
    @Convert(converter = AreaConverter.class)
    private Area area;

    @Column(nullable = false)
    @DbDefault("'DEFAULT'")
    @Enumerated(EnumType.STRING)
    private NexusSkin skin = NexusSkin.DEFAULT;

    @WhenCreated
    private Instant createdAt;

    @WhenModified
    private Instant updatedAt;

    //

    public DNexus() {
    }

    @PostLoad
    @PostUpdate
    @PostPersist
    public void onLoad() {
        RegionAPI.get().unregister(this);
        RegionAPI.get().register(this);
    }

    @PostRemove
    public void onRemove() {
        RegionAPI.get().unregister(this);
        clan = null;
    }

    public DNexus(Clan clan, Location location) {
        this.clan = (DClan) clan;
        this.location = location;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public UUID worldId() {
        return location.worldId();
    }

    @Override
    public String name() {
        return clan.name() + "-nexus";
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public Clan clan() {
        return clan;
    }

    @Override
    public NexusSkin skin() {
        return skin;
    }

    public void setSkin(NexusSkin skin) {
        this.skin = skin;
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }

    public void setCubeRadius(int radius) {
        area = CuboidArea.of(location.add(radius, radius, radius), location.add(-radius, -radius, -radius));
    }

    @Override
    public Area area() {
        return area;
    }

}