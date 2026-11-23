package com.vangix.tools.gui;

import com.vangix.tools.module.Category;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private final List<CategoryPanel> panels = new ArrayList<>();

    public ClickGuiScreen() {
        super(Component.literal("ClickGUI"));
        int x = 20;
        for (Category c : Category.values()) {
            panels.add(new CategoryPanel(c, x, 20, 140));
            x += 150;
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float delta) {
        g.fill(0, 0, width, height, 0x88050810);
        for (CategoryPanel p : panels) p.render(g, font);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (CategoryPanel p : panels) if (p.click(mx, my, button)) return true;
        return super.mouseClicked(mx, my, button);
    }

    @Override public boolean isPauseScreen() { return false; }
}
