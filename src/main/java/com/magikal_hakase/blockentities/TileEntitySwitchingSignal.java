package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class TileEntitySwitchingSignal extends BlockEntityMapper {

    public TileEntitySwitchingSignal(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SWITCHING_SIGNAL, pos, state);
    }
}