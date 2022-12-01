package com.guflimc.clans.spigot.menu;

import com.guflimc.brick.gui.spigot.SpigotBrickGUI;
import com.guflimc.brick.gui.spigot.api.ISpigotMenu;
import com.guflimc.brick.gui.spigot.api.ISpigotMenuBuilder;
import com.guflimc.brick.gui.spigot.api.ISpigotMenuRowBuilder;
import com.guflimc.brick.gui.spigot.api.ISpigotPaginatedMenuBuilder;
import com.guflimc.brick.gui.spigot.item.ItemStackBuilder;
import com.guflimc.brick.gui.spigot.menu.SpigotMenuItem;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.crest.CrestConfig;
import com.guflimc.clans.api.crest.CrestType;
import com.guflimc.clans.api.domain.*;
import com.guflimc.clans.spigot.SpigotClans;
import com.guflimc.clans.spigot.api.SpigotClanAPI;
import com.guflimc.clans.spigot.util.ClanTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ClanMenu {

    private static final SpigotNamespace namespace = SpigotI18nAPI.get().byClass(SpigotClans.class);

    private static void setup(ISpigotPaginatedMenuBuilder bmenu, Player player) {
        setup(bmenu, player, null);
    }

    private static ItemStack backItem(Player player) {
        return ItemStackBuilder.of(Material.RED_BED)
                .withName(namespace.string(player, "menu.items.back.name"))
                .withLore(namespace.string(player, "menu.items.back.lore"))
                .build();
    }

    private static void setup(ISpigotPaginatedMenuBuilder bmenu, Player player, Runnable back) {
        if (back != null) {
            bmenu.withHotbarItem(4, backItem(player), c -> {
                back.run();
            });
        }

//        bmenu.withBackItem(ItemStackBuilder.of(Material.RED_BED)
//                .withName(namespace.string(player, "menu.items.previousPage"))
//                .build());
//
//        bmenu.withBackItem(ItemStackBuilder.of(Material.RED_BED)
//                .withName(namespace.string(player, "menu.items.nextPage"))
//                .build());
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

    //

    public static void open(Player player) {
        ISpigotMenuBuilder bmenu = SpigotBrickGUI.builder()
                .withTitle(namespace.string(player, "menu.main.title"));

        Profile profile = SpigotClanAPI.get().findCachedProfile(player.getUniqueId()).orElseThrow();
        Clan clan = profile.clanProfile().map(ClanProfile::clan).orElse(null);

        // clans
        if (clan != null) {
            ItemStack clanItem = ItemStackBuilder.of(ClanTools.crest(clan))
                    .withName(clan.displayName())
                    .withLore(namespace.string(player, "menu.main.clan.lore"))
                    .build();
            bmenu.withItem(clanItem, (e) -> {
                clan(player, clan, () -> open(player));
            });
        }

        if (ClanAPI.get().clans().size() > 0) {
            ItemStack clanListItem = ItemStackBuilder.of(Material.BOOK)
                    .withName(namespace.string(player, "menu.main.clanList.name"))
                    .withLore(namespace.string(player, "menu.main.clanList.lore"))
                    .build();
            bmenu.withItem(clanListItem, (e) -> {
                clanList(player, () -> open(player));
            });
        }

        // profiles
        ItemStack profileListItem = ItemStackBuilder.of(Material.PLAYER_HEAD)
                .withName(namespace.string(player, "menu.main.profileList.name"))
                .withLore(namespace.string(player, "menu.main.profileList.lore"))
                .build();
        bmenu.withItem(profileListItem, (e) -> {
            profileList(player, () -> open(player));
        });

        ItemStack profileItem = ItemStackBuilder.skull().withPlayer(player.getPlayerProfile())
                .withName(Component.text(player.getName(), NamedTextColor.WHITE))
                .withLore(namespace.string(player, "menu.main.profile.lore"))
                .build();
        bmenu.withItem(profileItem, (e) -> {
            profile(player, profile, () -> open(player));
        });

        bmenu.build().open(player);
    }

    private static void clanList(Player player, Runnable back) {
        ISpigotPaginatedMenuBuilder bmenu = SpigotBrickGUI.paginatedBuilder();
        setup(bmenu, player, back);

        List<Clan> clans = new ArrayList<>(SpigotClanAPI.get().clans());
        bmenu.withTitle(index -> namespace.string(player, "menu.clanList.title", index + 1, clans.size()));

        bmenu.withItems(clans.size(), index -> {
            Clan clan = clans.get(index);
            ItemStack clanItem = ItemStackBuilder.of(ClanTools.crest(clan))
                    .withName(clan.displayName())
                    .withLore(namespace.string(player, "menu.clanList.clan.lore", clan.name()))
                    .build();
            return new SpigotMenuItem(clanItem, c -> clan(player, clan, () -> clanList(player, back)));
        });

        bmenu.build().open(player);
    }


    public static void clan(Player player, Clan clan, Runnable back) {
        boolean any = hasPermission(player, clan, ClanPermission.ACCESS_STORAGE) ||
                hasPermission(player, clan, ClanPermission.ACCESS_VAULT) ||
                hasPermission(player, clan, ClanPermission.CHANGE_CREST);
        ISpigotMenu bmenu = SpigotBrickGUI.create(any ? 54 : 36, namespace.string(player, "menu.clan.title", clan.name()));

        ItemStack infoItem = ItemStackBuilder.of(ClanTools.crest(clan))
                .withName(clan.displayName())
                .withLore(namespace.string(player, "menu.clan.info.lore", format(player, clan.createdAt())))
                .build();
        bmenu.setItem(12, infoItem);

        ItemStack membersItem = ItemStackBuilder.skull()
                .withName(namespace.string(player, "menu.clan.members.name", clan.memberCount(), clan.maxMembers()))
                .withLore(namespace.string(player, "menu.clan.members.lore", clan.name()))
                .build();
        bmenu.setItem(14, membersItem, c -> {
            clanMembers(player, clan, () -> clan(player, clan, back));
        });

        if (!any) {
            bmenu.setItem(31, backItem(player), (c) -> {
                back.run();
            });
            bmenu.open(player);
            return;
        }

        ISpigotMenuRowBuilder row = SpigotBrickGUI.rowBuilder();

        // TODO
//        if (hasPermission(player, clan, ClanPermission.ACCESS_STORAGE)) {
//            ItemStack storageItem = ItemStackBuilder.of(Material.CHEST)
//                    .withName(namespace.string(player, "menu.clan.storage.name"))
//                    .withLore(namespace.string(player, "menu.clan.storage.lore", clan.name()))
//                    .build();
//            row.withItem(storageItem, (e) -> {
//                clanStorage(player, clan, () -> clan(player, clan, back));
//            });
//        }
//
//
//        if (hasPermission(player, clan, ClanPermission.ACCESS_VAULT)) {
//            ItemStack vaultItem = ItemStackBuilder.of(Material.ENDER_CHEST)
//                    .withName(namespace.string(player, "menu.clan.vault.name"))
//                    .withLore(namespace.string(player, "menu.clan.vault.lore", clan.name()))
//                    .build();
//            row.withItem(vaultItem, (e) -> {
//                clanVault(player, clan, () -> clan(player, clan, back));
//            });
//        }

        if (hasPermission(player, clan, ClanPermission.CHANGE_CREST)) {
            ItemStack bannerItem = ItemStackBuilder.banner(DyeColor.WHITE)
                    .withName(namespace.string(player, "menu.clan.change-crest.name"))
                    .withLore(namespace.string(player, "menu.clan.change-crest.lore", clan.name()))
                    .build();
            row.withItem(bannerItem, (e) -> {
                clanEditCrest(player, clan, () -> clan(player, clan, back));
            });
        }

        row.fill(bmenu, 3);

        bmenu.setItem(49, backItem(player), (c) -> {
            back.run();
        });

        bmenu.open(player);
    }

    private static void clanStorage(Player player, Clan clan, Runnable back) {

    }

    private static void clanVault(Player player, Clan clan, Runnable back) {

    }

    private static void clanMembers(Player player, Clan clan, Runnable back) {
        ISpigotPaginatedMenuBuilder bmenu = SpigotBrickGUI.paginatedBuilder();
        setup(bmenu, player, back);

        SpigotClanAPI.get().profiles(clan).thenAccept(profiles -> {
            bmenu.withTitle(index -> namespace.string(player, "menu.clan.members.title", clan.name(), index + 1, profiles.size()));

            bmenu.withItems(profiles.size(), index -> {
                Profile profile = profiles.get(index);
                ItemStack profileItem = ItemStackBuilder.skull().withPlayer(profile.id())
                        .withName(Component.text(profile.name(), NamedTextColor.WHITE))
                        .withLore(namespace.string(player, "menu.clan.members.profile.lore", profile.name()))
                        .build();
                return new SpigotMenuItem(profileItem, c -> {
                    profile(player, profile, () -> clanMembers(player, clan, back));
                });
            });

            SpigotClans.scheduler.sync().execute(() -> bmenu.build().open(player));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private static void kickMember(Player player, Profile profile, Runnable back) {
        SpigotBrickGUI.confirmationBuilder()
                .withTitle(namespace.string(player, "menu.profile.kick.confirm.title", profile.name()))
                .withDisplay(ItemStackBuilder.skull().withPlayer(profile.id())
                        .withName(Component.text(profile.name(), NamedTextColor.WHITE))
                        .build())
                .withAccept(() -> {
                    player.chat("/clans kick " + profile.name());
                    back.run();
                })
                .withDeny(back)
                .build().open(player);
    }

    private static void clanEditCrest(Player player, Clan clan, Runnable back) {
        ISpigotMenu menu = SpigotBrickGUI.create(54, namespace.string(player, "menu.clan.change-crest.title", clan.name()));

        ItemStack previewItem = ItemStackBuilder.of(ClanTools.crest(clan))
                .withName(namespace.string(player, "menu.clan.change-crest.preview.name"))
                .build();
        menu.setItem(13, previewItem);

        CrestConfig config = clan.crestConfig();

        DyeColor primaryColor = ClanTools.dyeColor(clan);
        ItemStack primaryColorItem = ItemStackBuilder.wool(primaryColor)
                .withName(namespace.string(player, "menu.clan.change-crest.primary-color.name"))
                .withLore(namespace.string(player, "menu.clan.change-crest.primary-color.lore", primaryColor.name()))
                .build();
        menu.setItem(28, primaryColorItem, c -> {
            clanEditCrestColor(player, clan,
                    () -> clanEditCrest(player, clan, back),
                    color -> {
                        clan.setColor(color.getColor().asRGB());
                        ClanAPI.get().update(clan);
                        clanEditCrest(player, clan, back);
                    });
        });

        DyeColor secondaryColor = DyeColor.valueOf(config.color().name());
        ItemStack secondaryColorItem = ItemStackBuilder.wool(secondaryColor)
                .withName(namespace.string(player, "menu.clan.change-crest.secondary-color.name"))
                .withLore(namespace.string(player, "menu.clan.change-crest.secondary-color.lore", secondaryColor.name()))
                .build();
        menu.setItem(30, secondaryColorItem, c -> {
            clanEditCrestColor(player, clan,
                    () -> clanEditCrest(player, clan, back),
                    color -> {
                        clan.setCrestConfig(config.withColor(CrestType.Color.valueOf(color.name())));
                        ClanAPI.get().update(clan);
                        clanEditCrest(player, clan, back);
                    });
        });

        CrestConfig.ColorTarget target = config.target();
        ItemStack colorToggleItem = ItemStackBuilder.of(target == CrestConfig.ColorTarget.FOREGROUND ? Material.GLOW_INK_SAC : Material.INK_SAC)
                .withName(namespace.string(player, "menu.clan.change-crest.color-toggle.name"))
                .withLore(namespace.string(player, "menu.clan.change-crest.color-toggle.lore", target.name()))
                .build();
        menu.setItem(32, colorToggleItem, (e) -> {
            clan.setCrestConfig(config.withTarget(CrestConfig.ColorTarget.values()[(target.ordinal() + 1) % 2]));
            ClanAPI.get().update(clan);
            clanEditCrest(player, clan, back);
        });

        ItemStack crestTypeItem = ItemStackBuilder.of(ClanTools.crest(clan))
                .withName(namespace.string(player, "menu.clan.change-crest.type.name"))
                .withLore(namespace.string(player, "menu.clan.change-crest.type.lore"))
                .build();
        menu.setItem(34, crestTypeItem, (e) -> {
            clanEditCrestType(player, clan, () -> clanEditCrest(player, clan, back));
        });

        menu.setItem(49, backItem(player), c -> {
            back.run();
        });

        menu.open(player);
    }

    private static void clanEditCrestType(Player player, Clan clan, Runnable back) {
        ISpigotPaginatedMenuBuilder bmenu = SpigotBrickGUI.paginatedBuilder();
        setup(bmenu, player, back);
        bmenu.withTitle(index -> namespace.string(player, "menu.clan.change-crest.type.title", clan.name(), index + 1, 1));

        List<CrestTemplate> crestTemplates = new ArrayList<>(SpigotClanAPI.get().crestTemplates());
        crestTemplates.sort(Comparator.<CrestTemplate>comparingInt(c -> c.restricted() ? 1 : 0).thenComparing(CrestTemplate::name));

        bmenu.withItems(crestTemplates.size(), index -> {
            CrestTemplate crestTemplate = crestTemplates.get(index);
            ItemStack item = ItemStackBuilder.of(ClanTools.crest(crestTemplate.type(), clan.crestConfig(), ClanTools.dyeColor(clan.color())))
                    .withName(Component.text(crestTemplate.name(), NamedTextColor.WHITE))
                    .build();
            return new SpigotMenuItem(item, c -> {
                clan.setCrestTemplate(crestTemplate);
                ClanAPI.get().update(clan);
                back.run();
            });
        });

        bmenu.build().open(player);
    }

    private static void clanEditCrestColor(Player player, Clan clan, Runnable back, Consumer<DyeColor> callback) {
        ISpigotMenu menu = SpigotBrickGUI.create(54, namespace.string(player, "menu.clan.change-crest.color.title", clan.name()));

        int index = 10;
        for (DyeColor color : DyeColor.values()) {
            menu.setItem(index, ItemStackBuilder.wool(color)
                    .withName(color.name().charAt(0) + color.name().toLowerCase()
                            .substring(1).replace("_", " "))
                    .build(), c -> {
                callback.accept(color);
            });

            index++;
            if ((index + 1) % 9 == 0) {
                index += 2;
            }
        }

        menu.setItem(49, backItem(player), c -> {
            back.run();
        });

        menu.open(player);
    }

    private static void profileList(Player player, Runnable back) {
        ISpigotPaginatedMenuBuilder bmenu = SpigotBrickGUI.paginatedBuilder();
        setup(bmenu, player, back);

        List<Profile> profiles = new ArrayList<>(SpigotClanAPI.get().cachedProfiles());
        bmenu.withTitle(index -> namespace.string(player, "menu.profileList.title", index + 1, profiles.size()));

        bmenu.withItems(profiles.size(), index -> {
            Profile profile = profiles.get(index);
            ItemStack profileItem = ItemStackBuilder.skull().withPlayer(profile.id())
                    .withName(Component.text(profile.name(), NamedTextColor.WHITE))
                    .withLore(namespace.string(player, "menu.profileList.profile.lore", profile.name()))
                    .build();
            return new SpigotMenuItem(profileItem, c -> {
                profile(player, profile, () -> profileList(player, back));
            });
        });

        bmenu.build().open(player);
    }

    private static String format(Player player, Instant time) {
        return time.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }

    private static String format(Player player, Duration duration) {
        String result = duration.toString().substring(2);
        int index = result.indexOf(".");
        if (index > 0) {
            result = result.substring(0, index);
        }

        result = result.replace("H", "h ");
        result = result.replace("M", "m ");
        result = result.replace("S", "s");
        return result;
    }

    public static void profile(Player player, Profile target, Runnable back) {
        ISpigotMenuBuilder bmenu = SpigotBrickGUI.builder();
        bmenu.withTitle(namespace.string(player, "menu.profile.title", target.name()));

        ItemStack infoItem = ItemStackBuilder.skull().withPlayer(target.id())
                .withName(Component.text(target.name(), NamedTextColor.WHITE))
                .withLore(namespace.string(player, "menu.profile.info.lore",
                        format(player, target.createdAt())))
                .build();
        bmenu.withItem(infoItem);

        Clan clan = target.clanProfile().map(ClanProfile::clan).orElse(null);
        if (clan != null) {
            ItemStack clanItem = ItemStackBuilder.of(ClanTools.crest(clan))
                    .withName(clan.displayName())
                    .withLore(namespace.string(player, "menu.profile.clan.lore", clan.name()))
                    .build();
            bmenu.withItem(clanItem, c -> {
                clan(player, clan, () -> profile(player, target, back));
            });

            if (hasPermission(player, clan, ClanPermission.KICK_MEMBER) && !target.id().equals(player.getUniqueId())) {
                ItemStack kickItem = ItemStackBuilder.of(Material.REDSTONE_BLOCK)
                        .withName(namespace.string(player, "menu.profile.kick.name"))
                        .withLore(namespace.string(player, "menu.profile.kick.lore", target.name()))
                        .build();
                bmenu.withItem(kickItem, c -> {
                    kickMember(player, target, () -> profile(player, target, back));
                });
            }

            // TODO
//            ClanProfile cp = ClanTools.clanProfile(player).orElse(null);
//            if (cp != null && cp.isLeader() && !target.id().equals(player.getUniqueId())) {
//                ItemStack changePermissionItem = ItemStackBuilder.of(Material.COMMAND_BLOCK)
//                        .withName(namespace.string(player, "menu.profile.change-permissions.name"))
//                        .withLore(namespace.string(player, "menu.profile.change-permissions.lore", target.name()))
//                        .build();
//                bmenu.withItem(changePermissionItem, c -> {
//                    changePlayerPermissions(player, cp, () -> profile(player, target, back));
//                });
//            }
        } else {
            Clan pclan = ClanTools.clan(player).orElse(null);
            if (pclan != null && hasPermission(player, pclan, ClanPermission.INVITE_PLAYER)) {
                ItemStack inviteItem = ItemStackBuilder.of(Material.WRITABLE_BOOK)
                        .withName(namespace.string(player, "menu.profile.invite.name"))
                        .withLore(namespace.string(player, "menu.profile.invite.lore", target.name()))
                        .build();
                bmenu.withItem(inviteItem, c -> {
                    invitePlayer(player, target, () -> profile(player, target, back));
                });
            }
        }

        bmenu.withHotbarItem(4, backItem(player), c -> {
            back.run();
        });

        bmenu.build().open(player);
    }

    private static void invitePlayer(Player player, Profile profile, Runnable back) {
        SpigotBrickGUI.confirmationBuilder()
                .withTitle(namespace.string(player, "menu.profile.invite.confirm.title", profile.name()))
                .withDisplay(ItemStackBuilder.skull().withPlayer(profile.id())
                        .withName(Component.text(profile.name(), NamedTextColor.WHITE))
                        .build())
                .withAccept(() -> {
                    player.chat("/clans invite " + profile.name());
                    back.run();
                })
                .withDeny(back)
                .build().open(player);
    }

    private static final Map<ClanPermission, Material> materials = Map.of(
            ClanPermission.CHANGE_CREST, Material.WHITE_BANNER,
            ClanPermission.INVITE_PLAYER, Material.PLAYER_HEAD,
            ClanPermission.KICK_MEMBER, Material.IRON_SWORD,
            ClanPermission.ACCESS_STORAGE, Material.CHEST,
            ClanPermission.ACCESS_VAULT, Material.EMERALD
    );

    public static void changePlayerPermissions(Player player, ClanProfile cp, Runnable back) {
        ISpigotMenu menu = SpigotBrickGUI.create(45, namespace.string(player, "menu.clan.permissions.title", cp.profile().name()));

        int index = 11;
        for (ClanPermission perm : ClanPermission.values()) {
            String statusKey = "menu.clan.permissions." + cp.hasPermission(perm);
            ItemStack item = ItemStackBuilder.of(materials.get(perm))
                    .withName(namespace.string(player, "menu." + perm.i18nKey()))
                    .withLore(namespace.string(player, "menu.clan.permissions.status", namespace.string(player, statusKey)))
                    .apply(cp.hasPermission(perm), b -> {
                        b.withEnchantment(Enchantment.SILK_TOUCH);
                        b.withItemFlag(ItemFlag.HIDE_ENCHANTS);
                    })
                    .build();

            menu.setItem(index, item, click -> {
                if (cp.hasPermission(perm)) {
                    cp.removePermission(perm);
                } else {
                    cp.addPermission(perm);
                }
                ClanAPI.get().update(cp);
                changePlayerPermissions(player, cp, back);
            });

            index++;
            if ((index + 2) % 9 == 0) {
                index += 4;
            }
        }

        menu.setItem(40, backItem(player), c -> {
            back.run();
        });

        menu.open(player);
    }

}
