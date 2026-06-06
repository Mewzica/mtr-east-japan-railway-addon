package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityCrossingSignmark;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class BlockCrossingSignmark extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(4, 2, 8, 12, 14, 16);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(0, 2, 4, 8, 14, 12);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(4, 2, 0, 12, 14, 8);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(8, 2, 4, 16, 14, 12);

    public BlockCrossingSignmark(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case EAST: return SHAPE_E;
            case SOUTH: return SHAPE_S;
            case WEST: return SHAPE_W;
            default: return SHAPE_N;
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityCrossingSignmark(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getPlayerFacing().getOpposite();

        // 1. 自身への直接のレッドストーン信号を確認
        boolean isPowered = world.isReceivingRedstonePower(pos);

        // 2. ★ 修正: 周囲4方向に通電中の柱があるか確認
        if (!isPowered) {
            isPowered = checkAdjacentPoles(world, pos);
        }

        return this.getDefaultState()
                .with(FACING, facing)
                .with(POWERED, isPowered);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            // 1. 自身への直接のレッドストーン信号を確認
            boolean isPowered = world.isReceivingRedstonePower(pos);

            // 2. ★ 修正: 周囲4方向に通電中の柱があるか確認
            if (!isPowered) {
                isPowered = checkAdjacentPoles(world, pos);
            }

            // 状態が変わっていれば更新
            if (state.get(POWERED) != isPowered) {
                world.setBlockState(pos, state.with(POWERED, isPowered), 3);
            }
        }
    }

    // ★ 新規追加: 水平4方向の隣接ブロックをループでスキャンし、通電中の柱があるか判定する
    private boolean checkAdjacentPoles(World world, BlockPos pos) {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);

            // 隣のブロックが判定対象の「柱」クラスかどうか
            if (isPoleBlock(neighborState.getBlock())) {
                // かつ、その柱が POWERED=true の状態を保持しているか
                if (neighborState.contains(Properties.POWERED) && neighborState.get(Properties.POWERED)) {
                    return true; // 1つでも見つかればその時点でON確定
                }
            }
        }
        return false;
    }

    // ★ 新規追加: 対象とする柱ブロックのリスト
    private boolean isPoleBlock(Block block) {
        return block instanceof BlockSignalPole ||
                block instanceof BlockConcreteCatenaryPole ||
                block instanceof BlockSteelPipeCatenaryPole ||
                block instanceof BlockCrossingPole;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }
}