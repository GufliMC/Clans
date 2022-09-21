package com.guflimc.clans.spigot;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import com.google.gson.Gson;
import com.guflimc.brick.chat.spigot.api.SpigotChatAPI;
import com.guflimc.brick.gui.spigot.SpigotBrickGUI;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Clan;
import com.guflimc.clans.api.domain.Profile;
import com.guflimc.clans.common.AbstractClanManager;
import com.guflimc.clans.common.ClansConfig;
import com.guflimc.clans.common.ClansDatabaseContext;
import com.guflimc.clans.common.commands.ClanCommands;
import com.guflimc.clans.common.commands.arguments.ClanArgument;
import com.guflimc.clans.spigot.chat.ClanChatChannel;
import com.guflimc.clans.spigot.listener.PlayerChatListener;
import com.guflimc.clans.spigot.listener.RegionBuildListener;
import com.guflimc.clans.spigot.commands.SpigotClanCommands;
import com.guflimc.clans.spigot.listener.JoinQuitListener;
import com.guflimc.clans.spigot.listener.RegionEnterLeaveListener;
import com.guflimc.clans.spigot.placeholders.ClanPlaceholders;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SpigotClans extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger(SpigotClans.class);

    public final Gson gson = new Gson();

    public AbstractClanManager manager;
    public ClansConfig config;
    public BukkitAudiences adventure;

    @Override
    public void onEnable() {
        saveResource("config.json", false);
        try (
                InputStream is = new FileInputStream(new File(getDataFolder(), "config.json"));
                InputStreamReader isr = new InputStreamReader(is)
        ) {
            config = gson.fromJson(isr, ClansConfig.class);
        } catch (IOException e) {
            logger.error("Cannot load configuration.", e);
            return;
        }

        SpigotBrickGUI.register(this);

        // ADVENTURE
        adventure = BukkitAudiences.create(this);

        // DATABASE
        ClansDatabaseContext databaseContext = new ClansDatabaseContext(config.database);

        // LAVA CLANS MANAGER
        manager = new SpigotClanManager(databaseContext);
        ClanAPI.register(manager);

        // TRANSLATIONS
        SpigotNamespace namespace = new SpigotNamespace(this, Locale.ENGLISH);
        namespace.loadValues(this, "languages");
        SpigotI18nAPI.get().register(namespace);

        // COMMANDS
        setupCommands();

        // PLACEHOLDERS
        ClanPlaceholders.init(config);

        // CHAT
        SpigotChatAPI.get().channelByName("clan").ifPresent(ch -> {
            ClanChatChannel replacement = new ClanChatChannel(ch.name(), ch.activator(), ch.format());
            SpigotChatAPI.get().unregisterChatChannel(ch);
            SpigotChatAPI.get().registerChatChannel(replacement);
            logger.info("Injected custom chat channel for clans.");
        });

        // EVENTS

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitListener(this), this);
        pm.registerEvents(new RegionEnterLeaveListener(this), this);
        pm.registerEvents(new RegionBuildListener(this), this);
        pm.registerEvents(new PlayerChatListener(), this);

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
        // COMMANDS
        try {
            BukkitCommandManager<CommandSender> commandManager = new BukkitCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
//            commandManager.registerBrigadier();

            commandManager.parserRegistry().registerParserSupplier(TypeToken.get(Clan.class),
                    ps -> new ClanArgument.ClanParser<>());

            AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                    commandManager,
                    CommandSender.class,
                    parameters -> SimpleCommandMeta.empty()
            );

            annotationParser.getParameterInjectorRegistry().registerInjector(Profile.class,
                    (context, annotationAccessor) -> manager.findCachedProfile(((Player) context.getSender()).getUniqueId()));

            annotationParser.parse(new ClanCommands(adventure));
            annotationParser.parse(new SpigotClanCommands(this));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /*
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
            if (manager.findCachedProfile(ctx.getIssuer().getUniqueId()).clanProfile().isEmpty()) {
                SpigotI18nAPI.get(this).send(ctx.getIssuer().getPlayer(), "cmd.error.base.not.in.clan");
                throw new ConditionFailedException();
            }
        });

        // COMPLETIONS
        CommandCompletions<BukkitCommandCompletionContext> cmpls = commandManager.getCommandCompletions();

        cmpls.registerCompletion("clan", ctx ->
                manager.clans().stream().map(Clan::name).toList());

        // REGISTER
        commandManager.registerCommand(new SpigotLavaClansCommands(this));
         */
    }
}