package com.guflimc.clans.api;

import com.guflimc.clans.api.domain.Attack;
import com.guflimc.clans.api.domain.Clan;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public interface AttackManager {

    Optional<Attack> findAttack(Clan clan);

    Collection<Attack> attacks();

    CompletableFuture<Collection<Attack>> attacks(Clan clan);

    CompletableFuture<Void> update(Attack attack);

    //

    CompletableFuture<Attack> start(Clan defender, Clan attacker);

    void stop(Attack attack);
    
}
