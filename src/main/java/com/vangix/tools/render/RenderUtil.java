package com.vangix.tools.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

public final class RenderUtil {
    private RenderUtil() {}

    public static void line(PoseStack ps, VertexConsumer vc, Vec3 a, Vec3 b, int argb) {
        PoseStack.Pose pose = ps.last();
        float dx = (float) (b.x - a.x), dy = (float) (b.y - a.y), dz = (float) (b.z - a.z);
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0) return;
        dx /= len; dy /= len; dz /= len;
        vc.addVertex(pose, (float) a.x, (float) a.y, (float) a.z).setColor(argb).setNormal(pose, dx, dy, dz);
        vc.addVertex(pose, (float) b.x, (float) b.y, (float) b.z).setColor(argb).setNormal(pose, dx, dy, dz);
    }

    /** Wireframe box from min to max (already camera-relative). */
    public static void box(PoseStack ps, VertexConsumer vc, Vec3 mn, Vec3 mx, int argb) {
        Vec3[] c = {
            new Vec3(mn.x, mn.y, mn.z), new Vec3(mx.x, mn.y, mn.z),
            new Vec3(mx.x, mn.y, mx.z), new Vec3(mn.x, mn.y, mx.z),
            new Vec3(mn.x, mx.y, mn.z), new Vec3(mx.x, mx.y, mn.z),
            new Vec3(mx.x, mx.y, mx.z), new Vec3(mn.x, mx.y, mx.z)
        };
        int[][] e = {{0,1},{1,2},{2,3},{3,0},{4,5},{5,6},{6,7},{7,4},{0,4},{1,5},{2,6},{3,7}};
        for (int[] ed : e) line(ps, vc, c[ed[0]], c[ed[1]], argb);
    }
}
