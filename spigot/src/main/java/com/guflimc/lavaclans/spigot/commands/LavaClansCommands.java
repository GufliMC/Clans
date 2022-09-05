package com.guflimc.lavaclans.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.lavaclans.api.ClanAPI;
import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.Profile;
import com.guflimc.lavaclans.spigot.LavaClans;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("%rootCommand")
public class LavaClansCommands extends BaseCommand {

    private final LavaClans lavaClans;

    public LavaClansCommands(LavaClans lavaClans) {
        this.lavaClans = lavaClans;
    }

    @Subcommand("list")
    @CommandPermission("lavaclans.clans.list")
    public void list(Audience sender) {
        SpigotI18nAPI.get(this).send(sender, "cmd.clans.list", ClanAPI.get().clans());
    }

    @Subcommand("create")
    @CommandPermission("lavaclans.clans.create")
    public void create(Player player,
                       @Single String name,
                       @Single String tag) {
        Audience sender = lavaClans.adventure.player(player);

        Profile profile = ClanAPI.get().findCachedProfile(player.getUniqueId());
        if (profile.clanProfile().isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create.error.member");
            return;
        }

        if (!name.matches("[a-zA-Z0-9]{2,24}")) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create.error.name", name);
            return;
        }

        if (!tag.matches("[a-zA-Z0-9]{2,3}")) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create.error.tag", tag);
            return;
        }

        if (ClanAPI.get().findClan(name).isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create.error.exists.name");
            return;
        }

        if (ClanAPI.get().findClanByTag(tag).isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create.error.exists.tag");
            return;
        }

        ClanAPI.get().create(name, tag.toUpperCase()).thenCompose(clan -> {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.create", clan.name(), clan.tag());
            return ClanAPI.get().joinClan(profile, clan);
        }).thenRun(() -> {
            // TODO set leader rank
        });
    }

    @Subcommand("invite")
    @CommandPermission("lavaclans.clans.invite")
    public void invite(Player player,
                       @Single String username) {
        Audience sender = lavaClans.adventure.player(player);

        Profile profile = ClanAPI.get().findCachedProfile(player.getUniqueId());
        if (profile.clanProfile().isEmpty()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.error.base.noclan");
            return;
        }

        // TODO check for clan moderator role

        ClanAPI.get().findProfile(username).thenAccept(target -> {
            if (target == null) {
                SpigotI18nAPI.get(this).send(sender, "cmd.error.args.player", username);
                return;
            }

            Clan clan = profile.clanProfile().orElseThrow().clan();
            target.addInvite(profile, clan);
            ClanAPI.get().update(target);

            // send messages
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.invite.sender", profile.name());

            Player targetp = Bukkit.getPlayer(target.id());
            SpigotI18nAPI.get(this).chatMenu(targetp)
                    .withMessage("cmd.clans.invite.target", clan.name())
                    .addButton("chat.button.accept", "chat.button.accept.hover", ClickEvent.runCommand(lavaClans.config.rootCommand + " accept " + clan.name()))
                    .addButton("chat.button.decline", "chat.button.decline.hover", ClickEvent.runCommand(lavaClans.config.rootCommand + " reject " + clan.name()))
                    .send();

            targetp.sendMessage("");

            SpigotI18nAPI.get(this).chatMenu(targetp)
                    .withMessage("cmd.clans.invite.target", clan.name())
                    .addButton("chat.button.accept", ClickEvent.runCommand(lavaClans.config.rootCommand + " accept " + clan.name()))
                    .addButton("chat.button.decline", ClickEvent.runCommand(lavaClans.config.rootCommand + " reject " + clan.name()))
                    .addButton("chat.button.decline", ClickEvent.runCommand(lavaClans.config.rootCommand + " reject " + clan.name()))
                    .addButton("chat.button.accept", ClickEvent.runCommand(lavaClans.config.rootCommand + " accept " + clan.name()))
                    .send();

            targetp.sendMessage("");

            SpigotI18nAPI.get(this).chatMenu(targetp)
                    .withMessage("cmd.clans.invite.target", clan.name())
                    .addButton("chat.button.accept", ClickEvent.runCommand(lavaClans.config.rootCommand + " accept " + clan.name()))
                    .addButton("chat.button.decline", ClickEvent.runCommand(lavaClans.config.rootCommand + " reject " + clan.name()))
                    .addButton("chat.button.decline", ClickEvent.runCommand(lavaClans.config.rootCommand + " reject " + clan.name()))
                    .addButton("chat.button.accept", ClickEvent.runCommand(lavaClans.config.rootCommand + " accept " + clan.name()))
                    .addButton("chat.button.decline", ClickEvent.runCommand(lavaClans.config.rootCommand + " reject " + clan.name()))
                    .send();

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

}
