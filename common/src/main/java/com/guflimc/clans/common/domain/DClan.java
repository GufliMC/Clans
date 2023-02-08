package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.crest.CrestConfig;
import com.guflimc.clans.api.crest.CrestType;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.CrestTemplate;
import com.guflimc.clans.common.converters.CrestConfigConverter;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

    @DbDefault("16581375")
    private int rgbColor = 16581375;

    @DbDefault("10")
    private int maxMembers = 10;

    @Formula(select = "aggr.member_count", join = "join (select cp.clan_id, count(cp.id) as member_count from clan_profiles cp) as aggr ON ${ta}.id = aggr.clan_id")
    public int memberCount;

    @ManyToOne(cascade = {CascadeType.ALL})
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    private DCrestTemplate crestTemplate;

    @Convert(converter = CrestConfigConverter.class)
    @Column(length = 8192)
    private CrestConfig crestConfig;

    @OneToMany(targetEntity = DClanAttribute.class, mappedBy = "clan",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DClanAttribute> attributes = new ArrayList<>();

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
    public int color() {
        return rgbColor;
    }

    @Override
    public void setColor(int color) {
        this.rgbColor = color;
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

    // crest

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

    // attributes

    @Override
    public <T> void setAttribute(ClanAttributeKey<T> key, T value) {
        if (value == null) {
            removeAttribute(key);
            return;
        }

        DAttribute attribute = attributes.stream()
                .filter(attr -> attr.name().equals(key.name()))
                .findFirst().orElse(null);

        if (attribute == null) {
            attributes.add(new DClanAttribute(this, key, value));
            return;
        }

        attribute.setValue(key, value);
    }

    @Override
    public <T> void removeAttribute(ClanAttributeKey<T> key) {
        attributes.removeIf(attr -> attr.name().equals(key.name()));
    }

    @Override
    public <T> Optional<T> attribute(ClanAttributeKey<T> key) {
        return attributes.stream().filter(attr -> attr.name().equals(key.name()))
                .findFirst().map(ra -> ra.value(key));
    }

    // timestamps

    @Override
    public Instant createdAt() {
        return createdAt;
    }

}