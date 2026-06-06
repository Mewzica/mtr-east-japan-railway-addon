package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityThreeLightSignal;
import mtr.block.BlockSignalLightBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockThreeLightSignal extends BlockSignalLightBase {

    private static final VoxelShape SHAPE = Block.createCuboidShape(2, -6, 2, 14, 22, 14);

    public BlockThreeLightSignal(Settings settings) {
        // aspects: 3 (赤判定、黄判定まで行う)
        super(settings, 3, 16);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityThreeLightSignal(pos, state);
    }
}