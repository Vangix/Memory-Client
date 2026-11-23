package com.vangix.tools.module.impl;

import com.vangix.tools.module.*;
import com.vangix.tools.setting.*;

public class TracersModule extends Module {
    public final BoolSetting blocks = add(new BoolSetting("Blocks", true));
    public final BoolSetting players = add(new BoolSetting("Players", true));
    public final BoolSetting mobs = add(new BoolSetting("Mobs", false));
    public final NumberSetting range = add(new NumberSetting("Range", 128, 16, 256, 16));
    public final NumberSetting maxLines = add(new NumberSetting("Max Lines", 50, 5, 200, 5));

    public TracersModule() { super("TRACERS", Category.RENDER); }
}
