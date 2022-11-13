package com.guflimc.clans.api.domain;

import com.guflimc.clans.api.cosmetic.CrestConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface Clan {

    UUID id();

    String name();

    String tag();

    Optional<Nexus> nexus();

    int nexusRadius();

    /**
     * Represents a bitwise rgb value
     */
    int color();

    void setColor(int color);

    int level();

    void setLevel(int level);

    int maxMembers();

    void setMaxMembers(int value);

    int memberCount();

    CrestTemplate crestTemplate();

    void setCrestTemplate(CrestTemplate template);

    CrestConfig crestConfig();

    void setCrestConfig(CrestConfig config);

    Instant createdAt();

    //

    default Component displayName() {
        return Component.text(name(), textColor());
    }

    default Component displayTag() {
        return Component.text(tag(), textColor());
    }

    default TextColor textColor() {
        return TextColor.color(color());
    }

    default NamedTextColor namedTextColor() {
        return NamedTextColor.nearestTo(TextColor.color(color()));
    }

}
