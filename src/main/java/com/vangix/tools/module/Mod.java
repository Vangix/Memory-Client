package com.vangix.tools.module;

import com.vangix.tools.setting.Setting;
import java.util.ArrayList;
import java.util.List;

public abstract class Mod {
    public final String name;
    public final Category category;
    private boolean enabled;
    private final List<Setting<?>> settings = new ArrayList<>();

    protected Mod(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    protected <S extends Setting<?>> S add(S setting) { settings.add(setting); return setting; }
    public List<Setting<?>> settings() { return settings; }

    public boolean isEnabled() { return enabled; }
    public void toggle() { setEnabled(!enabled); }
    public void setEnabled(boolean on) {
        if (on == enabled) return;
        enabled = on;
        if (on) onEnable(); else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
}
