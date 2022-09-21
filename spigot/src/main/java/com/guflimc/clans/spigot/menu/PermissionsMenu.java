package com.guflimc.clans.spigot.menu;

import com.guflimc.brick.gui.spigot.SpigotBrickGUI;
import com.guflimc.brick.gui.spigot.api.ISpigotMenu;
import com.guflimc.brick.gui.spigot.item.ItemStackBuilder;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.ClanPermission;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.spigot.SpigotClans;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PermissionsMenu {

    private static final SpigotNamespace namespace = SpigotI18nAPI.get().byClass(SpigotClans.class);

    private static final Map<ClanPermission, Material> materials = Map.of(
            ClanPermission.CHANGE_NAME, Material.NAME_TAG,
            ClanPermission.CHANGE_TAG, Material.PAPER,
            ClanPermission.CHANGE_BANNER, Material.BLUE_BANNER,
            ClanPermission.CHANGE_COLOR, Material.INK_SAC,
            ClanPermission.INVITE_PLAYER, Material.PLAYER_HEAD,
            ClanPermission.KICK_MEMBER, Material.IRON_SWORD,
            ClanPermission.OPEN_VAULT, Material.CHEST,
            ClanPermission.WITHDRAW_MONEY, Material.EMERALD
    );

    public static void open(Player player, ClanProfile cp) {
        ISpigotMenu menu = SpigotBrickGUI.create(36, namespace.string(player, "menu.clans.permissions.title", cp.profile().name()));

        int index = 11;
        for (ClanPermission perm : ClanPermission.values()) {
            ItemStack item = ItemStackBuilder.of(materials.get(perm))
                    .withName(namespace.string(player, "menu." + perm.i18nKey()))
                    .apply(cp.hasPermission(perm),
                            b -> {
                                b.withLore(namespace.string(player, "menu.clans.permissions.status")
                                        + namespace.string(player, "menu.clans.permissions.enabled"));
                                b.withEnchantment(Enchantment.SILK_TOUCH);
                                b.withItemFlag(ItemFlag.HIDE_ENCHANTS);
                            }, b -> {
                                b.withLore(namespace.string(player, "menu.clans.permissions.status")
                                        + namespace.string(player, "menu.clans.permissions.disabled"));
                            })
                    .build();

            menu.setItem(index, item, click -> {
                if ( cp.hasPermission(perm) ) {
                    cp.removePermission(perm);
                } else {
                    cp.addPermission(perm);
                }
                ClanAPI.get().update(cp);
                open(player, cp);
            });

            index++;
            if ((index + 2) % 9 == 0) {
                index += 4;
            }
        }

        menu.open(player);
    }

}
