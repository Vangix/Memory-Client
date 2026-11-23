package com.vangix.tools;

import com.vangix.tools.gui.ClickGuiScreen;
import com.vangix.tools.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class VangixTools implements ClientModInitializer {
    public static ModuleManager modules;
    private static KeyMapping openGui;

    @Override
    public void onInitializeClient() {
        modules = new ModuleManager();

        ClientChunkEvents.CHUNK_LOAD.register((level, chunk) -> {
            if (modules.baseFinder.isEnabled()) modules.baseFinder.scanner.enqueue(chunk.getPos());
        });
        ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) ->
            modules.baseFinder.scanner.remove(chunk.getPos()));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            modules.tick();
            while (openGui.consumeClick()) mc.setScreenAndShow(new ClickGuiScreen());
        });

        // 344 = Right Shift. VERIFY: KeyMappingHelper package/name (fabric-key-mapping-api-v1).
        openGui = KeyMappingHelper.registerKeyMapping(
            new KeyMapping("key.vangix.gui", 344, KeyMapping.Category.MISC));
    }
}
