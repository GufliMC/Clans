package com.guflimc.clans.spigot.listener;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.*;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.Nexus;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.spigot.SpigotClans;
import com.guflimc.clans.spigot.api.SpigotClanAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PowerActionListener implements Listener {

    private final Map<Inventory, Instant> inventoryCache = new HashMap<>();
    private final SpigotClans plugin;

    public PowerActionListener(SpigotClans plugin) {
        this.plugin = plugin;

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            new HashSet<>(inventoryCache.keySet()).stream()
                    .filter(inv -> !isRecent(inventoryCache.get(inv)))
                    .forEach(inventoryCache::remove);
        }, 1L, 20 * 60);
    }

    //

    private int price(Block block) {
        Material type = block.getType();
        if (type.toString().contains("CHEST")) {
            return 5;
        }
        return 1;
    }

    private int price(Inventory inventory) {
        InventoryType type = inventory.getType();
        if (type == InventoryType.CHEST) {
            return 5;
        }
        return 1;
    }

    //

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(PlayerRegionsBlockPlaceEvent event) {
        handle(event, price(event.block()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(PlayerRegionsBlockBreakEvent event) {
        handle(event, price(event.block()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onContainerOpen(PlayerRegionsContainerOpenEvent event) {
        handle(event, price(event.entity()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockInteract(PlayerRegionsBlockInteractEvent event) {
        handle(event, 1);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityInteract(PlayerRegionsEntityInteractEvent event) {
        handle(event, 1);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityPlace(PlayerRegionsEntityPlaceEvent event) {
        handle(event, 1);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(PlayerRegionsEntityDamageEvent event) {
        handle(event, 1);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityBreak(PlayerRegionsEntityBreakEvent event) {
        handle(event, 2);
    }

    //

    private void handle(PlayerRegionsEvent event, int price) {
        handle(event.player(), event.regions(), price, event);
    }

    private void handle(Player player, Collection<Region> regions, int price, Cancellable event) {
        Nexus nexus = (Nexus) regions.stream()
                .filter(rg -> rg instanceof Nexus)
                .findAny().orElse(null);

        Profile profile = SpigotClanAPI.get().findCachedProfile(player.getUniqueId()).orElseThrow();

        // if nexus area is of same clan as player's clan
        if (nexus != null && profile.clanProfile().map(ClanProfile::clan).map(c -> c.equals(nexus.clan())).orElse(false)) {
            return;
        }

        if (event instanceof PlayerRegionsBlockEvent be
                && be.block().hasMetadata("POWER")
                && isRecent((Instant) be.block().getMetadata("POWER").get(0).value())) {
            return;
        } else if (event instanceof PlayerRegionsContainerOpenEvent ce
                && inventoryCache.containsKey(ce.entity())
                && isRecent(inventoryCache.get(ce.entity()))) {
            return;
        }

        if (price > profile.power()) {
            SpigotI18nAPI.get(this).send(player, "power.action.not-enough");
            event.setCancelled(true);
            return;
        }

        profile.setPower(profile.power() - price);
        SpigotI18nAPI.get(this).send(player, "power.action.pay", price);

        if (event instanceof PlayerRegionsBlockEvent be) {
            be.block().setMetadata("POWER", new FixedMetadataValue(plugin, Instant.now()));
        } else if (event instanceof PlayerRegionsContainerOpenEvent ce) {
            inventoryCache.put(ce.entity(), Instant.now());
        }

    }

    private boolean isRecent(Instant instant) {
        return instant.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
    }

}
