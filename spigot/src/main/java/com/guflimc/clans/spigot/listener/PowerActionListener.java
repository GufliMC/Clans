package com.guflimc.clans.spigot.listener;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.*;
import com.guflimc.clans.api.domain.ClanProfile;
import com.guflimc.clans.api.domain.Nexus;
import com.guflimc.clans.api.domain.Profile;
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

import java.util.Collection;

public class PowerActionListener implements Listener {

    private int price(Block block) {
        Material type = block.getType();
        if (type.toString().contains("CHEST")) {
            return 5;
        }
        return 1;
    }

    private int price(Inventory inventory) {
        InventoryType type = inventory.getType();
        if ( type == InventoryType.CHEST ) {
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

    private void handle(Player player, Collection<Region> regions, int price, Cancellable cancellable) {
        Nexus nexus = (Nexus) regions.stream()
                .filter(rg -> rg instanceof Nexus)
                .findAny().orElse(null);

        Profile profile = SpigotClanAPI.get().findCachedProfile(player.getUniqueId()).orElseThrow();

        // if nexus area is of same clan as player's clan
        if (nexus != null && profile.clanProfile().map(ClanProfile::clan).map(c -> c.equals(nexus.clan())).orElse(false)) {
            return;
        }

        if (price > profile.power()) {
            SpigotI18nAPI.get(this).send(player, "power.action.not-enough");
            cancellable.setCancelled(true);
            return;
        }

        profile.setPower(profile.power() - price);
        SpigotI18nAPI.get(this).send(player, "power.action.pay",  price);
    }

}
