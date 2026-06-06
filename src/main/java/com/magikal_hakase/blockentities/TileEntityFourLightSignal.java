package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper; // ★ここを変更
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

// extends を BlockEntityMapper に変更
public class TileEntityFourLightSignal extends BlockEntityMapper {

    public TileEntityFourLightSignal(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FOUR_LIGHT_SIGNAL, pos, state);
    }
}