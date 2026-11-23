package com.vangix.tools.gui;

import net.minecraft.client.gui.GuiGraphics;

public final class GuiUtil {
    private GuiUtil() {}

    /** Rounded rectangle approximated with stepped corners (cheap, no shaders). */
    public static void roundedFill(GuiGraphics g, int x1, int y1, int x2, int y2, int r, int color) {
        g.fill(x1, y1 + r, x2, y2 - r, color);
        for (int i = 0; i < r; i++) {
            int inset = r - i;
            g.fill(x1 + inset, y1 + i, x2 - inset, y1 + i + 1, color);
            g.fill(x1 + inset, y2 - i - 1, x2 - inset, y2 - i, color);
        }
    }

    /** Rounded card with a 1px border. */
    public static void card(GuiGraphics g, int x1, int y1, int x2, int y2, int r, int bg, int border) {
        roundedFill(g, x1 - 1, y1 - 1, x2 + 1, y2 + 1, r + 1, border);
        roundedFill(g, x1, y1, x2, y2, r, bg);
    }
}
