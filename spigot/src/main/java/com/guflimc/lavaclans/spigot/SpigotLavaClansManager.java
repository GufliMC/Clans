package com.guflimc.lavaclans.spigot;

import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.lavaclans.api.cosmetic.NexusSkin;
import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.Nexus;
import com.guflimc.lavaclans.common.AbstractLavaClansManager;
import com.guflimc.lavaclans.common.LavaClansDatabaseContext;
import com.guflimc.lavaclans.common.domain.DClan;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SpigotLavaClansManager extends AbstractLavaClansManager {

    private final Map<NexusSkin, Clipboard> schematics = new HashMap<>();

    public SpigotLavaClansManager(LavaClansDatabaseContext databaseContext) {
        super(databaseContext);

        ClipboardFormat format = ClipboardFormats.findByAlias("sponge");
        for (NexusSkin skin : NexusSkin.values()) {
            try (ClipboardReader reader = format.getReader(getClass().getClassLoader()
                    .getResourceAsStream("schematics/nexus.schem"))) {
                schematics.put(skin, reader.read());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public CompletableFuture<Void> createNexus(@NotNull Clan clan, Location location) {
        Clipboard schematic = schematics.get(clan.nexus().map(Nexus::skin).orElse(NexusSkin.DEFAULT));

        // convert loc to bukkit location
        org.bukkit.Location bloc = SpigotMaths.toSpigotLocation(location);

        // verify that the nexus can be placed
        for (BlockVector3 bv : schematic.getRegion()) {
            BlockVector3 orig = bv.subtract(schematic.getOrigin());
            if (bloc.clone().add(orig.getX(), orig.getY(), orig.getZ()).getBlock().getType() != Material.AIR) {
                throw new IllegalArgumentException("Nexus must be placed in an empty area.");
            }
        }

        // remove old nexus physically
        clan.nexus().ifPresent(nexus -> {
            org.bukkit.Location bloc2 = SpigotMaths.toSpigotLocation(nexus.location());
            for (BlockVector3 bv : schematic.getRegion()) {
                BlockVector3 orig = bv.subtract(schematic.getOrigin());
                bloc2.clone().add(orig.getX(), orig.getY(), orig.getZ()).getBlock().setType(Material.AIR);
            }
        });

        // change nexus location of clan
        ((DClan) clan).setNexus(location);

        // place new nexus physically
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld(location.worldId())))) {
            Operation operation = new ClipboardHolder(schematic)
                    .createPaste(editSession)
                    .to(BukkitAdapter.adapt(bloc).toVector().toBlockPoint())
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }

        return update(clan);
    }

}
