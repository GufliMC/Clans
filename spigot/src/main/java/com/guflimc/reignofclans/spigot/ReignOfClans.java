package com.guflimc.reignofclans.spigot;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import com.google.gson.Gson;
import com.guflimc.brick.i18n.minestom.api.MinestomI18nAPI;
import com.guflimc.brick.i18n.minestom.api.namespace.MinestomNamespace;
import com.guflimc.cloud.minestom.MinestomCommandManager;
import com.guflimc.reignofclans.api.domain.Clan;
import com.guflimc.reignofclans.common.ReignOfClansConfig;
import com.guflimc.reignofclans.common.ReignOfClansDatabaseContext;
import com.guflimc.reignofclans.common.ReignOfClansManager;
import com.guflimc.reignofclans.common.command.HologramArgument;
import com.guflimc.reignofclans.common.command.HologramCommands;
import com.guflimc.reignofclans.spigot.api.MinestomHologramAPI;
import com.guflimc.reignofclans.spigot.commands.MinestomHologramCommands;
import io.leangen.geantyref.TypeToken;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.function.Function;

public class ReignOfClans extends JavaPlugin {

    private static final Gson gson = new Gson();

    private ReignOfClansDatabaseContext databaseContext;
    private CommandManager<CommandSender> commandManager;

    @Override
    public void onEnable() {
        getLogger().info("Enabling " + nameAndVersion() + ".");

        ReignOfClansConfig config;
        try (
                InputStream is = getResource("config.json");
                InputStreamReader isr = new InputStreamReader(is)
        ) {
            config = gson.fromJson(isr, ReignOfClansConfig.class);
        } catch (IOException e) {
            getLogger().error("Cannot load configuration.", e);
            return;
        }

        // DATABASE
        databaseContext = new ReignOfClansDatabaseContext(config.database);

        ReignOfClansManager manager = new MinestomBrickHologramManager(databaseContext);
        MinestomHologramAPI.setHologramManager(manager);

        // TRANSLATIONS
        MinestomNamespace namespace = new MinestomNamespace(this, Locale.ENGLISH);
        namespace.loadValues(this, "languages");
        MinestomI18nAPI.get().register(namespace);

        // COMMAND MANAGER
        commandManager = new MinestomCommandManager<>(
                CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(),
                Function.identity()
        );

        commandManager.parserRegistry().registerParserSupplier(TypeToken.get(Clan.class), parserParameters ->
                new HologramArgument.HologramParser<>());

        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                commandManager,
                CommandSender.class,
                parameters -> SimpleCommandMeta.empty()
        );

        annotationParser.parse(new HologramCommands(manager));
        annotationParser.parse(new MinestomHologramCommands(manager));

        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, e -> {
            e.getPlayer().setGameMode(GameMode.CREATIVE);
        });

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getDescription().getName() + " v" + getDescription().getVersion();
    }
}