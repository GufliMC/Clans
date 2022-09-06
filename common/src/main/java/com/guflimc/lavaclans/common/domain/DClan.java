package com.guflimc.lavaclans.common.domain;

import com.guflimc.lavaclans.api.domain.Clan;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.List;
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

    @OneToOne(targetEntity = DNexus.class, cascade = { CascadeType.REMOVE })
    private DNexus nexus;

    @ColumnDefault("16581375")
    private int rgbColor;

    //

    public DClan() {}

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
    public Optional<com.guflimc.lavaclans.api.domain.nexus> nexus() {
        return Optional.ofNullable(nexus);
    }

    @Override
    public void setNexus(com.guflimc.lavaclans.api.domain.nexus nexus) {
        this.nexus = (DNexus) nexus;
    }

    @Override
    public int rgbColor() {
        return rgbColor;
    }

    @Override
    public void setRGBColor(int rgbColor) {
        this.rgbColor = rgbColor;
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