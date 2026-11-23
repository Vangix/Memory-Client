package com.vangix.tools.module.impl;

import com.vangix.tools.module.*;
import com.vangix.tools.setting.*;

public class EspModule extends Module {
    public final BoolSetting blocks = add(new BoolSetting("Blocks", true));
    public final BoolSetting players = add(new BoolSetting("Players", true));
    public final BoolSetting mobs = add(new BoolSetting("Mobs", false));
    public final NumberSetting range = add(new NumberSetting("Range", 128, 16, 256, 16));
    public final NumberSetting maxBoxes = add(new NumberSetting("Max Boxes", 200, 10, 1000, 10));

    public EspModule() { super("ESP", Category.RENDER); }
}
