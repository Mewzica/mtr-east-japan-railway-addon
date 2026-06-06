package com.magikal_hakase.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import mtr.mappings.BlockEntityMapper;

public class TileEntityCrossingSignmark extends BlockEntityMapper {
    public TileEntityCrossingSignmark(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CROSSING_SIGNMARK, pos, state);
    }
}