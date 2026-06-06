package com.magikal_hakase.blocks;

import mtr.block.BlockTicketMachine;
import mtr.block.IBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockEastJapanTicketMachine extends BlockTicketMachine {

    public BlockEastJapanTicketMachine(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        double height = IBlock.getStatePropertySafe(state, Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? 16.0 : 16;
        return IBlock.getVoxelShapeByDirection(0, 0, 0, 16, height, 12.5, facing);
    }
}
