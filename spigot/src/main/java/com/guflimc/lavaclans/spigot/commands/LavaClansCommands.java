package com.guflimc.lavaclans.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.lavaclans.api.ClanAPI;
import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.ClanInvite;
import com.guflimc.lavaclans.api.domain.ClanPermission;
import com.guflimc.lavaclans.api.domain.Profile;
import com.guflimc.lavaclans.spigot.LavaClans;
import com.guflimc.lavaclans.spigot.menu.PermissionsMenu;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Optional;

@CommandAlias("%rootCommand")
public class LavaClansCommands extends BaseCommand {

    private final LavaClans lavaClans;

    public LavaClansCommands(LavaClans lavaClans) {
        this.lavaClans = lavaClans;
    }

    @Subcommand("list")
    @CommandPermission("lavaclans.clans.list")
    public void list(Audience sender) {
        SpigotI18nAPI.get(this).send(sender, "cmd.clans.list",
                ClanAPI.get().clans().stream().map(Clan::name).toList());
    }

    @Subcommand("create")
    @CommandPermission("lavaclans.clans.create")
    public void create(Audience sender, Profile sprofile, @Single String name, @Single String tag) {
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

    @Subcommand("invite")
    @CommandPermission("lavaclans.clans.invite")
    public void invite(Audience sender, Profile sprofile, @Single @Values("@players") String username) {
        if (sprofile.clanProfile().isEmpty()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        Clan clan = sprofile.clanProfile().orElseThrow().clan();

        if (!sprofile.clanProfile().get().hasPermission(ClanPermission.INVITE_PLAYER)) {
            SpigotI18nAPI.get(this).send(sender, "cmd.error.base.no.permission");
            return;
        }

        ClanAPI.get().findProfile(username).thenAccept(target -> {
            if (target == null) {
                SpigotI18nAPI.get(this).send(sender, "cmd.error.args.player", username);
                return;
            }

            if (target.clanProfile().isPresent()) {
                SpigotI18nAPI.get(this).send(sender, "cmd.clans.invite.error.already.in.clan");
                return;
            }

            Optional<ClanInvite> recent = target.mostRecentInvite(clan);
            if (recent.isPresent() && !recent.get().isExpired()) {
                SpigotI18nAPI.get(this).send(sender, "cmd.clans.invite.error.already.invited");
                return;
            }

            target.addInvite(sprofile, clan);
            ClanAPI.get().update(target);

            // send messages
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.invite.sender", sprofile.name());

            Player targetp = Bukkit.getPlayer(target.id());
            SpigotI18nAPI.get(this).chatMenu(targetp)
                    .withMessage("cmd.clans.invite.target", clan.name())
                    .addButton("chat.button.accept", "chat.button.accept.hover",
                            ClickEvent.runCommand("/" + lavaClans.config.rootCommand + " join " + clan.name()))
                    .addButton("chat.button.decline", "chat.button.decline.hover",
                            ClickEvent.runCommand("/" + lavaClans.config.rootCommand + " reject " + clan.name()))
                    .send();

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Subcommand("join")
    @CommandPermission("lavaclans.clans.join")
    public void join(Audience sender, Profile sprofile, @Values("@clan") Clan clan) {
        if (sprofile.clanProfile().isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.error.base.already.in.clan");
            return;
        }

        Optional<ClanInvite> recent = sprofile.mostRecentInvite(clan);
        if (recent.isEmpty() || !recent.get().isValid()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.join.error.missing");
            return;
        }

        recent.get().accept();
        ClanAPI.get().update(sprofile);

        SpigotI18nAPI.get(this).send(sender, "cmd.clans.join", clan.name());
    }

    @Subcommand("reject")
    @CommandPermission("lavaclans.clans.reject")
    public void reject(Audience sender, Profile sprofile, @Values("@clan") Clan clan) {
        Optional<ClanInvite> recent = sprofile.mostRecentInvite(clan);
        if (recent.isEmpty() || !recent.get().isValid()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.join.error.missing");
            return;
        }

        recent.get().reject();
        ClanAPI.get().update(sprofile);

        SpigotI18nAPI.get(this).send(sender, "cmd.clans.reject", clan.name());

        Player invsender = Bukkit.getPlayer(recent.get().sender().id());
        if (invsender != null) {
            SpigotI18nAPI.get(this).send(invsender, "cmd.clans.reject.sender", sprofile.name());
        }
    }

    @Subcommand("quit")
    @CommandPermission("lavaclans.clans.quit")
    @Conditions("clan")
    public void quit(Audience sender, Profile sprofile) {
        // TODO check leader role

        sprofile.clanProfile().get().quit();
        ClanAPI.get().update(sprofile);

        SpigotI18nAPI.get(this).send(sender, "cmd.clans.quit");
    }

    @Subcommand("disband")
    @CommandPermission("lavaclans.clans.disband")
    @Conditions("clan")
    public void disband(Audience sender, Profile sprofile) {
        // TODO check leader role

        ClanAPI.get().remove(sprofile.clanProfile().get().clan());
        SpigotI18nAPI.get(this).send(sender, "cmd.clans.disband");
    }

    @Subcommand("info")
    @CommandPermission("lavaclans.clans.info")
    @Conditions("clan")
    public void info(Audience sender, Profile sprofile) {
        sender.sendMessage(Component.text("Your clan is: " + sprofile.clanProfile().get().clan().name()));
    }

    @Subcommand("perms")
    @CommandPermission("lavaclans.clans.perms")
    @Conditions("clan")
    public void perms(Player sender, Profile sprofile, @Single @Values("@players") String username) {
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

            PermissionsMenu.open(sender, target.clanProfile().get());
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Subcommand("nexus")
    @CommandPermission("lavaclans.clans.nexus")
    @Conditions("clan")
    public void nexus(Player sender, Profile sprofile) {
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
        if ( yaw > 45 && yaw < 135 ) {
            return new Vector(-1, 0, 0);
        } else if ( yaw > 135 && yaw < 225 ) {
            return new Vector(0, 0, -1);
        } else if ( yaw > 225 && yaw < 315 ) {
            return new Vector(1, 0, 0);
        } else {
            return new Vector(0, 0, 1);
        }
    }

}
