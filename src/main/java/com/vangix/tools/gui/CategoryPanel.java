package com.vangix.tools.gui;

import com.vangix.tools.VangixTools;
import com.vangix.tools.module.*;
import com.vangix.tools.setting.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.*;

public class CategoryPanel {
    private static final int ROW_H = 18, HEADER_H = 24, PAD = 8;
    private static final int BG = 0xD0121419, BORDER = 0xFFE8F4FF;
    private static final int ACCENT = 0xFF3A86FF, OFF = 0xFF3A3D45, TRACK = 0xFF2A2D35;
    private static final int TITLE = 0xFF7FE8FF, TEXT = 0xFFFFFFFF, SUB = 0xFFB0B8C4;

    private final Category category;
    private final int x, y, w;
    private final Set<Mod> expanded = new HashSet<>();
    private final Map<Mod, Float> anim = new HashMap<>();
    private NumberSetting dragging;

    public CategoryPanel(Category c, int x, int y, int w) {
        this.category = c; this.x = x; this.y = y; this.w = w;
    }

    private int heightOf(List<Mod> mods) {
        int h = HEADER_H;
        for (Mod m : mods) {
            h += ROW_H;
            if (expanded.contains(m)) h += m.settings().size() * ROW_H;
        }
        return h + 4;
    }

    public void render(GuiGraphicsExtractor g, Font font) {
        List<Mod> mods = VangixTools.modules.in(category);
        GuiUtil.card(g, x, y, x + w, y + heightOf(mods), 3, BG, BORDER);
        GuiUtil.centered(g, font, category.label, x + w / 2, y + 8, TITLE);

        int cy = y + HEADER_H;
        for (Mod m : mods) {
            GuiUtil.text(g, font, m.name, x + PAD, cy + 5, TEXT);

            float a = anim.getOrDefault(m, m.isEnabled() ? 1f : 0f);
            a += ((m.isEnabled() ? 1f : 0f) - a) * 0.25f;
            anim.put(m, a);

            int px = x + w - 28, py = cy + 4;
            GuiUtil.roundedFill(g, px, py, px + 20, py + 10, 3, m.isEnabled() ? ACCENT : OFF);
            int kx = px + 1 + Math.round(a * 10);
            GuiUtil.roundedFill(g, kx, py + 1, kx + 8, py + 9, 3, 0xFFFFFFFF);
            cy += ROW_H;

            if (expanded.contains(m)) {
                for (Setting<?> s : m.settings()) {
                    renderSetting(g, font, s, cy);
                    cy += ROW_H;
                }
            }
        }
    }

    private void renderSetting(GuiGraphicsExtractor g, Font font, Setting<?> s, int cy) {
        int left = x + PAD + 6;
        if (s instanceof BoolSetting b) {
            GuiUtil.text(g, font, s.name, left, cy + 5, SUB);
            int bx = x + w - 20;
            GuiUtil.fill(g, bx, cy + 4, bx + 10, cy + 14, OFF);
            if (b.get()) GuiUtil.fill(g, bx + 2, cy + 6, bx + 8, cy + 12, ACCENT);
        } else if (s instanceof NumberSetting n) {
            GuiUtil.text(g, font, s.name, left, cy + 2, SUB);
            String v = String.valueOf(n.intValue());
            GuiUtil.text(g, font, v, x + w - PAD - font.width(v), cy + 2, TEXT);
            int tx1 = left, tx2 = x + w - PAD;
            GuiUtil.fill(g, tx1, cy + 13, tx2, cy + 15, TRACK);
            double frac = (n.get() - n.min) / (n.max - n.min);
            int fx = tx1 + (int) ((tx2 - tx1) * frac);
            GuiUtil.fill(g, tx1, cy + 13, fx, cy + 15, ACCENT);
            GuiUtil.fill(g, fx - 1, cy + 11, fx + 2, cy + 17, TEXT);
        }
    }

    public boolean click(double mx, double my, int button) {
        int cy = y + HEADER_H;
        for (Mod m : VangixTools.modules.in(category)) {
            if (inRow(mx, my, cy)) {
                if (button == 0) m.toggle();
                else if (button == 1 && !expanded.remove(m)) expanded.add(m);
                return true;
            }
            cy += ROW_H;
            if (expanded.contains(m)) {
                for (Setting<?> s : m.settings()) {
                    if (inRow(mx, my, cy)) {
                        if (s instanceof BoolSetting b && button == 0) b.toggle();
                        else if (s instanceof NumberSetting n && button == 0) {
                            dragging = n;
                            drag(mx);
                        }
                        return true;
                    }
                    cy += ROW_H;
                }
            }
        }
        return false;
    }

    private boolean inRow(double mx, double my, int cy) {
        return mx >= x && mx <= x + w && my >= cy && my < cy + ROW_H;
    }

    public void drag(double mx) {
        if (dragging == null) return;
        int left = x + PAD + 6, right = x + w - PAD;
        double frac = Math.max(0, Math.min(1, (mx - left) / (right - left)));
        dragging.set(dragging.min + frac * (dragging.max - dragging.min));
    }

    public void release() { dragging = null; }
}
