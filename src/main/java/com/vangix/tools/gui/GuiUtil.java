package com.vangix.tools.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class GuiUtil {
    private GuiUtil() {}

    // VERIFY: method names on GuiGraphicsExtractor (fill / text) are assumed.
    public static void fill(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int color) {
        g.fill(x1, y1, x2, y2, color);
    }

    public static void text(GuiGraphicsExtractor g, Font font, String s, int x, int y, int color) {
        g.text(font, s, x, y, color, false);
    }

    public static void centered(GuiGraphicsExtractor g, Font font, String s, int cx, int y, int color) {
        text(g, font, s, cx - font.width(s) / 2, y, color);
    }

    /** Rounded rectangle approximated with stepped corners. */
    public static void roundedFill(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int r, int color) {
        fill(g, x1, y1 + r, x2, y2 - r, color);
        for (int i = 0; i < r; i++) {
            int inset = r - i;
            fill(g, x1 + inset, y1 + i, x2 - inset, y1 + i + 1, color);
            fill(g, x1 + inset, y2 - i - 1, x2 - inset, y2 - i, color);
        }
    }

    public static void card(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int r, int bg, int border) {
        roundedFill(g, x1 - 1, y1 - 1, x2 + 1, y2 + 1, r + 1, border);
        roundedFill(g, x1, y1, x2, y2, r, bg);
    }
}
