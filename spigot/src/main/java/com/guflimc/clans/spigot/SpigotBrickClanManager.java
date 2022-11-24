package com.guflimc.clans.spigot;

import com.guflimc.clans.api.crest.CrestType;
import com.guflimc.clans.common.AbstractClanManager;
import com.guflimc.clans.common.ClansDatabaseContext;
import com.guflimc.clans.spigot.api.SpigotClanManager;

public class SpigotBrickClanManager extends AbstractClanManager implements SpigotClanManager {

    public SpigotBrickClanManager(ClansDatabaseContext databaseContext) {
        super(databaseContext);

        setupCrests();
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

    ///////////////////////////////////////////////////////////////////////////

    private void setupCrests() {
        // free

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

        // restricted

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

}
