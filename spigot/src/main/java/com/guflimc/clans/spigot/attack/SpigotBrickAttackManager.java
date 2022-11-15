package com.guflimc.clans.spigot.attack;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.orm.api.database.DatabaseContext;
import com.guflimc.brick.placeholders.spigot.api.SpigotPlaceholderAPI;
import com.guflimc.brick.sidebar.api.Sidebar;
import com.guflimc.brick.sidebar.spigot.api.SpigotSidebarAPI;
import com.guflimc.clans.api.AttackAPI;
import com.guflimc.clans.api.domain.Attack;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Nexus;
import com.guflimc.clans.common.ClansConfig;
import com.guflimc.clans.common.attack.AbstractAttackManager;
import com.guflimc.clans.spigot.util.ClanTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

public class SpigotBrickAttackManager extends AbstractAttackManager {

    private final Sidebar sidebar;

    public SpigotBrickAttackManager(DatabaseContext databaseContext, ClansConfig config) {
        super(databaseContext);

        if (config.attackSidebar != null && config.attackSidebar.title != null && config.attackSidebar.lines != null) {
            sidebar = new Sidebar(MiniMessage.miniMessage().deserialize(config.attackSidebar.title));
            for (String line : config.attackSidebar.lines) {
                sidebar.appendLines(MiniMessage.miniMessage().deserialize(line));
            }
        } else {
            sidebar = null;
        }

        // PLACEHOLDERS
        SpigotPlaceholderAPI.get().registerReplacer("clan_attack_defender", (player) -> {
            return ClanTools.clan(player).flatMap(this::findAttack)
                    .map(Attack::defender).map(Clan::name)
                    .map(Component::text).orElse(null);
        });
        SpigotPlaceholderAPI.get().registerReplacer("clan_attack_attacker", (player) -> {
            return ClanTools.clan(player).flatMap(this::findAttack)
                    .map(Attack::attacker).map(Clan::name)
                    .map(Component::text).orElse(null);
        });
        SpigotPlaceholderAPI.get().registerReplacer("clan_attack_nexus_health", (player) -> {
            return ClanTools.clan(player).flatMap(this::findAttack)
                    .map(Attack::nexusHealth)
                    .map(Component::text).orElse(null);
        });
        SpigotPlaceholderAPI.get().registerReplacer("clan_attack_time_left", (player) -> {
            return ClanTools.clan(player).flatMap(this::findAttack)
                    .map(Attack::createdAt)
                    .map(start -> Duration.between(Instant.now(), start.plus(config.attackDuration, ChronoUnit.MINUTES)))
                    .map(this::format)
                    .map(Component::text).orElse(null);
        });

        resume();
    }

    private String format(Duration duration) {
        duration = duration.withNanos(0);
        String result = duration.toString().substring(2);
        int index = result.indexOf(".");
        if (index > 0) {
            result = result.substring(0, index);
        }

        result = result.replace("H", "h ");
        result = result.replace("M", "m ");
        result = result.replace("S", "s");
        return result;
    }

    @Override
    public CompletableFuture<Attack> start(Clan defender, Clan attacker) {
        return super.start(defender, attacker).thenApply(attack -> {
            // attacking players
            ClanTools.onlinePlayers(attacker).forEach(p -> {
                p.playSound(p.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                SpigotI18nAPI.get(this).send(p, "attack.start.attackers", defender.name());
                if (sidebar != null) SpigotSidebarAPI.get().push(p, sidebar);
            });

            // defending players
            ClanTools.onlinePlayers(defender).forEach(p -> {
                p.playSound(p.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                SpigotI18nAPI.get(this).send(p, "attack.start.defenders", attacker.name());
                if (sidebar != null) SpigotSidebarAPI.get().push(p, sidebar);
            });

            return attack;
        });
    }

    @Override
    public void stop(Attack attack) {
        super.stop(attack);

        // attacking players
        ClanTools.onlinePlayers(attack.attacker()).forEach(p -> {
            p.playSound(p.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
            SpigotI18nAPI.get(this).send(p, "attack.stop.attackers", attack.defender().name());
            if (sidebar != null) SpigotSidebarAPI.get().remove(p, sidebar);
        });

        // defending players
        ClanTools.onlinePlayers(attack.defender()).forEach(p -> {
            p.playSound(p.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
            SpigotI18nAPI.get(this).send(p, "attack.stop.defenders", attack.attacker().name());
            if (sidebar != null) SpigotSidebarAPI.get().remove(p, sidebar);
        });
    }

    private void resume() {
        for (Attack attack : attacks()) {
            ClanTools.onlinePlayers(attack.attacker()).forEach(p -> {
                if (sidebar != null) SpigotSidebarAPI.get().push(p, sidebar);
            });

            // defending players
            ClanTools.onlinePlayers(attack.defender()).forEach(p -> {
                if (sidebar != null) SpigotSidebarAPI.get().push(p, sidebar);
            });
        }
    }

    public void attack(Player player, Clan clan, Nexus nexus, Block block) {
        Attack dattack = AttackAPI.get().findAttack(nexus.clan()).orElse(null);
        Attack aattack = AttackAPI.get().findAttack(clan).orElse(null);
        if (dattack == null && aattack == null) {
            // START AN ATTACK
            AttackAPI.get().start(nexus.clan(), clan);
            return;
        }

        if (dattack != aattack) {
            SpigotI18nAPI.get(this).send(player, "attack.error.wrong-nexus");
            return; // can't attack another clan than the one you're attacking
        }

        // remove health from nexus
        int health = aattack.nexusHealth() - 1;
        aattack.setNexusHealth(health);

        if (aattack.isNexusDestroyed()) {
            AttackAPI.get().stop(aattack);
            block.setType(Material.BEDROCK);

            // TODO start timer to change block back to original
            return;
        }

        if ((health >= 100 && health % 100 == 0) || (health <= 50 && health >= 10 && health % 10 == 0) || health == 5) {
            // defenders
            ClanTools.onlinePlayers(nexus.clan()).forEach(p -> {
                SpigotI18nAPI.get(this).send(p, "attack.progress.defenders", health);
                p.playSound(block.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            });

            // attackers
            ClanTools.onlinePlayers(clan).forEach(p -> {
                SpigotI18nAPI.get(this).send(p, "attack.progress.attackers", health);
            });
        }
    }

}
