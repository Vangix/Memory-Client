package com.vangix.tools.scan;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockWeights {
    private BlockWeights() {}

    public static boolean isStorage(BlockState s) {
        Block b = s.getBlock();
        return b instanceof ChestBlock || b instanceof BarrelBlock || b instanceof ShulkerBoxBlock;
    }

    /** Score contribution for this state; 0 means "not interesting". */
    public static int score(BlockState s) {
        Block b = s.getBlock();
        if (isStorage(s)) return 3;
        if (b instanceof AbstractFurnaceBlock || b instanceof EnchantingTableBlock
                || b instanceof BrewingStandBlock || b instanceof AnvilBlock
                || b instanceof BeaconBlock || b == Blocks.CRAFTING_TABLE) return 2;
        if (b == Blocks.AMETHYST_CLUSTER) return 2;
        if (b == Blocks.LARGE_AMETHYST_BUD || b == Blocks.MEDIUM_AMETHYST_BUD
                || b == Blocks.SMALL_AMETHYST_BUD) return 1;
        if (b instanceof VineBlock) return 1;
        return 0;
    }
}
