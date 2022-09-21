package com.guflimc.clans.common.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanInvite;
import com.guflimc.clans.api.domain.ClanPermission;
import com.guflimc.clans.api.domain.Profile;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.Optional;

//@CommandContainer
public class ClanCommands {

    private final AudienceProvider adventure;

    public ClanCommands(AudienceProvider adventure) {
        this.adventure = adventure;
    }

    @CommandMethod("clans list")
    @CommandPermission("lavaclans.clans.list")
    public void list(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.clans.list",
                ClanAPI.get().clans().stream().map(Clan::name).toList());
    }

    @CommandMethod("clans invite <player>")
    @CommandPermission("lavaclans.clans.invite")
    public void invite(Audience sender, Profile sprofile, @Argument("player") String username) {
        if (sprofile.clanProfile().isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        Clan clan = sprofile.clanProfile().orElseThrow().clan();

        if (!sprofile.clanProfile().get().hasPermission(ClanPermission.INVITE_PLAYER)) {
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

            Optional<ClanInvite> recent = target.mostRecentInvite(clan);
            if (recent.isPresent() && !recent.get().isExpired()) {
                I18nAPI.get(this).send(sender, "cmd.clans.invite.error.already.invited");
                return;
            }

            target.addInvite(sprofile, clan);
            ClanAPI.get().update(target);

            // send messages
            I18nAPI.get(this).send(sender, "cmd.clans.invite.sender", sprofile.name());

            // to target
            Component accept = I18nAPI.get(this).hoverable(sender, "chat.button.accept", "chat.button.accept.hover")
                    .clickEvent(ClickEvent.runCommand("/clans join " + clan.name()));
            Component decline = I18nAPI.get(this).hoverable(sender, "chat.button.decline", "chat.button.decline.hover")
                    .clickEvent(ClickEvent.runCommand("/clans reject " + clan.name()));

            Component message = I18nAPI.get(this).translate(sender, "cmd.clans.invite.target", clan.name());
            I18nAPI.get(this).menu(sender, message, Component.text(""), I18nAPI.get(this).center(accept, decline));

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @CommandMethod("clans join <clan>")
    @CommandPermission("lavaclans.clans.join")
    public void join(Audience sender, Profile sprofile, @Argument("clan") Clan clan) {
        if (sprofile.clanProfile().isPresent()) {
            I18nAPI.get(this).send(sender, "cmd.error.base.already.in.clan");
            return;
        }

        Optional<ClanInvite> recent = sprofile.mostRecentInvite(clan);
        if (recent.isEmpty() || !recent.get().isValid()) {
            I18nAPI.get(this).send(sender, "cmd.clans.join.error.missing");
            return;
        }

        if (clan.memberCount() >= clan.maxMembers()) {
            I18nAPI.get(this).send(sender, "cmd.clans.join.error.max.members");
            return;
        }

        recent.get().accept();
        ClanAPI.get().update(sprofile);

        I18nAPI.get(this).send(sender, "cmd.clans.join", clan.name());
    }

    @CommandMethod("clans reject <clan>")
    @CommandPermission("lavaclans.clans.reject")
    public void reject(Audience sender, Profile sprofile, @Argument("clan") Clan clan) {
        Optional<ClanInvite> recent = sprofile.mostRecentInvite(clan);
        if (recent.isEmpty() || !recent.get().isValid()) {
            I18nAPI.get(this).send(sender, "cmd.clans.join.error.missing");
            return;
        }

        recent.get().reject();
        ClanAPI.get().update(sprofile);

        I18nAPI.get(this).send(sender, "cmd.clans.reject", clan.name());

        Audience invsender = adventure.player(recent.get().sender().id());
        I18nAPI.get(this).send(invsender, "cmd.clans.reject.sender", sprofile.name());
    }


}
