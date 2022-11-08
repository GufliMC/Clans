package com.guflimc.clans.spigot.util;

import com.guflimc.brick.gui.spigot.item.ItemStackBuilder;
import com.guflimc.brick.gui.spigot.item.specific.BannerBuilder;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.api.domain.SigilType;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

public class ClanTools {

    public static ItemStack sigil(@NotNull SigilType type, @NotNull DyeColor backgroundColor, @NotNull DyeColor foregroundColor) {
        BannerBuilder b = ItemStackBuilder.banner(backgroundColor);

        String[] layers = type.data().split(Pattern.quote(";"));
        String[] patterns = layers[0].split(Pattern.quote(","));
        for (String pattern : patterns) {
            b.withBannerPattern(foregroundColor, PatternType.valueOf(pattern));
        }

        if ( layers.length > 1 ) {
            patterns = layers[1].split(Pattern.quote(","));
            for (String pattern : patterns) {
                b.withBannerPattern(backgroundColor, PatternType.valueOf(pattern));
            }
        }

        return b.build();
    }

    public static ItemStack sigil(@NotNull Clan clan) {
        DyeColor background = findBestDye(clan.color());
        SigilType type = clan.sigilType();
        if ( type != null ) {
            return sigil(type, background, DyeColor.values()[clan.sigilColor()]);
        }
        return ItemStackBuilder.banner(background).build();
    }

    public static Collection<Player> onlinePlayers(@NotNull Clan clan) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> ClanAPI.get().findCachedProfile(p.getUniqueId())
                        .flatMap(Profile::clanProfile)
                        .map(ClanProfile::clan)
                        .filter(c -> c.equals(clan))
                        .isPresent()
                ).map(Player.class::cast).toList();
    }

    public static Optional<Clan> clan(@NotNull Player player) {
        return ClanAPI.get()
                .findCachedProfile(player.getUniqueId())
                .flatMap(Profile::clanProfile)
                .map(ClanProfile::clan);
    }

    public static DyeColor findBestDye(int ahex) {
        DyeColor closestMatch = null;
        int minMSE = Integer.MAX_VALUE;

        for ( DyeColor color : DyeColor.values() ) {
            int bhex = color.getColor().asRGB();
            int mse = mse(ahex, bhex);
            if (mse < minMSE) {
                minMSE = mse;
                closestMatch = color;
            }
        }
        return closestMatch;
    }

    private static int mse(int ahex, int bhex) {
        int ar = ahex >> 16 & 0xff;
        int ag = ahex >> 8 & 0xff;
        int ab = ahex & 0xff;

        int br = bhex >> 16 & 0xff;
        int bg = bhex >> 8 & 0xff;
        int bb = bhex & 0xff;

        return mse(ar, ag, ab, br, bg, bb);
    }

    private static int mse(int ar, int ag, int ab, int br, int bg, int bb) {
        return ((ar - br) * (ar - br)
                + (ag - bg) * (ag - bg)
                + (ab - bb) * (ab - bb)) / 3;
    }

}
