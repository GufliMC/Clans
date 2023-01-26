package com.guflimc.clans.common.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

//@CommandContainer
public class ClanCommands {

    private final AudienceProvider adventure;

    public ClanCommands(AudienceProvider adventure) {
        this.adventure = adventure;
    }

    @CommandMethod("clans list")
    @CommandPermission("clans.list")
    public void list(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.clans.list",
                ClanAPI.get().clans().stream().map(Clan::name).toList());
    }

    @CommandMethod("clans invite <player>")
    @CommandPermission("clans.invite")
    public void invite(Audience sender, Profile sprofile, @Argument("player") String username) {
        if (sprofile.clanProfile().isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        Clan clan = sprofile.clanProfile().orElseThrow().clan();

        if (!sprofile.clanProfile().get().hasPermission(ClanPermission.INVITE_PLAYERS)) {
            I18nAPI.get(this).send(sender, "cmd.error.base.no.permission");
            return;
        }

        if (clan.memberCount() >= clan.maxMembers()) {
            I18nAPI.get(this).send(sender, "cmd.clans.invite.error.max.members");
            return;
        }

        ClanAPI.get().findProfile(username).thenAccept(target -> {
            if (target == null) {
                I18nAPI.get(this).send(sender, "cmd.error.args.player", username);
                return;
            }

            if (target.clanProfile().isPresent()) {
                I18nAPI.get(this).send(sender, "cmd.clans.invite.error.already.in.clan");
                return;
            }

            ClanInvite recent = target.mostRecentInvite(clan).orElse(null);
            if (recent != null && !recent.isExpired() && !recent.isAnswered()) {
                I18nAPI.get(this).send(sender, "cmd.clans.invite.error.already.invited");
                return;
            }

            target.addInvite(sprofile, clan);
            ClanAPI.get().update(target);

            // send messages
            I18nAPI.get(this).send(sender, "cmd.clans.invite.sender", target.name());

            Audience targetAudience = adventure.player(target.id());

            // to target
            Component accept = I18nAPI.get(this).hoverable(targetAudience, "chat.button.accept", "chat.button.accept.hover")
                    .clickEvent(ClickEvent.runCommand("/clans join " + clan.name()));
            Component decline = I18nAPI.get(this).hoverable(targetAudience, "chat.button.decline", "chat.button.decline.hover")
                    .clickEvent(ClickEvent.runCommand("/clans reject " + clan.name()));

            Component message = I18nAPI.get(this).translate(targetAudience, "cmd.clans.invite.target", clan.name());

            int width = I18nAPI.get(this).width(message);
            Component buttons = I18nAPI.get(this).paddingAround(width, accept, decline);

            I18nAPI.get(this).menu(targetAudience, message, Component.text(""), buttons);

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @CommandMethod("clans uninvite <player>")
    @CommandPermission("clans.uninvite")
    public void uninvite(Audience sender, Profile sprofile, @Argument("player") String username) {
        if (sprofile.clanProfile().isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        Clan clan = sprofile.clanProfile().orElseThrow().clan();

        if (!sprofile.clanProfile().get().hasPermission(ClanPermission.INVITE_PLAYERS)) {
            I18nAPI.get(this).send(sender, "cmd.error.base.no.permission");
            return;
        }

        ClanAPI.get().findProfile(username).thenAccept(target -> {
            if (target == null) {
                I18nAPI.get(this).send(sender, "cmd.error.args.player", username);
                return;
            }

            ClanInvite recent = target.mostRecentInvite(clan).orElse(null);
            if (recent == null || !recent.isActive()) {
                I18nAPI.get(this).send(sender, "cmd.clans.uninvite.error.not.invited");
                return;
            }

            recent.cancel();
            ClanAPI.get().update(target);

            // send messages
            I18nAPI.get(this).send(sender, "cmd.clans.uninvite.sender", target.name());
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @CommandMethod("clans kick <player>")
    @CommandPermission("clans.kick")
    public void kick(Audience sender, Profile sprofile, @Argument("player") String username) {
        if (sprofile.clanProfile().isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        if (!sprofile.clanProfile().get().hasPermission(ClanPermission.KICK_MEMBERS)) {
            I18nAPI.get(this).send(sender, "cmd.error.base.no.permission");
            return;
        }

        Clan clan = sprofile.clanProfile().orElseThrow().clan();

        ClanAPI.get().findProfile(username).thenAccept(target -> {
            if (target == null) {
                I18nAPI.get(this).send(sender, "cmd.error.args.player", username);
                return;
            }

            if (target.clanProfile().isEmpty() || !target.clanProfile().get().clan().equals(clan)) {
                I18nAPI.get(this).send(sender, "cmd.clans.kick.error.not.in.clan");
                return;
            }

            target.clanProfile().get().quit();
            ClanAPI.get().update(target);

            // send messages
            I18nAPI.get(this).send(sender, "cmd.clans.kick.sender", target.name());

            Audience targetAudience = adventure.player(target.id());
            I18nAPI.get(this).send(targetAudience, "cmd.clans.kick.target");
        });
    }

    @CommandMethod("clans join <clan>")
    @CommandPermission("clans.join")
    public void join(Audience sender, Profile sprofile, @Argument("clan") Clan clan) {
//        if (sprofile.clanProfile().isPresent()) {
//            I18nAPI.get(this).send(sender, "cmd.error.base.already.in.clan");
//            return;
//        }

        ClanInvite recent = sprofile.mostRecentInvite(clan).orElse(null);
        if (recent == null || !recent.isActive()) {
            I18nAPI.get(this).send(sender, "cmd.clans.join.error.missing");
            return;
        }

        if (clan.memberCount() >= clan.maxMembers()) {
            I18nAPI.get(this).send(sender, "cmd.clans.join.error.max.members");
            return;
        }

        recent.accept();
        ClanAPI.get().update(sprofile);

        I18nAPI.get(this).send(sender, "cmd.clans.join", clan.name());
    }

    @CommandMethod("clans reject <clan>")
    @CommandPermission("clans.reject")
    public void reject(Audience sender, Profile sprofile, @Argument("clan") Clan clan) {
        ClanInvite recent = sprofile.mostRecentInvite(clan).orElse(null);
        if (recent == null || !recent.isActive()) {
            I18nAPI.get(this).send(sender, "cmd.clans.join.error.missing");
            return;
        }

        recent.reject();
        ClanAPI.get().update(sprofile);

        I18nAPI.get(this).send(sender, "cmd.clans.reject", clan.name());

        Audience invsender = adventure.player(recent.sender().id());
        I18nAPI.get(this).send(invsender, "cmd.clans.reject.sender", sprofile.name());
    }

    @CommandMethod("clans quit")
    @CommandPermission("clans.quit")
    public void quit(Audience sender, Profile sprofile) {
        if (sprofile.clanProfile().isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        if (sprofile.clanProfile().get().isLeader()) {
            I18nAPI.get(this).send(sender, "cmd.clans.quit.error.leader");
            return;
        }

        sprofile.clanProfile().get().quit();
        ClanAPI.get().update(sprofile);

        I18nAPI.get(this).send(sender, "cmd.clans.quit");
    }

    @CommandMethod("clans disband")
    @CommandPermission("clans.disband")
    public void disband(Audience sender, Profile sprofile) {
        if (sprofile.clanProfile().isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        if (!sprofile.clanProfile().get().isLeader()) {
            I18nAPI.get(this).send(sender, "cmd.clans.perms.error.not.leader");
            return;
        }

        ClanAPI.get().remove(sprofile.clanProfile().get().clan());
        I18nAPI.get(this).send(sender, "cmd.clans.disband");
    }

    @CommandMethod("clans create <name> <tag>")
    @CommandPermission("clans.create")
    public void create(Audience sender, Profile sprofile, @Argument("name") String name, @Argument("tag") String tag) {
        if (sprofile.clanProfile().isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.error.base.already.in.clan");
            return;
        }

        if (sprofile.clanProfile().isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.clans.join.error.already");
            return;
        }

        if (!name.matches("[a-zA-Z0-9]{2,24}")) {
            I18nAPI.get(this).send(sender, "cmd.clans.create.error.name.format", name);
            return;
        }

        if (!tag.matches("[a-zA-Z0-9]{2,3}")) {
            I18nAPI.get(this).send(sender, "cmd.clans.create.error.tag.format", tag);
            return;
        }

        if (ClanAPI.get().findClan(name).isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.clans.create.error.name.exists");
            return;
        }

        tag = tag.toUpperCase();
        if (ClanAPI.get().findClanByTag(tag).isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.clans.create.error.tag.exists");
            return;
        }

        ClanAPI.get().create(sprofile, name, tag).thenAccept(clan -> {
            I18nAPI.get(this).send(sender, "cmd.clans.create", clan.name(), clan.tag());
        });
    }

}
