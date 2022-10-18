package com.guflimc.clans.common.domain;

import com.guflimc.clans.api.domain.Attack;
import com.guflimc.clans.api.domain.Clan;
import jakarta.persistence.*;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "attacks")
public class DAttack implements Attack {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @ManyToOne(targetEntity = DClan.class)
    @JoinColumn(foreignKey = @ForeignKey(foreignKeyDefinition =
            "foreign key (defender_id) references clans (id) on delete set null"))
    private Clan defender;

    @ManyToOne(targetEntity = DClan.class)
    @JoinColumn(foreignKey = @ForeignKey(foreignKeyDefinition =
            "foreign key (defender_id) references clans (id) on delete set null"))
    private Clan attacker;

    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false)
    @ColumnDefault("1000")
    private int nexusHealth = 100; // 1000 = 17 minutes, one block every second

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean nexusDestroyed = false;

    private Instant endedAt;

    public DAttack() {}

    public DAttack(Clan defender, Clan attacker) {
        this.defender = defender;
        this.attacker = attacker;
    }

    public Clan defender() {
        return defender;
    }

    public Clan attacker() {
        return attacker;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public Instant endedAt() {
        return endedAt;
    }

    @Override
    public void setEndedAt(Instant endedAt) {
        if ( this.endedAt != null ) {
            throw new IllegalStateException("Attack already ended.");
        }

        this.endedAt = endedAt;
    }

    public int nexusHealth() {
        return nexusHealth;
    }

    @Override
    public void setNexusHealth(int health) {
        if ( this.nexusDestroyed || this.endedAt != null ) {
            throw new IllegalStateException("Attack already ended.");
        }

        this.nexusHealth = health;

        if ( this.nexusHealth <= 0 ) {
            this.nexusHealth = 0;
            this.nexusDestroyed = true;
            this.endedAt = Instant.now();
        }
    }

    public boolean isNexusDestroyed() {
        return nexusDestroyed;
    }

}
