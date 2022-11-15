package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.cosmetic.CrestType;
import com.guflimc.clans.api.domain.CrestTemplate;
import com.guflimc.clans.common.converters.CrestTypeConverter;
import io.ebean.annotation.DbDefault;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "crest_templates")
public class DCrestTemplate implements CrestTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 8192)
    @Convert(converter = CrestTypeConverter.class)
    private CrestType type;

    @DbDefault("false")
    private boolean restricted = false;

    //

    public DCrestTemplate() {
    }

    public DCrestTemplate(String name, CrestType type, boolean restricted) {
        this.name = name;
        this.type = type;
        this.restricted = restricted;
    }

    public DCrestTemplate(String name, CrestType type) {
        this(name, type, false);
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
    public CrestType type() {
        return type;
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
        return obj instanceof DCrestTemplate other && other.id.equals(id);
    }

}