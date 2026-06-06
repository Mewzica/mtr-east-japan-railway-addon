package com.magikal_hakase.blocks;

import mtr.block.BlockTicketBarrier;
import mtr.block.IBlock;
import mtr.data.TicketSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BlockEastJapanTicketBarrier extends BlockTicketBarrier {
    public BlockEastJapanTicketBarrier(boolean isEntrance) {
        super(isEntrance);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(13.0, 0, -2.5, 16, 18, 18.5, facing);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos blockPos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final TicketSystem.EnumTicketBarrierOpen open = IBlock.getStatePropertySafe(state, OPEN);
        final VoxelShape base = IBlock.getVoxelShapeByDirection(13.0, 0, -2.5, 16, 24, 18.5, facing);
        return open.isOpen() ? base : VoxelShapes.union(IBlock.getVoxelShapeByDirection(0.0, 0.0, 7.0, 16.0, 24.0, 9.0, facing), base);
    }
}
