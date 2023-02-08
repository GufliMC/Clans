package com.guflimc.clans.common.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Entity
@Table(
        name = "clan_attributes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"clan_id", "name"})
)
public class DClanAttribute extends DAttribute {

    @ManyToOne(optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DClan clan;

    public DClanAttribute() {
    }

    public <T> DClanAttribute(@NotNull DClan clan, @NotNull AttributeKey<T> key, @NotNull T value) {
        super(key, value);
        this.clan = clan;
    }

}
