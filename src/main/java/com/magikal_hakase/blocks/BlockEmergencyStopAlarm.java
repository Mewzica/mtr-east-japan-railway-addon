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

public class BlockEmergencyStopAlarm extends HorizontalFacingBlock {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // 当たり判定 (北向き)
    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(-0.5, -0.5, 4.75, 16.5, 16.25, 12);
    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(-0.5, -0.5, 4, 16.5, 16.25, 11.25);
    private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(4.75, -0.5, -0.5, 12, 16.25, 16.5);
    private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(4, -0.5, -0.5, 11.25, 16.25, 16.5);

    public BlockEmergencyStopAlarm(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
            default:
                return SHAPE_NORTH;
        }
    }

    // 設置時の向き設定
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }
}