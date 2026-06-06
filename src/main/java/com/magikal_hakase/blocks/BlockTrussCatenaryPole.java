package com.magikal_hakase.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class BlockTrussCatenaryPole extends Block {

    public enum PoleConnection implements StringIdentifiable {
        NONE("none"),
        POLE("pole"),              // 架線柱同士（梁）
        FOUR_LIGHT("four_light"),  // 4灯式など（アーム）
        THREE_LIGHT("three_light"),
        TWO_LIGHT("two_light"),
        SWITCHING("switching");

        private final String name;
        PoleConnection(String name) { this.name = name; }
        @Override public String asString() { return this.name; }
    }

    // ★ BooleanProperty を EnumProperty に変更
    public static final EnumProperty<PoleConnection> NORTH = EnumProperty.of("north", PoleConnection.class);
    public static final EnumProperty<PoleConnection> EAST = EnumProperty.of("east", PoleConnection.class);
    public static final EnumProperty<PoleConnection> SOUTH = EnumProperty.of("south", PoleConnection.class);
    public static final EnumProperty<PoleConnection> WEST = EnumProperty.of("west", PoleConnection.class);

    // 上下は架線柱同士の接続のみなのでBooleanのままでOK
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");

    // 当たり判定 (Post)
    private static final VoxelShape SHAPE_POST = Block.createCuboidShape(4, 0, 4, 12, 16, 12);
    // 梁の当たり判定 (Beam)
    private static final VoxelShape SHAPE_N = Block.createCuboidShape(5, 5, 0, 11, 11, 5);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(11, 5, 5, 16, 11, 11);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(5, 5, 11, 11, 11, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(0, 5, 5, 5, 11, 11);

    public BlockTrussCatenaryPole(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, PoleConnection.NONE)
                .with(EAST, PoleConnection.NONE)
                .with(SOUTH, PoleConnection.NONE)
                .with(WEST, PoleConnection.NONE)
                .with(UP, false).with(DOWN, false));
    }

    // ★ 隣接ブロックに応じた接続タイプを判定
    private PoleConnection getConnection(BlockState neighborState) {
        Block block = neighborState.getBlock();

        // 1. 自分と同じ架線柱なら "POLE" (梁)
        if (block instanceof BlockTrussCatenaryPole) {
            return PoleConnection.POLE;
        }
        // 2. 信号機なら種類に応じたタイプ (アーム)
        // ※クラス名は実際の環境に合わせてください
        if (block instanceof BlockFourLightSignal) {
            return PoleConnection.FOUR_LIGHT;
        } else if (block instanceof BlockThreeLightSignal) {
            return PoleConnection.THREE_LIGHT;
        } else if (block instanceof BlockTwoLightSignal || block instanceof BlockEmergencyStopAlarm) {
            return PoleConnection.TWO_LIGHT;
        } else if (block instanceof BlockSwitchingSignal) {
            return PoleConnection.SWITCHING;
        }

        return PoleConnection.NONE;
    }

    private boolean connectsToPole(BlockState state) {
        return state.getBlock() instanceof BlockTrussCatenaryPole;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockView world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState()
                .with(NORTH, getConnection(world.getBlockState(pos.north())))
                .with(EAST, getConnection(world.getBlockState(pos.east())))
                .with(SOUTH, getConnection(world.getBlockState(pos.south())))
                .with(WEST, getConnection(world.getBlockState(pos.west())))
                .with(UP, connectsToPole(world.getBlockState(pos.up())))
                .with(DOWN, connectsToPole(world.getBlockState(pos.down())));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // 横方向の更新
        if (direction.getAxis().isHorizontal()) {
            PoleConnection type = getConnection(neighborState);
            switch (direction) {
                case NORTH: return state.with(NORTH, type);
                case EAST:  return state.with(EAST, type);
                case SOUTH: return state.with(SOUTH, type);
                case WEST:  return state.with(WEST, type);
            }
        }
        // 縦方向の更新
        if (direction == Direction.UP)   return state.with(UP, connectsToPole(neighborState));
        if (direction == Direction.DOWN) return state.with(DOWN, connectsToPole(neighborState));

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();

        // 柱の判定ロジック: NONE以外なら接続ありとみなす
        boolean hasHorizontalConnection =
                state.get(NORTH) != PoleConnection.NONE ||
                        state.get(EAST) != PoleConnection.NONE ||
                        state.get(SOUTH) != PoleConnection.NONE ||
                        state.get(WEST) != PoleConnection.NONE;

        boolean isPost = state.get(UP) || state.get(DOWN) || !hasHorizontalConnection;

        if (isPost) shape = VoxelShapes.union(shape, SHAPE_POST);

        // 梁・アームの判定 (信号機の場合も梁の判定を流用して表示)
        if (state.get(NORTH) != PoleConnection.NONE) shape = VoxelShapes.union(shape, SHAPE_N);
        if (state.get(EAST) != PoleConnection.NONE)  shape = VoxelShapes.union(shape, SHAPE_E);
        if (state.get(SOUTH) != PoleConnection.NONE) shape = VoxelShapes.union(shape, SHAPE_S);
        if (state.get(WEST) != PoleConnection.NONE)  shape = VoxelShapes.union(shape, SHAPE_W);

        // 水平接続のみの場合の中心補完
        if (!isPost && hasHorizontalConnection) {
            shape = VoxelShapes.union(shape, Block.createCuboidShape(5, 5, 5, 11, 11, 11));
        }

        return shape;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }
}