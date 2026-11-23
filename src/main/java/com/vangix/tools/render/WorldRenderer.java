package com.vangix.tools.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vangix.tools.VangixTools;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class WorldRenderer {
    private WorldRenderer() {}

    public static void render(PoseStack ps, MultiBufferSource buffers, Vec3 cam, Vec3 lookDir) {
        var mm = VangixTools.modules;
        if (!mm.baseFinder.isEnabled()) return;

        var vc = buffers.getBuffer(RenderType.lines());

        if (mm.esp.isEnabled()) {
            List<BlockPos> hits = mm.baseFinder.flaggedHits(mm.esp.range.get(), mm.esp.maxBoxes.intValue());
            for (BlockPos p : hits) {
                Vec3 mn = new Vec3(p.getX() - cam.x, p.getY() - cam.y, p.getZ() - cam.z);
                RenderUtil.box(ps, vc, mn, mn.add(1, 1, 1), 0xFF3A86FF);
            }
        }

        if (mm.tracers.isEnabled()) {
            Vec3 start = lookDir.scale(0.5);
            List<BlockPos> hits = mm.baseFinder.flaggedHits(mm.tracers.range.get(), mm.tracers.maxLines.intValue());
            for (BlockPos p : hits) {
                Vec3 end = new Vec3(p.getX() + .5 - cam.x, p.getY() + .5 - cam.y, p.getZ() + .5 - cam.z);
                RenderUtil.line(ps, vc, start, end, 0xFF7FE8FF);
            }
        }
    }
}
