package com.vangix.tools;

import com.mojang.blaze3d.platform.InputConstants;
import com.vangix.tools.gui.ClickGuiScreen;
import com.vangix.tools.module.ModuleManager;
import com.vangix.tools.render.WorldRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

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
            while (openGui.consumeClick()) mc.setScreen(new ClickGuiScreen());
        });

        // VERIFY: KeyMapping constructor/category signature may differ in 26.3.
        openGui = KeyBindingHelper.registerKeyBinding(
            new KeyMapping("key.vangix.gui", InputConstants.Type.KEYSYM,
                           GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));

        // VERIFY: event name and context accessors vary by Fabric API version.
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx -> {
            var cam = ctx.camera();
            WorldRenderer.render(ctx.matrixStack(), ctx.consumers(), cam.getPosition(),
                    new net.minecraft.world.phys.Vec3(cam.getLookVector()));
        });
    }
}
