package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class TileEntityTwoLightSignal extends BlockEntityMapper {

    public TileEntityTwoLightSignal(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TWO_LIGHT_SIGNAL, pos, state);
    }
}