package com.magikal_hakase.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockEmergencyStopButton extends HorizontalFacingBlock {
    // 向きのプロパティを定義
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(5, 0, 12.5, 11, 23, 16);

    private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0, 0, 5, 3.5, 23, 11);

    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(5, 0, 0, 11, 23, 3.5);

    private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(12.5, 0, 5, 16, 23, 11);

    public BlockEmergencyStopButton(Settings settings) {
        super(settings);
        // デフォルトの向きを北に設定
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        switch (facing) {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_NORTH;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}