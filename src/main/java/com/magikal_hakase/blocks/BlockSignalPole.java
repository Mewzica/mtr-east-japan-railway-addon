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

public class BlockSignalPole extends Block {

    public static final BooleanProperty POWERED = Properties.POWERED;

    public enum ConnectionType implements StringIdentifiable {
        NONE("none"),
        FOUR_LIGHT("four_light"),
        THREE_LIGHT("three_light"),
        TWO_LIGHT("two_light"),
        SWITCHING("switching"),
        CROSSING_SIGNMARK("crossing_signmark");

        private final String name;
        ConnectionType(String name) { this.name = name; }
        @Override public String asString() { return this.name; }
    }

    public static final EnumProperty<ConnectionType> NORTH = EnumProperty.of("north", ConnectionType.class);
    public static final EnumProperty<ConnectionType> EAST = EnumProperty.of("east", ConnectionType.class);
    public static final EnumProperty<ConnectionType> SOUTH = EnumProperty.of("south", ConnectionType.class);
    public static final EnumProperty<ConnectionType> WEST = EnumProperty.of("west", ConnectionType.class);

    private static final VoxelShape SHAPE_POST = Block.createCuboidShape(6, 0, 6, 10, 16, 10);
    // アームの当たり判定 (共通でOK、必要なら分ける)
    private static final VoxelShape SHAPE_N = Block.createCuboidShape(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(10, 6, 6, 16, 10, 10);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(6, 6, 10, 10, 10, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(0, 6, 6, 6, 10, 10);

    public BlockSignalPole(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, ConnectionType.NONE)
                .with(EAST, ConnectionType.NONE)
                .with(SOUTH, ConnectionType.NONE)
                .with(WEST, ConnectionType.NONE)
                .with(POWERED, false));
    }

    private ConnectionType getConnectionType(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof BlockFourLightSignal) {
            return ConnectionType.FOUR_LIGHT;
        } else if (block instanceof BlockThreeLightSignal) {
            return ConnectionType.THREE_LIGHT;
        } else if (block instanceof BlockTwoLightSignal) {
            return ConnectionType.TWO_LIGHT;
        } else if (block instanceof BlockSwitchingSignal) {
            return ConnectionType.SWITCHING;
        } else if (block instanceof BlockEmergencyStopAlarm) {
            return ConnectionType.TWO_LIGHT;
        } else if (block instanceof BlockCrossingSignmark) {
            return ConnectionType.CROSSING_SIGNMARK;
        }

        return ConnectionType.NONE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockView world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState()
                .with(NORTH, getConnectionType(world.getBlockState(pos.north())))
                .with(EAST, getConnectionType(world.getBlockState(pos.east())))
                .with(SOUTH, getConnectionType(world.getBlockState(pos.south())))
                .with(WEST, getConnectionType(world.getBlockState(pos.west())));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal()) {
            // 隣が変わったらタイプを再取得
            ConnectionType type = getConnectionType(neighborState);
            switch (direction) {
                case NORTH: return state.with(NORTH, type);
                case EAST:  return state.with(EAST, type);
                case SOUTH: return state.with(SOUTH, type);
                case WEST:  return state.with(WEST, type);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = SHAPE_POST;
        // NONE じゃなければアームの当たり判定を追加
        if (state.get(NORTH) != ConnectionType.NONE) shape = VoxelShapes.union(shape, SHAPE_N);
        if (state.get(EAST) != ConnectionType.NONE)  shape = VoxelShapes.union(shape, SHAPE_E);
        if (state.get(SOUTH) != ConnectionType.NONE) shape = VoxelShapes.union(shape, SHAPE_S);
        if (state.get(WEST) != ConnectionType.NONE)  shape = VoxelShapes.union(shape, SHAPE_W);
        return shape;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, POWERED);
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