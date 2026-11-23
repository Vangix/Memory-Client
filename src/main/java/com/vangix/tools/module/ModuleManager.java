package com.vangix.tools.module;

import com.vangix.tools.module.impl.*;
import java.util.*;

public final class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    public final BaseFinderModule baseFinder = new BaseFinderModule();
    public final EspModule esp = new EspModule();
    public final TracersModule tracers = new TracersModule();

    public ModuleManager() {
        modules.add(baseFinder);
        modules.add(esp);
        modules.add(tracers);
    }

    public List<Module> all() { return modules; }

    public List<Module> in(Category c) {
        return modules.stream().filter(m -> m.category == c).toList();
    }

    public void tick() {
        for (Module m : modules) if (m.isEnabled()) m.onTick();
    }
}
