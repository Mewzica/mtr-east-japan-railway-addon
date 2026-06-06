package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityCommunicationInsulator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class BlockCatenaryStructure extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    // 左右に繋がっているかのフラグ
    public static final BooleanProperty LEFT = BooleanProperty.of("left");
    public static final BooleanProperty RIGHT = BooleanProperty.of("right");

    // 当たり判定 (必要に応じて調整してください)
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.createCuboidShape(0, -3, 4, 16, 12, 12);

    private static final VoxelShape SHAPE_EAST_WEST = Block.createCuboidShape(4, -3, 0, 12, 12, 16);

    public BlockCatenaryStructure(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LEFT, false)
                .with(RIGHT, false));
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        // 通信線碍子と同じTileEntityを使用
        return new TileEntityCommunicationInsulator(pos, state);
    }

    // --- 接続状態の更新ロジック ---

    // 設置時の初期状態決定
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getPlayerFacing().getOpposite();

        return this.getDefaultState()
                .with(FACING, facing)
                .with(LEFT, canConnect(world, pos, facing, true))
                .with(RIGHT, canConnect(world, pos, facing, false));
    }

    // 隣のブロックが更新された時の再計算
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        return state
                .with(LEFT, canConnect(world, pos, facing, true))
                .with(RIGHT, canConnect(world, pos, facing, false));
    }

    // 接続判定ヘルパー
    // isLeft: trueなら左側、falseなら右側をチェック
    private boolean canConnect(WorldAccess world, BlockPos pos, Direction facing, boolean isLeft) {
        // 向いている方向に対して、左は「時計回り」、右は「反時計回り」
        // (MinecraftのDirection回転ロジックに基づく)
        Direction targetDir = isLeft ? facing.rotateYClockwise() : facing.rotateYCounterclockwise();
        BlockPos targetPos = pos.offset(targetDir);
        BlockState targetState = world.getBlockState(targetPos);

        // 隣が「やぐら」なら繋がる
        // (必要なら「向きが同じ場合のみ」などの条件を追加可能)
        return targetState.getBlock() instanceof BlockCatenaryStructure;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEFT, RIGHT);
    }

    // 破壊時の接続解除 (BlockCommunicationInsulatorと同じ)
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof TileEntityCommunicationInsulator insulator) {
                for (BlockPos targetPos : insulator.connections) {
                    BlockEntity targetBe = world.getBlockEntity(targetPos);
                    if (targetBe instanceof TileEntityCommunicationInsulator targetInsulator) {
                        targetInsulator.removeConnection(pos);
                    }
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }
}