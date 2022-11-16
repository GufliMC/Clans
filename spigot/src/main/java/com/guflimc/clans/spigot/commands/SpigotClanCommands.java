package com.guflimc.clans.spigot.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.area.CuboidArea;
import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.attributes.RegionAttributes;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Nexus;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.spigot.SpigotClans;
import com.guflimc.clans.spigot.api.SpigotClanAPI;
import com.guflimc.clans.spigot.menu.ClanMenu;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

//@CommandContainer
public class SpigotClanCommands {

    private final SpigotClans plugin;

    public SpigotClanCommands(SpigotClans plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("clans menu")
    @CommandPermission("clans.menu")
    public void menu(Player sender) {
        ClanMenu.open(sender);
    }

    // TODO
//    @CommandMethod("clans info <input>")
//    @CommandPermission("lavaclans.clans.menu")
//    public void menu(Player sender, @Argument("input") String input) {
//        Clan clan = SpigotClanAPI.get().findClan(input).orElse(null);
//        if (clan != null) {
//            ClanMenu.clan(sender, clan);
//            return;
//        }
//
//        SpigotClanAPI.get().findProfile(input).thenAccept(profile -> {
//            ClanMenu.profile(sender, profile);
//        }).exceptionally(v -> {
//            SpigotI18nAPI.get(this).send(sender, "cmd.clans.info.not-found");
//            return null;
//        });
//    }

    @CommandMethod("clans nexus")
    @CommandPermission("clans.nexus")
    public void nexus(Player sender, Profile sprofile) {
        if (sprofile.clanProfile().isEmpty()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.error.base.not.in.clan");
            return;
        }

        if (!sprofile.clanProfile().get().isLeader()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.perms.error.not.leader");
            return;
        }

        Clan clan = sprofile.clanProfile().get().clan();

        Location loc = sender.getLocation();
        com.guflimc.brick.maths.api.geo.pos.Location nexus = SpigotMaths.toBrickLocation(loc.add(0, 1.5, 0));

//        if ( SpigotRegionAPI.get().regions().stream()
//                .filter(rg -> rg instanceof Nexus)
//                .map(rg -> (Nexus) rg)
//                .anyMatch(rg -> isTooClose(rg, nexus)) ) {
//            SpigotI18nAPI.get(this).send(sender, "cmd.clans.nexus.error.too.close");
//            return;
//        }

//        if (nexus.y() < 10) {
//            SpigotI18nAPI.get(this).send(sender, "cmd.clans.nexus.error.too.low");
//            return;
//        }

        int maxLevelRadius = RegionAttributes.CUBE_RADIUS_MULTIPLIER * 3;
        Area area = CuboidArea.of(nexus.add(-maxLevelRadius, -maxLevelRadius, -maxLevelRadius),
                nexus.add(maxLevelRadius, maxLevelRadius, maxLevelRadius));
        if (!SpigotRegionAPI.get().intersecting(area).isEmpty()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.nexus.error.too.close");
            return;
        }

        try {
            ClanAPI.get().createNexus(clan, nexus);

            Vector dir = cardinalDirectionFrom(loc);
            sender.teleport(loc.getBlock().getLocation().add(0.5, 0, 0.5).add(dir.clone().multiply(2))
                    .setDirection(sender.getLocation().getDirection()));
            Bukkit.getScheduler().runTaskLater(plugin, () -> sender.setVelocity(dir.clone().setY(0.1)), 1L);
        } catch (IllegalArgumentException ex) {
            SpigotI18nAPI.get(this).send(sender, "cmd.clans.nexus.error.invalid");
        }
    }

    private boolean isTooClose(Nexus nexus, com.guflimc.brick.maths.api.geo.pos.Location location) {
        double distance = nexus.location().distance(location);
        return distance < 12 * RegionAttributes.CUBE_RADIUS_MULTIPLIER; // 2 clans * 3 levels * 2 radius
    }

    private Vector cardinalDirectionFrom(Location loc) {
        float yaw = loc.getYaw() + 180;
        if (yaw > 45 && yaw < 135) {
            return new Vector(-1, 0, 0);
        } else if (yaw > 135 && yaw < 225) {
            return new Vector(0, 0, -1);
        } else if (yaw > 225 && yaw < 315) {
            return new Vector(1, 0, 0);
        } else {
            return new Vector(0, 0, 1);
        }
    }

}
