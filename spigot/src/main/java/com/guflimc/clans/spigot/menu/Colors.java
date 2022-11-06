package com.guflimc.clans.spigot.menu;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public class Colors {

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
