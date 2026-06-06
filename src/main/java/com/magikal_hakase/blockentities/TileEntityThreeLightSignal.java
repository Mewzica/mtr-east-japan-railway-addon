package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class TileEntityThreeLightSignal extends BlockEntityMapper {

    public TileEntityThreeLightSignal(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THREE_LIGHT_SIGNAL, pos, state);
    }
}