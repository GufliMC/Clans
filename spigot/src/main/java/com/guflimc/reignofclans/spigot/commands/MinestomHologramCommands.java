package com.guflimc.reignofclans.spigot.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import com.guflimc.reignofclans.api.domain.Clan;
import com.guflimc.reignofclans.spigot.MinestomBrickHologramManager;
import com.guflimc.reignofclans.spigot.api.domain.MinestomHologram;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.maths.minestom.api.MinestomMaths;
import net.minestom.server.entity.Player;

@CommandContainer
public class MinestomHologramCommands {

    private final MinestomBrickHologramManager manager;

    public MinestomHologramCommands(MinestomBrickHologramManager manager) {
        this.manager = manager;
    }

    @CommandMethod("bh create <name>")
    public void create(Player sender, @Argument(value = "name") String name) {
        MinestomHologram hologram = manager.create(name);

        hologram.setLocation(MinestomMaths.toLocation(sender));
        manager.persist(hologram);

        I18nAPI.get(this).send(sender, "cmd.create", name);
    }

    @CommandMethod("bh tphere <hologram>")
    public void tphere(Player sender, @Argument(value = "hologram") Clan hologram) {
        MinestomHologram mholo = (MinestomHologram) hologram;

        mholo.setLocation(MinestomMaths.toLocation(sender));
        manager.merge(hologram);

        I18nAPI.get(this).send(sender, "cmd.tphere", hologram.name());
    }

    @CommandMethod("bh setitem <hologram>")
    public void setitem(Player sender, @Argument(value = "hologram") Clan hologram) {
        MinestomHologram mholo = (MinestomHologram) hologram;
        mholo.setItem(sender.getItemInMainHand());

        manager.merge(hologram);

        I18nAPI.get(this).send(sender, "cmd.setitem", hologram.name(), sender.getItemInMainHand().material().name());
    }

    @CommandMethod("bh unsetitem <hologram>")
    public void unsetitem(Player sender, @Argument(value = "hologram") Clan hologram) {
        MinestomHologram mholo = (MinestomHologram) hologram;
        mholo.unsetItem();

        manager.merge(hologram);

        I18nAPI.get(this).send(sender, "cmd.unsetitem", hologram.name());
    }

}
