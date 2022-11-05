package com.guflimc.clans.spigot.placeholders;

import com.guflimc.brick.placeholders.spigot.api.SpigotPlaceholderAPI;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Nexus;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.common.ClansConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.time.Duration;

public class ClanPlaceholders {

    public static void init(ClansConfig config) {

        Component clanChatPrefix = MiniMessage.miniMessage().deserialize(config.clanChatPrefix);
        Component clanNametagPrefix = MiniMessage.miniMessage().deserialize(config.clanNametagPrefix);
        TextComponent clanRegionNone = Component.text().append(MiniMessage.miniMessage().deserialize(config.clanRegionNone)).build();

        SpigotPlaceholderAPI.get().registerReplacer("clans_clan_display", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> Component.text(cp.clan().name(), TextColor.color(cp.clan().color())))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clans_clan_name", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> Component.text(cp.clan().name()))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clans_clan_tag", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> Component.text(cp.clan().tag(), TextColor.color(cp.clan().color())))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clans_clan_level", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> Component.text(cp.clan().level()))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clans_clan_nexus", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .flatMap(cp -> cp.clan().nexus())
                        .map(nexus -> Component.text((int) nexus.location().x() + ", "
                                + (int) nexus.location().y() + ", " + (int) nexus.location().z()))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clans_clan_chat_prefix", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> SpigotPlaceholderAPI.get().replace(player, clanChatPrefix))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clans_clan_nametag_prefix", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(cp -> SpigotPlaceholderAPI.get().replace(player, clanNametagPrefix))
                        .orElse(null));

        SpigotPlaceholderAPI.get().registerReplacer("clans_region_clan", (player) ->
                SpigotRegionAPI.get().regionsAt(player.getLocation()).stream()
                        .filter(rg -> rg instanceof Nexus)
                        .map(rg -> (Nexus) rg)
                        .findFirst()
                        .map(nexus -> Component.text(nexus.clan().name(), TextColor.color(nexus.clan().color())))
                        .orElse(clanRegionNone));

        SpigotPlaceholderAPI.get().registerReplacer("clans_profile_power", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .map(profile -> Component.text(profile.power()))
                        .orElse(Component.text(0)));

        SpigotPlaceholderAPI.get().registerReplacer("clans_profile_playtime", (player) ->
                ClanAPI.get().findCachedProfile(player.getUniqueId())
                        .map(profile -> Component.text(format(Duration.ofSeconds(profile.playTime()))))
                        .orElse(Component.text(0)));
    }

    private static String format(Duration duration) {
//        duration = duration.withNanos(0);
        duration = Duration.ofSeconds(duration.getSeconds() - duration.getSeconds() % 60);
        String result = duration.toString().substring(2);
        int index = result.indexOf(".");
        if (index > 0) {
            result = result.substring(0, index);
        }

        result = result.replace("H", "h ");
        result = result.replace("M", "m ");
        result = result.replace("S", "m");
        return result;
    }

}
