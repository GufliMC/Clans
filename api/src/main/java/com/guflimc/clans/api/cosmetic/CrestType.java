package com.guflimc.clans.api.cosmetic;

import java.util.ArrayList;
import java.util.List;

public record CrestType(Color background, List<CrestLayer> layers) {

    public static CrestType of(CrestLayer... layers) {
        return new CrestType(Color.BACKGROUND, layers);
    }

    public static CrestType of(Color background, CrestLayer... layers) {
        return new CrestType(background, layers);
    }

    public CrestType(Color background, List<CrestLayer> layers) {
        this.background = background;
        this.layers = List.copyOf(layers);
    }

    public CrestType(Color background, CrestLayer... layers) {
        this(background, List.of(layers));
    }

    public CrestType withLayer(CrestLayer layer) {
        List<CrestLayer> newLayers = new ArrayList<>(layers);
        newLayers.add(layer);
        return new CrestType(background, newLayers);
    }

    public record CrestLayer(Color color, List<Pattern> patterns) {

        public static CrestLayer of(Color color, Pattern... patterns) {
            return new CrestLayer(color, patterns);
        }

        public CrestLayer(Color color, List<Pattern> patterns) {
            this.patterns = List.copyOf(patterns);
            this.color = color;
        }

        public CrestLayer(Color color, Pattern... patterns) {
            this(color, List.of(patterns));
        }

        public CrestLayer withPattern(Pattern pattern) {
            List<Pattern> newPatterns = new ArrayList<>(patterns);
            newPatterns.add(pattern);
            return new CrestLayer(color, newPatterns);
        }

        public CrestLayer withColor(Color color) {
            return new CrestLayer(color, patterns);
        }

    }

    public enum Color {
        FOREGROUND,
        BACKGROUND,

        ACCENT,

        WHITE,
        ORANGE,
        MAGENTA,
        LIGHT_BLUE,
        YELLOW,
        LIME,
        PINK,
        GRAY,
        LIGHT_GRAY,
        CYAN,
        PURPLE,
        BLUE,
        BROWN,
        GREEN,
        RED,
        BLACK;
    }

    public enum Pattern {
        BASE("b"),
        SQUARE_BOTTOM_LEFT("bl"),
        SQUARE_BOTTOM_RIGHT("br"),
        SQUARE_TOP_LEFT("tl"),
        SQUARE_TOP_RIGHT("tr"),
        STRIPE_BOTTOM("bs"),
        STRIPE_TOP("ts"),
        STRIPE_LEFT("ls"),
        STRIPE_RIGHT("rs"),
        STRIPE_CENTER("cs"),
        STRIPE_MIDDLE("ms"),
        STRIPE_DOWNRIGHT("drs"),
        STRIPE_DOWNLEFT("dls"),
        STRIPE_SMALL("ss"),
        CROSS("cr"),
        STRAIGHT_CROSS("sc"),
        TRIANGLE_BOTTOM("bt"),
        TRIANGLE_TOP("tt"),
        TRIANGLES_BOTTOM("bts"),
        TRIANGLES_TOP("tts"),
        DIAGONAL_LEFT("ld"),
        DIAGONAL_RIGHT("rd"),
        DIAGONAL_LEFT_MIRROR("lud"),
        DIAGONAL_RIGHT_MIRROR("rud"),
        CIRCLE_MIDDLE("mc"),
        RHOMBUS_MIDDLE("mr"),
        HALF_VERTICAL("vh"),
        HALF_HORIZONTAL("hh"),
        HALF_VERTICAL_MIRROR("vhr"),
        HALF_HORIZONTAL_MIRROR("hhb"),
        BORDER("bo"),
        CURLY_BORDER("cbo"),
        CREEPER("cre"),
        GRADIENT("gra"),
        GRADIENT_UP("gru"),
        BRICKS("bri"),
        SKULL("sku"),
        FLOWER("flo"),
        MOJANG("moj"),
        GLOBE("glb"),
        PIGLIN("pig");

        private final String id;

        Pattern(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

}
