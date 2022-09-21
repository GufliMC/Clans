package com.guflimc.clans.spigot.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.spigot.SpigotClans;
import com.guflimc.clans.spigot.menu.PermissionsMenu;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

//@CommandContainer
public class SpigotClanCommands {

    private final SpigotClans lavaClans;

    public SpigotClanCommands(SpigotClans lavaClans) {
        this.lavaClans = lavaClans;
    }

    @CommandMethod("clans create <name> <tag>")
    @CommandPermission("lavaclans.clans.create")
    public void create(Audience sender, Profile sprofile, @Argument("name") String name, @Argument("tag") String tag) {
        if (sprofile.clanProfile().isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.error.base.already.in.clan");
            return;
        }

        if (sprofile.clanProfile().isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.join.error.already");
            return;
        }

        if (!name.matches("[a-zA-Z0-9]{2,24}")) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create.error.name.format", name);
            return;
        }

        if (!tag.matches("[a-zA-Z0-9]{2,3}")) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create.error.tag.format", tag);
            return;
        }

        if (ClanAPI.get().findClan(name).isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create.error.name.exists");
            return;
        }

        if (ClanAPI.get().findClanByTag(tag).isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create.error.tag.exists");
            return;
        }

        ClanAPI.get().create(sprofile, name, tag.toUpperCase()).thenAccept(clan -> {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create", clan.name(), clan.tag());
        });
    }

    @CommandMethod("clans quit")
    @CommandPermission("lavaclans.clans.quit")
    public void quit(Audience sender, Profile sprofile) {
        if (sprofile.clanProfile().isEmpty()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        // TODO check leader role

        sprofile.clanProfile().get().quit();
        ClanAPI.get().update(sprofile);

        SpigotI18nAPI.get(this).send(sender, "cmd.clans.quit");
    }

    @CommandMethod("clans disband")
    @CommandPermission("lavaclans.clans.disband")
    public void disband(Audience sender, Profile sprofile) {
        if (sprofile.clanProfile().isEmpty()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        if (!sprofile.clanProfile().get().isLeader()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.perms.error.not.leader");
            return;
        }

        ClanAPI.get().remove(sprofile.clanProfile().get().clan());
        SpigotI18nAPI.get(this).send(sender, "cmd.clans.disband");
    }

    @CommandMethod("clans perms <player>")
    @CommandPermission("lavaclans.clans.perms")
    public void perms(Player sender, Profile sprofile, @Argument("player") String username) {
        if (sprofile.clanProfile().isEmpty()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        if (!sprofile.clanProfile().get().isLeader()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.perms.error.not.leader");
            return;
        }

        Clan clan = sprofile.clanProfile().get().clan();
        ClanAPI.get().findProfile(username).thenAccept(target -> {
            if (target == null) {
                SpigotI18nAPI.get(this).send(sender, "cmd.error.args.player", username);
                return;
            }

            if (target.clanProfile().isEmpty() || !target.clanProfile().get().clan().equals(clan)) {
                SpigotI18nAPI.get(this).send(sender, "cmd.clans.perms.error.not.in.clan");
                return;
            }

            if ( target.clanProfile().get().isLeader() ) {
                SpigotI18nAPI.get(this).send(sender, "cmd.clans.perms.error.self");
                return;
            }

            PermissionsMenu.open(sender, target.clanProfile().get());
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @CommandMethod("clans nexus")
    @CommandPermission("lavaclans.clans.nexus")
    public void nexus(Player sender, Profile sprofile) {
        if (sprofile.clanProfile().isEmpty()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        if (!sprofile.clanProfile().get().isLeader()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.perms.error.not.leader");
            return;
        }

        Clan clan = sprofile.clanProfile().get().clan();

        Location loc = sender.getLocation();
        com.guflimc.brick.maths.api.geo.pos.Location nexus = SpigotMaths.toBrickLocation(loc.add(0, 1.5, 0));

        try {
            ClanAPI.get().createNexus(clan, nexus);

            Vector dir = cardinalDirectionFrom(loc);
            sender.teleport(loc.getBlock().getLocation().add(0.5, 0, 0.5).add(dir.clone().multiply(2))
                    .setDirection(sender.getLocation().getDirection()));
            Bukkit.getScheduler().runTaskLater(lavaClans, () -> sender.setVelocity(dir.clone().setY(0.1)), 1L);
        } catch (IllegalArgumentException ex) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.nexus.error.invalid");
        }
    }

    private Vector cardinalDirectionFrom(Location loc) {
        float yaw = loc.getYaw() + 180;
        if (yaw > 45 && yaw < 135) {
            return new Vector(-1, 0, 0);
        } else if (yaw > 135 && yaw < 225) {
            return new Vector(0, 0, -1);
        } else if (yaw > 225 && yaw < 315) {
            return new Vector(1, 0, 0);
        } else {
            return new Vector(0, 0, 1);
        }
    }

}