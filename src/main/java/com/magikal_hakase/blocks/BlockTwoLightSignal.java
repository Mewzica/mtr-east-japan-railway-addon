package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityTwoLightSignal;
import mtr.block.BlockSignalLightBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockTwoLightSignal extends BlockSignalLightBase {

    // ポールの当たり判定
    private static final VoxelShape SHAPE = Block.createCuboidShape(2, -6, 2, 14, 22, 14);

    public BlockTwoLightSignal(Settings settings) {
        // aspects: 2 (赤・緑 の2段階判定)
        super(settings, 2, 16);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityTwoLightSignal(pos, state);
    }
}