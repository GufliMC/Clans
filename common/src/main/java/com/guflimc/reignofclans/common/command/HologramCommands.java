package com.guflimc.reignofclans.common.command;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Greedy;
import com.guflimc.reignofclans.api.ClanManager;
import com.guflimc.reignofclans.api.domain.Clan;
import com.guflimc.brick.i18n.api.I18nAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Objects;

@CommandContainer
public class HologramCommands {

    private final ClanManager<?> manager;

    public HologramCommands(ClanManager<?> manager) {
        this.manager = manager;
    }

//    @Suggestions("index")
//    public List<String> indexSuggestion(CommandContext<Audience> sender, CommandContext<Audience> ctx) {
//        Hologram holo = ctx.getOrDefault("hologram", null);
//        if ( holo == null ) return null;
//        return IntStream.range(0, holo.lines().size()).boxed().map(Object::toString).toList();
//    }

    @CommandMethod("bh reload")
    public void reload(Audience sender) {
        manager.reload();
        I18nAPI.get(this).send(sender, "cmd.reload");
    }

    @CommandMethod("bh list")
    public void list(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.list",
                manager.holograms().stream().map(Clan::name)
                        .filter(Objects::nonNull).toList()
        );
    }

    @CommandMethod("bh delete <hologram>")
    public void delete(Audience sender, @Argument(value = "hologram") Clan hologram) {
        manager.remove(hologram);
        I18nAPI.get(this).send(sender, "cmd.delete", hologram.name());
    }

    @CommandMethod("bh addline <hologram> <line>")
    public void addline(Audience sender,
                        @Argument(value = "hologram") Clan hologram,
                        @Argument(value = "line") @Greedy String line) {
        Component text = MiniMessage.miniMessage().deserialize(line);
        hologram.addLine(text);
        manager.merge(hologram);

        I18nAPI.get(this).send(sender, "cmd.addline", text, hologram.name());
    }

    @CommandMethod("bh setline <hologram> <index> <line>")
    public void setline(Audience sender,
                        @Argument(value = "hologram") Clan hologram,
                        @Argument(value = "index"/*, suggestions = "index"*/) int index,
                        @Argument(value = "line") @Greedy String line) {
        if ( index  < 0 || index >= hologram.lines().size() ) {
            I18nAPI.get(this).send(sender, "cmd.error.args.index", index);
            return;
        }

        Component text = MiniMessage.miniMessage().deserialize(line);
        hologram.setLine(index, text);
        manager.merge(hologram);

        I18nAPI.get(this).send(sender, "cmd.addline", text, hologram.name());
    }

    @CommandMethod("bh removeline <hologram> <index>")
    public void removeline(Audience sender,
                           @Argument(value = "hologram") Clan hologram,
                           @Argument(value = "index"/*, suggestions = "index"*/) int index) {
        if ( index  < 0 || index >= hologram.lines().size() ) {
            I18nAPI.get(this).send(sender, "cmd.error.args.index", index);
            return;
        }

        hologram.removeLine(index);
        manager.merge(hologram);

        I18nAPI.get(this).send(sender, "cmd.removeline", index, hologram.name());
    }
}
