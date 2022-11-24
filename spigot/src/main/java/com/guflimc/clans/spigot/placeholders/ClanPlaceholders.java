package com.guflimc.clans.spigot.placeholders;

import com.guflimc.brick.placeholders.spigot.api.SpigotPlaceholderAPI;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.common.ClansConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ClanPlaceholders {

    public static void init(ClansConfig config) {

        Component clanChatPrefix = MiniMessage.miniMessage().deserialize(config.clanChatPrefix);
        Component clanNametagPrefix = MiniMessage.miniMessage().deserialize(config.clanNametagPrefix);

        SpigotPlaceholderAPI.get().registerReplacer("clan_name", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> Component.text(cp.clan().name()))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_display_name", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> cp.clan().displayName())
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_tag", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> Component.text(cp.clan().tag()))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_display_tag", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> cp.clan().displayTag())
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_chat_prefix", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> SpigotPlaceholderAPI.get().replace(player, clanChatPrefix))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clan_nametag_prefix", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> SpigotPlaceholderAPI.get().replace(player, clanNametagPrefix))
                        .orElse(null));

    }

}
