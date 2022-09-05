package com.guflimc.lavaclans.spigot;

import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.lavaclans.api.ClanAPI;
import com.guflimc.lavaclans.common.LavaClansConfig;
import com.guflimc.lavaclans.common.LavaClansDatabaseContext;
import com.guflimc.lavaclans.common.LavaClansManager;
import com.guflimc.lavaclans.spigot.commands.LavaClansCommands;
import com.guflimc.lavaclans.spigot.listener.JoinQuitListener;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class LavaClans extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger(LavaClans.class);

    public final Gson gson = new Gson();

    public LavaClansManager manager;
    public LavaClansConfig config;
    public BukkitAudiences adventure;

    @Override
    public void onEnable() {
        try (
                InputStream is = getResource("config.json");
                InputStreamReader isr = new InputStreamReader(is)
        ) {
            config = gson.fromJson(isr, LavaClansConfig.class);
        } catch (IOException e) {
            logger.error("Cannot load configuration.", e);
            return;
        }

        // ADVENTURE
        adventure = BukkitAudiences.create(this);

        // DATABASE
        LavaClansDatabaseContext databaseContext = new LavaClansDatabaseContext(config.database);

        // LAVA CLANS MANAGER
        manager = new LavaClansManager(databaseContext);
        ClanAPI.register(manager);

        // TRANSLATIONS
        SpigotNamespace namespace = new SpigotNamespace(this, Locale.ENGLISH);
        namespace.loadValues(this, "languages");
        SpigotI18nAPI.get().register(namespace);

        // COMMANDS
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.GRAY, ChatColor.GREEN, ChatColor.DARK_GREEN);
        commandManager.getCommandContexts().registerIssuerOnlyContext(Audience.class,
                ctx -> adventure.player(ctx.getPlayer()));
        commandManager.getCommandReplacements().addReplacement("rootCommand", config.rootCommand);

        commandManager.registerCommand(new LavaClansCommands(this));

        // EVENTS

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitListener(this), this);

        // LOAD

        CompletableFuture.allOf(Bukkit.getServer().getOnlinePlayers().stream()
                .map(p -> manager.load(p.getUniqueId(), p.getName()))
                .toArray(CompletableFuture[]::new)).join();

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