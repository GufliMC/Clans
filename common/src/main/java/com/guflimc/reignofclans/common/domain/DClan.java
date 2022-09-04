package com.guflimc.reignofclans.common.domain;

import com.guflimc.reignofclans.api.domain.Clan;
import com.guflimc.reignofclans.api.domain.Crest;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "clans")
public class DClan implements Clan {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tag;

    @OneToOne(targetEntity = Crest.class)
    private DCrest nexus;

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
    public Optional<Crest> nexus() {
        return Optional.ofNullable(nexus);
    }

    @Override
    public void setNexus(Crest nexus) {
        this.nexus = (DCrest) nexus;
    }

    @Override
    public int rgbColor() {
        return rgbColor;
    }

    @Override
    public void setRGBColor(int rgbColor) {
        this.rgbColor = rgbColor;
    }
}