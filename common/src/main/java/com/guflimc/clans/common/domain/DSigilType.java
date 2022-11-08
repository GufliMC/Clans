package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.domain.SigilType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "sigil_types")
public class DSigilType implements SigilType {

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

    public DSigilType() {
    }

    public DSigilType(String name, String data, boolean restricted) {
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
        return obj instanceof DSigilType other && other.id.equals(id);
    }

}