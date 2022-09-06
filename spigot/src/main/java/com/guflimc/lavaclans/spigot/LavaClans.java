package com.guflimc.lavaclans.spigot;

import co.aikar.commands.*;
import com.google.gson.Gson;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.lavaclans.api.ClanAPI;
import com.guflimc.lavaclans.api.domain.Clan;
import com.guflimc.lavaclans.api.domain.Profile;
import com.guflimc.lavaclans.common.LavaClansConfig;
import com.guflimc.lavaclans.common.LavaClansDatabaseContext;
import com.guflimc.lavaclans.common.LavaClansManager;
import com.guflimc.lavaclans.spigot.commands.LavaClansCommands;
import com.guflimc.lavaclans.spigot.listener.JoinQuitListener;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.translation.TranslationRegistry;
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
import java.util.stream.Collectors;

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
        setupCommands();

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

    private void setupCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.GRAY, ChatColor.GREEN, ChatColor.DARK_GREEN);

        // REPLACEMENTS
        commandManager.getCommandReplacements().addReplacement("rootCommand", config.rootCommand);

        // CONTEXTS
        CommandContexts<BukkitCommandExecutionContext> ctxs = commandManager.getCommandContexts();

        ctxs.registerIssuerOnlyContext(Audience.class,
                ctx -> adventure.player(ctx.getPlayer()));

        ctxs.registerIssuerOnlyContext(Profile.class,
                ctx -> manager.findCachedProfile(ctx.getPlayer().getUniqueId()));

        ctxs.registerContext(Clan.class, ctx -> {
            String name = ctx.popFirstArg();
            return manager.findClan(name)
                    .orElseThrow(() -> {
                        SpigotI18nAPI.get(this).send(ctx.getPlayer(), "cmd.error.args.clan", name);
                        return new InvalidCommandArgument();
                    });
        });

        // CONDITIONS
        CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> conds
                = commandManager.getCommandConditions();

        conds.addCondition("clan", ctx -> {
           if ( manager.findCachedProfile(ctx.getIssuer().getUniqueId()).clanProfile().isEmpty() ) {
               SpigotI18nAPI.get(this).send(ctx.getIssuer().getPlayer(), "cmd.error.base.not.in.clan");
               throw new ConditionFailedException();
           }
        });

        // COMPLETIONS
        CommandCompletions<BukkitCommandCompletionContext> cmpls = commandManager.getCommandCompletions();

        cmpls.registerCompletion("clan", ctx ->
                manager.clans().stream().map(Clan::name).toList());

        // REGISTER
        commandManager.registerCommand(new LavaClansCommands(this));
    }
}