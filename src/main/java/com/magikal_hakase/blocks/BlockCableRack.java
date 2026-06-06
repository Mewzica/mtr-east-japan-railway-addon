package com.magikal_hakase.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class BlockCableRack extends Block {

    // 東西南北の接続フラグ
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty WEST = BooleanProperty.of("west");

    // 当たり判定 (形状に合わせて調整してください)
    // ここでは高さ4ドット(Y=0~4)のラックを想定
    private static final VoxelShape SHAPE_CENTER = Block.createCuboidShape(4, 7.5, 4, 12, 11, 12);
    private static final VoxelShape SHAPE_N = Block.createCuboidShape(4, 7.5, 0, 12, 11, 4);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(12, 7.5, 4, 16, 11, 12);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(4, 7.5, 12, 12, 11, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(0, 7.5, 4, 4, 11, 12);

    public BlockCableRack(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false));
    }

    // 隣が同じケーブルラックなら接続する
    private boolean connectsTo(BlockState state) {
        return state.getBlock() instanceof BlockCableRack;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = SHAPE_CENTER;
        if (state.get(NORTH)) shape = VoxelShapes.union(shape, SHAPE_N);
        if (state.get(EAST))  shape = VoxelShapes.union(shape, SHAPE_E);
        if (state.get(SOUTH)) shape = VoxelShapes.union(shape, SHAPE_S);
        if (state.get(WEST))  shape = VoxelShapes.union(shape, SHAPE_W);
        return shape;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockView world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState()
                .with(NORTH, connectsTo(world.getBlockState(pos.north())))
                .with(EAST, connectsTo(world.getBlockState(pos.east())))
                .with(SOUTH, connectsTo(world.getBlockState(pos.south())))
                .with(WEST, connectsTo(world.getBlockState(pos.west())));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // 横方向の隣接ブロックが変わったら接続状態を更新
        if (direction.getAxis().isHorizontal()) {
            boolean isConnected = connectsTo(neighborState);
            switch (direction) {
                case NORTH: return state.with(NORTH, isConnected);
                case EAST:  return state.with(EAST, isConnected);
                case SOUTH: return state.with(SOUTH, isConnected);
                case WEST:  return state.with(WEST, isConnected);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }
}