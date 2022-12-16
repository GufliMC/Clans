package com.guflimc.clans.api.domain;

import com.guflimc.brick.i18n.api.I18nAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;

public class ClanPermission {

    private final static Set<ClanPermission> permissions = new CopyOnWriteArraySet<>();

    //

    public static final ClanPermission CHANGE_CREST = new ClanPermission("CHANGE_CREST", "clans.permission.change.crest");

    public static final ClanPermission INVITE_PLAYERS = new ClanPermission("INVITE_PLAYERS", "clans.permission.invite.player");

    public static final ClanPermission KICK_MEMBERS = new ClanPermission("KICK_MEMBERS", "clans.permission.kick.member");

    //

    private final String key;
    private final Function<Audience, Component> displayProvider;

    public ClanPermission(@NotNull String key, @NotNull Function<Audience, Component> displayProvider) {
        this.key = key;
        this.displayProvider = displayProvider;
        permissions.add(this);
    }

    public ClanPermission(@NotNull String key, @NotNull String i18nKey) {
        this(key, audience -> I18nAPI.get(ClanPermission.class).translate(audience, i18nKey));
    }

    public String key() {
        return key;
    }

    public Component display(@NotNull Audience audience) {
        return displayProvider.apply(audience);
    }

    //

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClanPermission cp && cp.key.equals(key);
    }

    //

    public static @Nullable ClanPermission valueOf(String key) {
        return permissions.stream()
                .filter(cp -> cp.key.equals(key))
                .findFirst().orElse(null);
    }
}
