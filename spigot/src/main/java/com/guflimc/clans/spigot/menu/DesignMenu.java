package com.guflimc.clans.spigot.menu;

import com.guflimc.brick.gui.spigot.SpigotBrickGUI;
import com.guflimc.brick.gui.spigot.api.ISpigotMenu;
import com.guflimc.brick.gui.spigot.api.ISpigotMenuBuilder;
import com.guflimc.brick.gui.spigot.item.ItemStackBuilder;
import com.guflimc.brick.gui.spigot.item.specific.LeatherArmorBuilder;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.brick.maths.api.geo.pos.Vector;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.ClanPermission;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.spigot.SpigotClans;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Comparator;

public class DesignMenu {

    private static final SpigotNamespace namespace = SpigotI18nAPI.get().byClass(SpigotClans.class);

    public static void open(Player player, Clan clan) {
        ISpigotMenuBuilder bmenu = SpigotBrickGUI.builder()
                .withTitle(namespace.string(player, "menu.clans.design.title", clan.name()));

        if (hasPermission(player, clan, ClanPermission.CHANGE_COLOR)) {
            ItemStack color = ItemStackBuilder.leatherArmor(LeatherArmorBuilder.ArmorType.CHESTPLATE)
                    .withArmorColor(clan.color())
                    .withName(namespace.string(player, "menu.clans.design.color"))
                    .withLore(namespace.string(player, "menu.clans.design.color.lore"))
                    .build();
            bmenu.withItem(color, c -> {
                openColor(player, clan);
            });
        }

        if (hasPermission(player, clan, ClanPermission.CHANGE_BANNER)) {
            Color color = Color.fromRGB(clan.color());
            Vector vec = new Vector(color.getRed(), color.getGreen(), color.getBlue());
            DyeColor dye = Arrays.stream(DyeColor.values())
                    .min(Comparator.comparing(dc -> {
                        Color c = dc.getColor();
                        return vec.distance(new Vector(c.getRed(), c.getGreen(), c.getBlue()));
                    })).orElse(DyeColor.WHITE);

            ItemStack banner = ItemStackBuilder.banner(dye)
                    .withName(namespace.string(player, "menu.clans.design.banner"))
                    .withLore(namespace.string(player, "menu.clans.design.banner.lore"))
                    .build();
            bmenu.withItem(banner, c -> {
                openBanner(player, clan);
            });
        }

        bmenu.build().open(player);
    }

    public static void openColor(Player player, Clan clan) {
        ISpigotMenu menu = SpigotBrickGUI.create(54, namespace.string(player, "menu.clans.design.color.title", clan.name()));

        int index = 11;
        for (DyeColor color : DyeColor.values()) {
            menu.setItem(index, ItemStackBuilder.wool(color)
                    .withName(color.name().charAt(0) + color.name().toLowerCase().substring(1).replace("_", " "))
                    .build(), c -> {
                if (color == DyeColor.WHITE || color == DyeColor.BLACK) {
                    clan.setColor(color.getColor().asRGB());
                    open(player, clan);
                    return;
                }

                openExtraColor(player, clan, color.getColor());
            });

            index++;
            if ((index + 2) % 9 == 0) {
                index += 4;
            }
        }

        menu.open(player);
    }

    public static void openExtraColor(Player player, Clan clan, Color color) {
        ISpigotMenu menu = SpigotBrickGUI.create(54, namespace.string(player, "menu.clans.design.color.extra.title", clan.name()));
        float[] hsb = java.awt.Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

//        int index = 10;
//        for (float s = 1f; s > 0.22f; s -= 0.2f) {
//            for (float b = 0.28f; b <= 1f; b += 0.12f) {
//                java.awt.Color tmp = java.awt.Color.getHSBColor(hsb[0], s, b);
//                Color c = Color.fromRGB(tmp.getRed(), tmp.getGreen(), tmp.getBlue());
//                menu.setItem(index, ItemStackBuilder.leatherArmor(LeatherArmorBuilder.ArmorType.CHESTPLATE)
//                        .withArmorColor(c)
//                        .build());
//                index++;
//            }
//            index += 2;
//        }

        int index = 10;
        for (float b = 0.28f; b <= 1f; b += 0.12f) {
            java.awt.Color tmp = java.awt.Color.getHSBColor(hsb[0], Math.min(0, hsb[1] + .3f), b);
            Color c = Color.fromRGB(tmp.getRed(), tmp.getGreen(), tmp.getBlue());
            menu.setItem(index, ItemStackBuilder.leatherArmor(LeatherArmorBuilder.ArmorType.CHESTPLATE)
                    .withArmorColor(c)
                    .build());
            index++;
        }
        index += 2;

        for (float b = 0.28f; b <= 1f; b += 0.12f) {
            java.awt.Color tmp = java.awt.Color.getHSBColor(hsb[0], Math.max(0, hsb[1] - .3f), b);
            Color c = Color.fromRGB(tmp.getRed(), tmp.getGreen(), tmp.getBlue());
            menu.setItem(index, ItemStackBuilder.leatherArmor(LeatherArmorBuilder.ArmorType.CHESTPLATE)
                    .withArmorColor(c)
                    .build());
            index++;
        }
        index += 2;

        for (float b = 0.28f; b <= 1f; b += 0.12f) {
            java.awt.Color tmp = java.awt.Color.getHSBColor(hsb[0], hsb[1], b);
            Color c = Color.fromRGB(tmp.getRed(), tmp.getGreen(), tmp.getBlue());
            menu.setItem(index, ItemStackBuilder.leatherArmor(LeatherArmorBuilder.ArmorType.CHESTPLATE)
                    .withArmorColor(c)
                    .build());
            index++;
        }
        index += 2;


        for (float b = 0.28f; b <= 1f; b += 0.12f) {
            java.awt.Color tmp = java.awt.Color.getHSBColor(hsb[0] - .2f, hsb[1], b);
            Color c = Color.fromRGB(tmp.getRed(), tmp.getGreen(), tmp.getBlue());
            menu.setItem(index, ItemStackBuilder.leatherArmor(LeatherArmorBuilder.ArmorType.CHESTPLATE)
                    .withArmorColor(c)
                    .build());
            index++;
        }

        menu.open(player);
    }

    public static void openBanner(Player player, Clan clan) {

    }

    private static boolean hasPermission(Player player, Clan clan, ClanPermission permission) {
        if (player.hasPermission("clans.admin")) {
            return true;
        }
        ClanProfile profile = ClanAPI.get().findCachedProfile(player.getUniqueId())
                .flatMap(Profile::clanProfile).orElse(null);
        if (profile == null || !profile.clan().equals(clan)) {
            return false;
        }
        return profile.hasPermission(permission);
    }

}
