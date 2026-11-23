package com.vangix.tools.scan;

import net.minecraft.core.BlockPos;
import java.util.List;

public record ChunkScore(int total, int storageCount, List<BlockPos> hits) {}
