package com.vangix.tools.module.impl;

import com.vangix.tools.module.*;
import com.vangix.tools.scan.*;
import com.vangix.tools.setting.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BaseFinderModule extends Mod {
    public final NumberSetting threshold = add(new NumberSetting("Threshold", 8, 1, 50, 1));
    public final BoolSetting requireStorage = add(new BoolSetting("Req Storage", true));
    public final NumberSetting rescanSeconds = add(new NumberSetting("Rescan Sec", 10, 2, 60, 1));
    public final ChunkScanner scanner = new ChunkScanner();
    private int ticks;

    public BaseFinderModule() { super("BASE FINDER", Category.BASEFINDER); }

    @Override protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        int r = mc.options.getEffectiveRenderDistance();
        ChunkPos c = mc.player.chunkPosition();
        for (int dx = -r; dx <= r; dx++)
            for (int dz = -r; dz <= r; dz++)
                scanner.enqueue(new ChunkPos(c.x() + dx, c.z() + dz));
    }

    @Override protected void onDisable() { scanner.clear(); }

    @Override public void onTick() {
        scanner.tick();
        if (++ticks >= rescanSeconds.intValue() * 20) { ticks = 0; scanner.requeueAll(); }
    }

    /** Hit positions from chunks that pass the threshold, nearest-first, capped. */
    public List<BlockPos> flaggedHits(double maxDist, int maxCount) {
        Minecraft mc = Minecraft.getInstance();
        List<BlockPos> out = new ArrayList<>();
        if (mc.player == null) return out;
        double maxSq = maxDist * maxDist;
        for (ChunkScore s : scanner.results().values()) {
            if (s.total() < threshold.intValue()) continue;
            if (requireStorage.get() && s.storageCount() == 0) continue;
            for (BlockPos p : s.hits())
                if (mc.player.distanceToSqr(Vec3.atCenterOf(p)) <= maxSq) out.add(p);
        }
        out.sort((a, b) -> Double.compare(
            mc.player.distanceToSqr(Vec3.atCenterOf(a)),
            mc.player.distanceToSqr(Vec3.atCenterOf(b))));
        return out.size() > maxCount ? out.subList(0, maxCount) : out;
    }
}
