package com.vangix.tools.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vangix.tools.VangixTools;
import com.vangix.tools.module.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class WorldRenderer {
    private static final int BLOCK_COLOR = 0xFF3A86FF;
    private static final int PLAYER_COLOR = 0xFFFF5555;
    private static final int MOB_COLOR = 0xFF55FF55;
    private static final int TRACER_COLOR = 0xFF7FE8FF;

    private WorldRenderer() {}

    public static void render(PoseStack ps, MultiBufferSource buffers, Vec3 cam, Vec3 lookDir) {
        var mm = VangixTools.modules;
        EspModule esp = mm.esp;
        TracersModule tr = mm.tracers;
        if (!esp.isEnabled() && !tr.isEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // VERIFY: partial tick accessor name in 26.3 (getDeltaTracker().getGameTimeDeltaPartialTick(true)).
        float pt = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        var vc = buffers.getBuffer(RenderType.lines());
        Vec3 start = lookDir.scale(0.5); // slightly in front of the camera

        // ---- Blocks (static positions, no interpolation needed) ----
        boolean finder = mm.baseFinder.isEnabled();
        if (finder && esp.isEnabled() && esp.blocks.get()) {
            for (BlockPos p : mm.baseFinder.flaggedHits(esp.range.get(), esp.maxBoxes.intValue())) {
                Vec3 mn = new Vec3(p.getX() - cam.x, p.getY() - cam.y, p.getZ() - cam.z);
                RenderUtil.box(ps, vc, mn, mn.add(1, 1, 1), BLOCK_COLOR);
            }
        }
        if (finder && tr.isEnabled() && tr.blocks.get()) {
            for (BlockPos p : mm.baseFinder.flaggedHits(tr.range.get(), tr.maxLines.intValue())) {
                Vec3 end = new Vec3(p.getX() + .5 - cam.x, p.getY() + .5 - cam.y, p.getZ() + .5 - cam.z);
                RenderUtil.line(ps, vc, start, end, TRACER_COLOR);
            }
        }

        // ---- Entities (interpolated by partial tick) ----
        boolean espEnt = esp.isEnabled() && (esp.players.get() || esp.mobs.get());
        boolean trEnt = tr.isEnabled() && (tr.players.get() || tr.mobs.get());
        if (!espEnt && !trEnt) return;

        double maxRange = Math.max(esp.range.get(), tr.range.get());
        double maxSq = maxRange * maxRange;
        List<Entity> targets = new ArrayList<>();
        for (Entity e : mc.level.entitiesForRendering()) {
            if (e == mc.player || !(e instanceof LivingEntity) || !e.isAlive()) continue;
            if (mc.player.distanceToSqr(e) > maxSq) continue;
            targets.add(e);
        }

        int lines = 0;
        for (Entity e : targets) {
            boolean isPlayer = e instanceof Player;

            // Interpolated position: lerp(previous tick, current tick, partialTick)
            double ix = Mth.lerp(pt, e.xo, e.getX());
            double iy = Mth.lerp(pt, e.yo, e.getY());
            double iz = Mth.lerp(pt, e.zo, e.getZ());
            AABB bb = e.getBoundingBox().move(ix - e.getX(), iy - e.getY(), iz - e.getZ());
            int color = isPlayer ? PLAYER_COLOR : MOB_COLOR;
            double dist = mc.player.distanceTo(e);

            if (esp.isEnabled() && (isPlayer ? esp.players.get() : esp.mobs.get()) && dist <= esp.range.get()) {
                RenderUtil.box(ps, vc, bb, cam, color);
            }
            if (tr.isEnabled() && (isPlayer ? tr.players.get() : tr.mobs.get())
                    && dist <= tr.range.get() && lines < tr.maxLines.intValue()) {
                Vec3 c = bb.getCenter();
                RenderUtil.line(ps, vc, start, new Vec3(c.x - cam.x, c.y - cam.y, c.z - cam.z), color);
                lines++;
            }
        }
    }
}
