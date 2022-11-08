package com.guflimc.clans.spigot.menu;

import com.guflimc.brick.gui.spigot.SpigotBrickGUI;
import com.guflimc.brick.gui.spigot.api.ISpigotMenu;
import com.guflimc.brick.gui.spigot.api.ISpigotMenuBuilder;
import com.guflimc.brick.gui.spigot.api.ISpigotPaginatedMenuBuilder;
import com.guflimc.brick.gui.spigot.item.ItemStackBuilder;
import com.guflimc.brick.gui.spigot.menu.SpigotMenuItem;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.*;
import com.guflimc.clans.spigot.SpigotClans;
import com.guflimc.clans.spigot.api.SpigotClanAPI;
import com.guflimc.clans.spigot.util.ClanTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
            ItemStack clanItem = ItemStackBuilder.of(ClanTools.sigil(clan))
                    .withName(clan.displayName())
                    .withLore(namespace.string(player, "menu.main.clan.lore"))
                    .build();
            bmenu.withItem(clanItem, (e) -> {
                clan(player, clan);
            });
        }

        ItemStack clanListItem = ItemStackBuilder.of(Material.BOOK)
                .withName(namespace.string(player, "menu.main.clanList.name"))
                .withLore(namespace.string(player, "menu.main.clanList.lore"))
                .build();
        bmenu.withItem(clanListItem, (e) -> {
            clanList(player);
        });

        // profiles
        ItemStack profileListItem = ItemStackBuilder.of(Material.BOOK)
                .withName(namespace.string(player, "menu.main.profileList.name"))
                .withLore(namespace.string(player, "menu.main.profileList.lore"))
                .build();
        bmenu.withItem(profileListItem, (e) -> {
            profileList(player);
        });

        ItemStack profileItem = ItemStackBuilder.skull().withPlayer(player.getPlayerProfile())
                .withName(Component.text(player.getName(), NamedTextColor.WHITE))
                .withLore(namespace.string(player, "menu.main.profile.lore"))
                .build();
        bmenu.withItem(profileItem, (e) -> {
            profile(player, profile);
        });

        bmenu.build().open(player);
    }

    private static void clanList(Player player) {
        ISpigotPaginatedMenuBuilder bmenu = SpigotBrickGUI.paginatedBuilder();
        setup(bmenu, player, () -> open(player));

        List<Clan> clans = new ArrayList<>(SpigotClanAPI.get().clans());
        bmenu.withTitle(index -> namespace.string(player, "menu.clanList.title", index + 1, clans.size()));

        bmenu.withItems(clans.size(), index -> {
            Clan clan = clans.get(index);
            ItemStack clanItem = ItemStackBuilder.of(ClanTools.sigil(clan))
                    .withName(clan.displayName())
                    .withLore(namespace.string(player, "menu.clanList.clan.lore", clan.name()))
                    .build();
            return new SpigotMenuItem(clanItem, c -> clan(player, clan));
        });

        bmenu.build().open(player);
    }


    public static void clan(Player player, Clan clan) {
        ISpigotMenuBuilder bmenu = SpigotBrickGUI.builder();
        bmenu.withTitle(namespace.string(player, "menu.clan.title", clan.name()));

        ItemStack infoItem = ItemStackBuilder.of(ClanTools.sigil(clan))
                .withName(clan.displayName())
                .withLore(namespace.string(player, "menu.clan.info.lore", clan.createdAt()))
                .build();
        bmenu.withItem(infoItem);

        ItemStack membersItem = ItemStackBuilder.skull()
                .withName(namespace.string(player, "menu.clan.members.name", clan.memberCount(), clan.maxMembers()))
                .withLore(namespace.string(player, "menu.clan.members.lore", clan.name()))
                .build();
        bmenu.withItem(membersItem, c -> {
            clanMembers(player, clan);
        });

        if (hasPermission(player, clan, ClanPermission.ACCESS_STORAGE)) {
            ItemStack storageItem = ItemStackBuilder.of(Material.CHEST)
                    .withName(namespace.string(player, "menu.clan.storage.name"))
                    .withLore(namespace.string(player, "menu.clan.storage.lore", clan.name()))
                    .build();
            bmenu.withItem(storageItem, (e) -> {
                clanStorage(player, clan);
            });
        }

        if (hasPermission(player, clan, ClanPermission.ACCESS_VAULT)) {
            ItemStack vaultItem = ItemStackBuilder.of(Material.ENDER_CHEST)
                    .withName(namespace.string(player, "menu.clan.vault.name"))
                    .withLore(namespace.string(player, "menu.clan.vault.lore", clan.name()))
                    .build();
            bmenu.withItem(vaultItem, (e) -> {
                clanVault(player, clan);
            });
        }

        if (hasPermission(player, clan, ClanPermission.CHANGE_BANNER)) {
            ItemStack bannerItem = ItemStackBuilder.banner(ClanTools.findBestDye(clan.color()))
                    .withName(namespace.string(player, "menu.clan.change-banner.name"))
                    .withLore(namespace.string(player, "menu.clan.change-banner.lore"))
                    .build();
            bmenu.withItem(bannerItem, (e) -> {
                clanEditBanner(player, clan);
            });
        }


        bmenu.build().open(player);
    }

    private static void clanStorage(Player player, Clan clan) {

    }

    private static void clanVault(Player player, Clan clan) {

    }

    private static void clanMembers(Player player, Clan clan) {
        ISpigotPaginatedMenuBuilder bmenu = SpigotBrickGUI.paginatedBuilder();
        setup(bmenu, player, () -> clan(player, clan));

        SpigotClanAPI.get().profiles(clan).thenAccept(profiles -> {
            bmenu.withTitle(index -> namespace.string(player, "menu.clan.members.title", clan.name(), index + 1, profiles.size()));

            bmenu.withItems(profiles.size(), index -> {
                Profile profile = profiles.get(index);
                ItemStack profileItem = ItemStackBuilder.skull().withPlayer(profile.id(), profile.name())
                        .withName(Component.text(profile.name(), NamedTextColor.WHITE))
                        .withLore(namespace.string(player, "menu.clan.members.profile.lore", profile.name()))
                        .build();
                return new SpigotMenuItem(profileItem, c -> {
                    if (c.isRightClick()) {
                        kickMember(player, profile);
                    } else {
                        profile(player, profile);
                    }
                });
            });

            bmenu.build().open(player);
        });
    }

    private static void kickMember(Player player, Profile profile) {
        SpigotBrickGUI.confirmationBuilder()
                .withTitle(namespace.string(player, "menu.profile.kick.confirm.title", profile.name()))
                .withDisplay(ItemStackBuilder.skull().withPlayer(profile.id(), profile.name())
                        .withName(Component.text(profile.name(), NamedTextColor.WHITE))
                        .build())
                .withAccept(() -> {
                    player.chat("/clans kick " + profile.name());
                })
                .withDeny(() -> {
                    profile(player, profile);
                })
                .build().open(player);
    }

    private static void clanEditBanner(Player player, Clan clan) {
        ISpigotMenu menu = SpigotBrickGUI.create(36, namespace.string(player, "menu.clan.change-sigil.title", clan.name()));

        DyeColor foreground = DyeColor.values()[clan.sigilColor()];
        ItemStack colorItem = ItemStackBuilder.wool(foreground)
                .withName(namespace.string(player, "menu.clan.change-sigil.color.name"))
                .withLore(namespace.string(player, "menu.clan.change-sigil.color.lore"))
                .build();
        menu.setItem(12, colorItem, (e) -> {
            clanEditBannerColor(player, clan);
        });

        ItemStack patternItem = ItemStackBuilder.of(ClanTools.sigil(clan))
                .withName(namespace.string(player, "menu.clan.change-sigil.pattern.name"))
                .withLore(namespace.string(player, "menu.clan.change-sigil.pattern.lore"))
                .build();
        menu.setItem(14, patternItem, (e) -> {
            clanEditBannerPattern(player, clan);
        });

        menu.setItem(31, backItem(player), c -> {
            clan(player, clan);
        });

        menu.open(player);
    }

    private static void clanEditBannerPattern(Player player, Clan clan) {
        ISpigotPaginatedMenuBuilder bmenu = SpigotBrickGUI.paginatedBuilder();
        setup(bmenu, player, () -> clanEditBanner(player, clan));
        bmenu.withTitle(index -> namespace.string(player, "menu.clan.change-sigil.pattern.title", clan.name(), index + 1, 1));

        DyeColor background = ClanTools.findBestDye(clan.color());
        DyeColor foreground = DyeColor.values()[clan.sigilColor()];

        List<SigilType> sigilTypes = new ArrayList<>(SpigotClanAPI.get().sigilTypes());
        bmenu.withItems(sigilTypes.size(), index -> {
            SigilType sigilType = sigilTypes.get(index);
            return new SpigotMenuItem(ClanTools.sigil(sigilType, background, foreground), c -> {
                clan.setSigilType(sigilType);
                clanEditBanner(player, clan);
            });
        });

        bmenu.build().open(player);
    }

    private static void clanEditBannerColor(Player player, Clan clan) {
        ISpigotMenu menu = SpigotBrickGUI.create(54, namespace.string(player, "menu.clan.change-sigil.color.title", clan.name()));

        int index = 11;
        for (DyeColor color : DyeColor.values()) {
            menu.setItem(index, ItemStackBuilder.wool(color)
                    .withName(color.name().charAt(0) + color.name().toLowerCase()
                            .substring(1).replace("_", " "))
                    .build(), c -> {
                clan.setSigilColor((byte) color.ordinal());
                clanEditBanner(player, clan);
            });

            index++;
            if ((index + 2) % 9 == 0) {
                index += 4;
            }
        }

        menu.open(player);
    }

    private static void profileList(Player player) {
        ISpigotPaginatedMenuBuilder bmenu = SpigotBrickGUI.paginatedBuilder();
        setup(bmenu, player, () -> open(player));

        List<Profile> profiles = new ArrayList<>(SpigotClanAPI.get().cachedProfiles());
        bmenu.withTitle(index -> namespace.string(player, "menu.profileList.title", index + 1, profiles.size()));

        bmenu.withItems(profiles.size(), index -> {
            Profile profile = profiles.get(index);
            ItemStack profileItem = ItemStackBuilder.skull().withPlayer(profile.id(), profile.name())
                    .withName(Component.text(profile.name(), NamedTextColor.WHITE))
                    .withLore(namespace.string(player, "menu.profileList.profile.lore", profile.createdAt(), profile.power(), profile.playTime()))
                    .build();
            return new SpigotMenuItem(profileItem, c -> {
                if (c.isRightClick()) {
                    invitePlayer(player, profile);
                } else {
                    profile(player, profile);
                }
            });
        });

        bmenu.build().open(player);
    }

    public static void profile(Player player, Profile target) {
        ISpigotMenuBuilder bmenu = SpigotBrickGUI.builder();
        bmenu.withTitle(namespace.string(player, "menu.profile.title", target.name()));

        ItemStack infoItem = ItemStackBuilder.skull().withPlayer(target.id(), target.name())
                .withName(Component.text(target.name(), NamedTextColor.WHITE))
                .withLore(namespace.string(player, "menu.profile.info.lore", target.name()))
                .build();
        bmenu.withItem(infoItem);

        Clan clan = target.clanProfile().map(ClanProfile::clan).orElse(null);
        if (clan != null) {
            ItemStack clanItem = ItemStackBuilder.banner(ClanTools.findBestDye(clan.color()))
                    .withName(clan.displayName())
                    .withLore(namespace.string(player, "menu.profile.clan.lore", clan.name()))
                    .build();
            bmenu.withItem(clanItem, c -> {
                clan(player, clan);
            });

            if (hasPermission(player, clan, ClanPermission.KICK_MEMBER)) {
                ItemStack kickItem = ItemStackBuilder.of(Material.REDSTONE_BLOCK)
                        .withName(namespace.string(player, "menu.profile.kick.name"))
                        .withLore(namespace.string(player, "menu.profile.kick.lore", target.name()))
                        .build();
                bmenu.withItem(kickItem, c -> {
                    kickMember(player, target);
                });
            }
        } else {
            Clan pclan = ClanTools.clan(player).orElse(null);
            if (pclan != null && hasPermission(player, pclan, ClanPermission.INVITE_PLAYER)) {
                ItemStack inviteItem = ItemStackBuilder.of(Material.WRITABLE_BOOK)
                        .withName(namespace.string(player, "menu.profile.invite.name"))
                        .withLore(namespace.string(player, "menu.profile.invite.lore", target.name()))
                        .build();
                bmenu.withItem(inviteItem, c -> {
                    invitePlayer(player, target);
                });
            }
        }

        bmenu.build().open(player);
    }

    private static void invitePlayer(Player player, Profile profile) {
        SpigotBrickGUI.confirmationBuilder()
                .withTitle(namespace.string(player, "menu.profile.invite.confirm.title", profile.name()))
                .withDisplay(ItemStackBuilder.skull().withPlayer(profile.id(), profile.name())
                        .withName(Component.text(profile.name(), NamedTextColor.WHITE))
                        .build())
                .withAccept(() -> {
                    player.chat("/clans invite " + profile.name());
                })
                .withDeny(() -> {
                    profile(player, profile);
                })
                .build().open(player);
    }

}
