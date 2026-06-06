package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityEastJapanPIDS;
import mtr.block.BlockPIDSBaseHorizontal;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockEastJapanPIDS extends BlockPIDSBaseHorizontal {

    public BlockEastJapanPIDS(Settings settings) {
        super();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // 天井から吊り下がる形状 (モデルに合わせて調整してください)
        // IBlock.getVoxelShapeByDirection を使うのが MTR 流です
        return IBlock.getVoxelShapeByDirection(5.5, 0, 0, 10.5, 16, 16, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityEastJapanPIDS(pos, state);
    }
}