package com.guflimc.clans.spigot;

import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.clans.api.cosmetic.CrestType;
import com.guflimc.clans.api.cosmetic.NexusSkin;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Nexus;
import com.guflimc.clans.common.AbstractClanManager;
import com.guflimc.clans.common.ClansDatabaseContext;
import com.guflimc.clans.common.domain.DClan;
import com.guflimc.clans.spigot.api.SpigotClanManager;
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
import org.bukkit.Color;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SpigotBrickClanManager extends AbstractClanManager implements SpigotClanManager {

    private final Map<NexusSkin, Clipboard> schematics = new HashMap<>();

    public SpigotBrickClanManager(ClansDatabaseContext databaseContext) {
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

        addCrestTemplate("Cross", CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.CROSS));
        addCrestTemplate("Straight Cross", CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.STRAIGHT_CROSS));
        addCrestTemplate("Circle", CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.CIRCLE_MIDDLE));
        addCrestTemplate("Creeper", CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.CREEPER));
        addCrestTemplate("Globe", CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.GLOBE));
        addCrestTemplate("Flower", CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.FLOWER));
        addCrestTemplate("Black Sun",
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.FLOWER, CrestType.Pattern.RHOMBUS_MIDDLE),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.CIRCLE_MIDDLE)
        );

        //

        addCrestTemplate("Wither Face", true,
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.PIGLIN, CrestType.Pattern.FLOWER),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.CURLY_BORDER, CrestType.Pattern.STRIPE_BOTTOM)
        );

        addCrestTemplate("Skull", true,
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.SKULL)
        );

        addCrestTemplate("Squid", true,
                CrestType.Color.FOREGROUND,
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.TRIANGLE_BOTTOM),
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.STRIPE_SMALL),
                CrestType.CrestLayer.of(CrestType.Color.BLACK, CrestType.Pattern.STRIPE_MIDDLE),
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.CIRCLE_MIDDLE),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.CURLY_BORDER)
        );

        addCrestTemplate("Wolf", true,
                CrestType.Color.WHITE,
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.RHOMBUS_MIDDLE),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.CURLY_BORDER),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.CIRCLE_MIDDLE),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.CREEPER),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.TRIANGLE_TOP),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.TRIANGLES_TOP)
        );

        addCrestTemplate("Pirate", true,
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.SKULL),
                CrestType.CrestLayer.of(CrestType.Color.ACCENT, CrestType.Pattern.STRIPE_TOP),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.CURLY_BORDER),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.TRIANGLES_TOP)
        );

        addCrestTemplate("Angel", true,
                CrestType.Color.ACCENT,
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.GRADIENT),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.CURLY_BORDER),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.CROSS),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.CIRCLE_MIDDLE),
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.FLOWER),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.TRIANGLE_TOP)
        );

        addCrestTemplate("Holy", true,
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.STRAIGHT_CROSS),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.BORDER),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.STRIPE_TOP),
                CrestType.CrestLayer.of(CrestType.Color.BACKGROUND, CrestType.Pattern.GRADIENT_UP)
        );

        addCrestTemplate("Supercross", true,
                CrestType.CrestLayer.of(CrestType.Color.BLACK, CrestType.Pattern.STRIPE_MIDDLE),
                CrestType.CrestLayer.of(CrestType.Color.BLACK, CrestType.Pattern.STRIPE_CENTER),
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.STRAIGHT_CROSS),
                CrestType.CrestLayer.of(CrestType.Color.BLACK, CrestType.Pattern.FLOWER),
                CrestType.CrestLayer.of(CrestType.Color.FOREGROUND, CrestType.Pattern.CIRCLE_MIDDLE)
        );
    }

    private void addCrestTemplate(String name, boolean restricted, CrestType.Color background, CrestType.CrestLayer... layers) {
        if (crestTemplates().stream().anyMatch(ct -> ct.name().equals(name))) {
            return;
        }
        addCrestTemplate(name, CrestType.of(background, layers), restricted);
    }

    private void addCrestTemplate(String name, CrestType.Color background, CrestType.CrestLayer... layers) {
        addCrestTemplate(name, false, background, layers);
    }

    private void addCrestTemplate(String name, boolean restricted, CrestType.CrestLayer... layers) {
        addCrestTemplate(name, restricted, CrestType.Color.BACKGROUND, layers);
    }

    private void addCrestTemplate(String name, CrestType.CrestLayer... layers) {
        addCrestTemplate(name, false, CrestType.Color.BACKGROUND, layers);
    }

    //

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
