package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntitySignalPlate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class BlockSignalPlate extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty ON_POLE = BooleanProperty.of("on_pole");

    // 通常の当たり判定 (壁用: ブロックの奥側)
    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(6, 1, 15, 10, 15, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(6, 1, 0, 10, 15, 1);
    private static final VoxelShape SHAPE_WEST  = Block.createCuboidShape(15, 1, 6, 16, 15, 10);
    private static final VoxelShape SHAPE_EAST  = Block.createCuboidShape(0, 1, 6, 1, 15, 10);

    // 柱用の当たり判定 (柱用: ブロックの手前側へ移動)
    private static final VoxelShape SHAPE_POLE_NORTH = Block.createCuboidShape(6, 1, 15, 10, 15, 16); // 調整が必要なら変更してください
    private static final VoxelShape SHAPE_POLE_SOUTH = Block.createCuboidShape(6, 1, 0, 10, 15, 1);
    private static final VoxelShape SHAPE_POLE_WEST  = Block.createCuboidShape(15, 1, 6, 16, 15, 10);
    private static final VoxelShape SHAPE_POLE_EAST  = Block.createCuboidShape(0, 1, 6, 1, 15, 10);

    public BlockSignalPlate(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(ON_POLE, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        boolean onPole = state.get(ON_POLE);
        switch (state.get(FACING)) {
            case SOUTH: return onPole ? SHAPE_POLE_SOUTH : SHAPE_SOUTH;
            case WEST:  return onPole ? SHAPE_POLE_WEST : SHAPE_WEST;
            case EAST:  return onPole ? SHAPE_POLE_EAST : SHAPE_EAST;
            default:    return onPole ? SHAPE_POLE_NORTH : SHAPE_NORTH;
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntitySignalPlate(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getPlayerFacing().getOpposite();
        // 背面に柱があるかチェック
        boolean onPole = isPole(ctx.getWorld(), ctx.getBlockPos().offset(facing.getOpposite()));
        return this.getDefaultState().with(FACING, facing).with(ON_POLE, onPole);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // 背面方向のブロックが更新されたら再チェック
        Direction backDirection = state.get(FACING).getOpposite();
        if (direction == backDirection) {
            boolean onPole = isPoleBlock(neighborState.getBlock());
            return state.with(ON_POLE, onPole);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    // ★修正: 指定された座標のブロックが柱かどうか判定
    private boolean isPole(BlockView world, BlockPos pos) {
        return isPoleBlock(world.getBlockState(pos).getBlock());
    }

    // ★追加: ブロックの種類が柱（信号機柱または架線柱）かどうか判定
    private boolean isPoleBlock(Block block) {
        return block instanceof BlockSignalPole ||
                block instanceof BlockConcreteCatenaryPole ||
                block instanceof BlockTrussCatenaryPole ||
                block instanceof BlockSteelPipeCatenaryPole;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_POLE);
    }
}