package com.magikal_hakase.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class BlockSteelPipeCatenaryPole extends Block {

    public static final BooleanProperty POWERED = Properties.POWERED;

    // 接続タイプ定義
    public enum PoleConnection implements StringIdentifiable {
        NONE("none"),
        POLE("pole"),              // 架線柱同士（梁/カーブ）
        FOUR_LIGHT("four_light"),  // 信号機（アーム）
        THREE_LIGHT("three_light"),
        TWO_LIGHT("two_light"),
        SWITCHING("switching"),
        CROSSING_SIGNMARK("crossing_signmark");

        private final String name;
        PoleConnection(String name) { this.name = name; }
        @Override public String asString() { return this.name; }
    }

    // プロパティ
    public static final EnumProperty<PoleConnection> NORTH = EnumProperty.of("north", PoleConnection.class);
    public static final EnumProperty<PoleConnection> EAST = EnumProperty.of("east", PoleConnection.class);
    public static final EnumProperty<PoleConnection> SOUTH = EnumProperty.of("south", PoleConnection.class);
    public static final EnumProperty<PoleConnection> WEST = EnumProperty.of("west", PoleConnection.class);
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");

    // ★ ポスト（垂直の柱）を描画するかどうかをJava側で判定して渡すフラグ
    public static final BooleanProperty RENDER_POST = BooleanProperty.of("render_post");

    // 当たり判定
    private static final VoxelShape SHAPE_POST = Block.createCuboidShape(4.5, 0, 4.5, 11.5, 16, 11.5);
    private static final VoxelShape SHAPE_N = Block.createCuboidShape(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(10, 6, 6, 16, 10, 10);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(6, 6, 10, 10, 10, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(0, 6, 6, 6, 10, 10);

    public BlockSteelPipeCatenaryPole(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, PoleConnection.NONE)
                .with(EAST, PoleConnection.NONE)
                .with(SOUTH, PoleConnection.NONE)
                .with(WEST, PoleConnection.NONE)
                .with(UP, false)
                .with(DOWN, false)
                .with(RENDER_POST, true)); // デフォルトは表示
    }

    // 隣接ブロックに応じた接続タイプを判定
    private PoleConnection getConnection(BlockState neighborState) {
        Block block = neighborState.getBlock();
        if (block instanceof BlockSteelPipeCatenaryPole) return PoleConnection.POLE;
        if (block instanceof BlockFourLightSignal) return PoleConnection.FOUR_LIGHT;
        if (block instanceof BlockThreeLightSignal) return PoleConnection.THREE_LIGHT;
        if (block instanceof BlockTwoLightSignal || block instanceof BlockEmergencyStopAlarm) return PoleConnection.TWO_LIGHT;
        if (block instanceof BlockSwitchingSignal) return PoleConnection.SWITCHING;
        if (block instanceof BlockCrossingSignmark) return PoleConnection.CROSSING_SIGNMARK;

        return PoleConnection.NONE;
    }

    private boolean connectsToPole(BlockState state) {
        return state.getBlock() instanceof BlockSteelPipeCatenaryPole;
    }

    // ★ ポストを描画すべきか判定するロジック
    // ルール: 「上に続く場合(UP)」または「上端(UP=false)だが、架線柱(POLE)の接続がない場合」はポストを描画する。
    // (架線柱の接続がある上端は、Curveモデルがポストの役割を兼ねるため描画しない)
    private boolean shouldRenderPost(boolean up, PoleConnection n, PoleConnection e, PoleConnection s, PoleConnection w) {
        if (up) return true; // 途中なら必ず描画
        // 上端の場合、POLE接続が一つもなければ描画 (信号機のみ接続の場合も含む)
        boolean hasPoleConnection = (n == PoleConnection.POLE || e == PoleConnection.POLE || s == PoleConnection.POLE || w == PoleConnection.POLE);
        return !hasPoleConnection;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockView world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        PoleConnection n = getConnection(world.getBlockState(pos.north()));
        PoleConnection e = getConnection(world.getBlockState(pos.east()));
        PoleConnection s = getConnection(world.getBlockState(pos.south()));
        PoleConnection w = getConnection(world.getBlockState(pos.west()));
        boolean up = connectsToPole(world.getBlockState(pos.up()));
        boolean down = connectsToPole(world.getBlockState(pos.down()));

        return this.getDefaultState()
                .with(NORTH, n).with(EAST, e).with(SOUTH, s).with(WEST, w)
                .with(UP, up).with(DOWN, down)
                .with(POWERED, false)
                .with(RENDER_POST, shouldRenderPost(up, n, e, s, w));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // 状態を更新
        BlockState newState = state;
        if (direction.getAxis().isHorizontal()) {
            newState = newState.with(getFacingProperty(direction), getConnection(neighborState));
        } else if (direction == Direction.UP) {
            newState = newState.with(UP, connectsToPole(neighborState));
        } else if (direction == Direction.DOWN) {
            newState = newState.with(DOWN, connectsToPole(neighborState));
        }

        // RENDER_POST を再計算
        boolean up = newState.get(UP);
        return newState.with(RENDER_POST, shouldRenderPost(up,
                newState.get(NORTH), newState.get(EAST), newState.get(SOUTH), newState.get(WEST)));
    }

    private EnumProperty<PoleConnection> getFacingProperty(Direction dir) {
        switch (dir) {
            case NORTH: return NORTH;
            case EAST: return EAST;
            case SOUTH: return SOUTH;
            case WEST: return WEST;
            default: return NORTH;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();

        // ポスト判定はプロパティを見るだけでOK
        if (state.get(RENDER_POST)) {
            shape = VoxelShapes.union(shape, SHAPE_POST);
        }

        // 接続（梁やアーム）の判定
        if (state.get(NORTH) != PoleConnection.NONE) shape = VoxelShapes.union(shape, SHAPE_N);
        if (state.get(EAST) != PoleConnection.NONE)  shape = VoxelShapes.union(shape, SHAPE_E);
        if (state.get(SOUTH) != PoleConnection.NONE) shape = VoxelShapes.union(shape, SHAPE_S);
        if (state.get(WEST) != PoleConnection.NONE)  shape = VoxelShapes.union(shape, SHAPE_W);

        // 水平接続のみの場合の補完（ポストがない場合）
        // Curveモデルなどは中心部分も埋めるはずですが、念のため
        if (!state.get(RENDER_POST) && (state.get(NORTH) != PoleConnection.NONE || state.get(EAST) != PoleConnection.NONE || state.get(WEST) != PoleConnection.NONE || state.get(SOUTH) != PoleConnection.NONE)) {
            shape = VoxelShapes.union(shape, Block.createCuboidShape(5, 5, 5, 11, 11, 11));
        }

        return shape;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, RENDER_POST, POWERED);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            // この柱の連なりの「どこか」にレッドストーン信号が来ているかスキャン
            boolean shouldBePowered = isColumnPowered(world, pos);

            if (state.get(POWERED) != shouldBePowered) {
                world.setBlockState(pos, state.with(POWERED, shouldBePowered), 3);
                // 自身が更新されたら、周囲（側面の動作反応灯や上下の柱）にも更新を伝播させる
                world.updateNeighborsAlways(pos, this);
            }
        }
    }

    private boolean isColumnPowered(World world, BlockPos startPos) {
        // 1. まず自分自身に直接信号が来ているか
        if (world.isReceivingRedstonePower(startPos)) return true;

        // 2. 下に向かって柱が続く限りチェック
        BlockPos currentPos = startPos.down();
        while (isPoleBlock(world.getBlockState(currentPos).getBlock())) {
            if (world.isReceivingRedstonePower(currentPos)) return true;
            currentPos = currentPos.down();
        }

        // 3. 上に向かって柱が続く限りチェック
        currentPos = startPos.up();
        while (isPoleBlock(world.getBlockState(currentPos).getBlock())) {
            if (world.isReceivingRedstonePower(currentPos)) return true;
            currentPos = currentPos.up();
        }

        return false;
    }

    // どの柱の種類が繋がっていても伝達するようにする
    private boolean isPoleBlock(Block block) {
        return block instanceof BlockSignalPole ||
                block instanceof BlockConcreteCatenaryPole ||
                block instanceof BlockSteelPipeCatenaryPole ||
                block instanceof BlockCrossingPole;
    }
}