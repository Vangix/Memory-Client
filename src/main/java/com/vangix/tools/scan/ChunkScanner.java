package com.vangix.tools.scan;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/** Time-budgeted scanner: client thread, small slices, cached per chunk. */
public final class ChunkScanner {
    private static final Predicate<BlockState> INTERESTING = s -> BlockWeights.score(s) > 0;
    private static final long BUDGET_NANOS = 1_000_000L; // ~1 ms per tick

    private final Map<ChunkPos, ChunkScore> cache = new ConcurrentHashMap<>();
    private final ArrayDeque<ChunkPos> queue = new ArrayDeque<>();
    private final Set<ChunkPos> queued = new HashSet<>();

    public void enqueue(ChunkPos pos) {
        if (queued.add(pos)) queue.add(pos);
    }

    public void remove(ChunkPos pos) { cache.remove(pos); }

    public void clear() { cache.clear(); queue.clear(); queued.clear(); }

    public Map<ChunkPos, ChunkScore> results() { return cache; }

    public void requeueAll() {
        for (ChunkPos key : new ArrayList<>(cache.keySet())) enqueue(key);
    }

    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        long deadline = System.nanoTime() + BUDGET_NANOS;
        while (System.nanoTime() < deadline && !queue.isEmpty()) {
            ChunkPos pos = queue.poll();
            queued.remove(pos);
            LevelChunk chunk = mc.level.getChunkSource().getChunkNow(pos.x(), pos.z());
            if (chunk == null) continue;
            cache.put(pos, scan(chunk));
        }
    }

    private ChunkScore scan(LevelChunk chunk) {
        int total = 0, storage = 0;
        List<BlockPos> hits = new ArrayList<>();
        LevelChunkSection[] sections = chunk.getSections();
        int minY = chunk.getMinY();
        ChunkPos cp = chunk.getPos();

        for (int i = 0; i < sections.length; i++) {
            LevelChunkSection sec = sections[i];
            if (sec.hasOnlyAir() || !sec.maybeHas(INTERESTING)) continue;

            int baseY = minY + (i << 4);
            for (int y = 0; y < 16; y++)
                for (int z = 0; z < 16; z++)
                    for (int x = 0; x < 16; x++) {
                        BlockState st = sec.getBlockState(x, y, z);
                        int w = BlockWeights.score(st);
                        if (w == 0) continue;
                        total += w;
                        if (BlockWeights.isStorage(st)) storage++;
                        hits.add(new BlockPos(cp.getMinBlockX() + x, baseY + y, cp.getMinBlockZ() + z));
                    }
        }
        return new ChunkScore(total, storage, hits);
    }
}
