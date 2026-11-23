package com.vangix.tools.gui;

import com.vangix.tools.module.Category;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
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
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        GuiUtil.fill(g, 0, 0, width, height, 0x88050810);
        for (CategoryPanel p : panels) p.render(g, font);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent e, boolean doubleClick) {
        for (CategoryPanel p : panels) if (p.click(e.x(), e.y(), e.button())) return true;
        return super.mouseClicked(e, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        for (CategoryPanel p : panels) p.drag(e.x());
        return super.mouseDragged(e, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent e) {
        for (CategoryPanel p : panels) p.release();
        return super.mouseReleased(e);
    }

    @Override public boolean isPauseScreen() { return false; }
}
