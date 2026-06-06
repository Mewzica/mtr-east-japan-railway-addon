package com.magikal_hakase.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

// 架線碍子の機能をそのまま継承
public class TileEntityMovableBracket extends TileEntityCatenaryInsulator {

    public TileEntityMovableBracket(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOVABLE_BRACKET, pos, state);
    }
}