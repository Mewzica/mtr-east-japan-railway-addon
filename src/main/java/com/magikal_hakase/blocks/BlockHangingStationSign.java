package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityHangingStationSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class BlockHangingStationSign extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.createCuboidShape(-14.0, 0.0, 7.25, 30.0, 16.0, 8.75);

    private static final VoxelShape SHAPE_EAST_WEST = Block.createCuboidShape(7.25, 0, -14, 8.75, 16, 30);

    public BlockHangingStationSign(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityHangingStationSign(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        switch (facing) {
            case NORTH:
            case SOUTH:
                return SHAPE_NORTH_SOUTH;
            case EAST:
            case WEST:
                return SHAPE_EAST_WEST;
            default:
                return SHAPE_NORTH_SOUTH;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // プレイヤーの向きと逆（対面）になるように設置
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}