package com.guflimc.clans.spigot.placeholders;

import com.guflimc.brick.placeholders.spigot.api.SpigotPlaceholderAPI;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.common.ClansConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Pattern;

public class ClanPlaceholders {

    public static void init(ClansConfig config) {

        Component clanChatPrefix = MiniMessage.miniMessage().deserialize(config.clanChatPrefix);
        Component clanNametagPrefix = MiniMessage.miniMessage().deserialize(config.clanNametagPrefix);

        SpigotPlaceholderAPI.get().registerReplacer("clan", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .clanProfile()
                        .map(cp -> Component.text(cp.clan().name(), TextColor.color(cp.clan().color())))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_name", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .clanProfile()
                        .map(cp -> Component.text(cp.clan().name()))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_tag", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .clanProfile()
                        .map(cp -> Component.text(cp.clan().tag(), TextColor.color(cp.clan().color())))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_level", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .clanProfile()
                        .map(cp -> Component.text(cp.clan().level()))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_nexus", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .clanProfile()
                        .flatMap(cp -> cp.clan().nexus())
                        .map(nexus -> Component.text((int) nexus.location().x() + ", "
                                + (int) nexus.location().y() + ", " + (int) nexus.location().z()))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_chat_prefix", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .clanProfile()
                        .map(cp -> SpigotPlaceholderAPI.get().replace(player, clanChatPrefix))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_nametag_prefix", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .clanProfile()
                        .map(cp -> SpigotPlaceholderAPI.get().replace(player, clanNametagPrefix))
                        .orElse(null));
    }

}
