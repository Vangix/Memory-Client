package com.vangix.tools.gui;

import com.vangix.tools.VangixTools;
import com.vangix.tools.module.*;
import com.vangix.tools.setting.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.*;

public class CategoryPanel {
    private static final int ROW_H = 18, HEADER_H = 22;
    private static final int BG = 0xCC14161C, BORDER = 0xFFE8F4FF;
    private static final int ACCENT = 0xFF3A86FF, OFF = 0xFF3A3D45;

    private final Category category;
    private final int x, y, w;
    private final Set<Module> expanded = new HashSet<>();

    public CategoryPanel(Category c, int x, int y, int w) {
        this.category = c; this.x = x; this.y = y; this.w = w;
    }

    private int heightOf(List<Module> mods) {
        int h = HEADER_H;
        for (Module m : mods) {
            h += ROW_H;
            if (expanded.contains(m)) h += m.settings().size() * ROW_H;
        }
        return h + 4;
    }

    public void render(GuiGraphics g, Font font) {
        List<Module> mods = VangixTools.modules.in(category);
        int h = heightOf(mods);
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, BORDER);
        g.fill(x, y, x + w, y + h, BG);
        g.drawCenteredString(font, category.label, x + w / 2, y + 7, 0xFF7FE8FF);

        int cy = y + HEADER_H;
        for (Module m : mods) {
            g.drawString(font, m.name, x + 8, cy + 5, 0xFFFFFFFF, false);
            int px = x + w - 26, py = cy + 4;
            g.fill(px, py, px + 20, py + 10, m.isEnabled() ? ACCENT : OFF);
            int knob = m.isEnabled() ? px + 11 : px + 1;
            g.fill(knob, py + 1, knob + 8, py + 9, 0xFFFFFFFF);
            cy += ROW_H;

            if (expanded.contains(m)) {
                for (Setting<?> s : m.settings()) {
                    g.drawString(font, s.name + ": " + s.get(), x + 14, cy + 5, 0xFFAAAAAA, false);
                    cy += ROW_H;
                }
            }
        }
    }

    public boolean click(double mx, double my, int button) {
        int cy = y + HEADER_H;
        for (Module m : VangixTools.modules.in(category)) {
            if (mx >= x && mx <= x + w && my >= cy && my < cy + ROW_H) {
                if (button == 0) m.toggle();
                else if (button == 1 && !expanded.remove(m)) expanded.add(m);
                return true;
            }
            cy += ROW_H;
            if (expanded.contains(m)) {
                for (Setting<?> s : m.settings()) {
                    if (mx >= x && mx <= x + w && my >= cy && my < cy + ROW_H) {
                        if (s instanceof BoolSetting b && button == 0) b.toggle();
                        else if (s instanceof NumberSetting n) {
                            double d = button == 0 ? n.step : -n.step;
                            n.set(n.get() + d);
                        }
                        return true;
                    }
                    cy += ROW_H;
                }
            }
        }
        return false;
    }
}
