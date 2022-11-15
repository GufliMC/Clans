package com.guflimc.clans.common.attack;

import com.guflimc.brick.orm.api.database.DatabaseContext;
import com.guflimc.clans.api.AttackManager;
import com.guflimc.clans.api.domain.Attack;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.common.domain.DAttack;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class AbstractAttackManager implements AttackManager {

    private final Set<Attack> attacks = new CopyOnWriteArraySet<>();
    private final DatabaseContext databaseContext;

    public AbstractAttackManager(DatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
        databaseContext.findAllAsync(DAttack.class).thenAccept(this.attacks::addAll).join();
    }

    public final Optional<Attack> findAttack(Clan clan) {
        return attacks.stream()
                .filter(a -> a.attacker().equals(clan) || a.defender().equals(clan))
                .findFirst();
    }

    public final Collection<Attack> attacks() {
        return Collections.unmodifiableSet(attacks);
    }

    public final CompletableFuture<Collection<Attack>> attacks(Clan clan) {
        // TODO
//        return databaseContext.findAllAsync(DAttack.class, (cb, root, cq) ->
//                        cq.where(cb.or(cb.equal(root.get("attacker"), clan), cb.equal(root.get("defender"), clan))))
//                .thenApply(list -> {
//                    List<Attack> result = new ArrayList<>(list);
//                    findAttack(clan).ifPresent(result::add);
//                    return result;
//                });
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    public final CompletableFuture<Void> update(Attack attack) {
        return databaseContext.persistAsync(attack);
    }

    // override me

    public CompletableFuture<Attack> start(Clan defender, Clan attacker) {
        if (findAttack(defender).isPresent()) {
            throw new IllegalStateException("The defending clan is already in an attack.");
        }
        if (findAttack(attacker).isPresent()) {
            throw new IllegalStateException("The attacking clan is already in an attack.");
        }
        if (defender.equals(attacker)) {
            throw new IllegalStateException("The attacking clan cannot be the same as the defending clan.");
        }

        DAttack attack = new DAttack(defender, attacker);
        attacks.add(attack);

        // TODO stuffs

        return databaseContext.persistAsync(attack).thenApply((v) -> attack);
    }

    public void stop(Attack attack) {
        attacks.remove(attack);

        // TODO stuff

        update(attack);
    }
}
