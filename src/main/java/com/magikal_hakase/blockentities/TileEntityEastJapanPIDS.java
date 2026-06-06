package com.magikal_hakase.blockentities;

import mtr.block.BlockPIDSBaseHorizontal;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class TileEntityEastJapanPIDS extends BlockPIDSBaseHorizontal.TileEntityBlockPIDSBaseHorizontal {

    public TileEntityEastJapanPIDS(BlockPos pos, BlockState state) {
        // BlockEntityMapper 経由で登録されたタイプを渡します
        super(ModBlockEntities.EAST_JAPAN_PIDS, pos, state);
    }

    @Override
    public int getMaxArrivals() {
        // JR東日本の発車標は2段または3段が一般的です
        return 2;
    }
}