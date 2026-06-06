package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.ModBlockEntities;
import com.magikal_hakase.blockentities.TileEntityCrossingGate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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
import org.jetbrains.annotations.Nullable;

public class BlockCrossingGate extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;

    // 遮断機本体（メカボックス）の当たり判定（適宜モデルに合わせて調整してください）
    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 24.75, 14);

    public BlockCrossingGate(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityCrossingGate(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        // 設置時のレッドストーン信号を確認
        return this.getDefaultState()
                .with(FACING, ctx.getPlayerFacing().getOpposite())
                .with(POWERED, world.isReceivingRedstonePower(pos));
    }

    // レッドストーン信号の変化を監視
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean isPowered = world.isReceivingRedstonePower(pos);
            if (state.get(POWERED) != isPowered) {
                // ★ フラグを 2 から 3 に変更（クライアントへ確実にブロック更新を通知するフラグ）
                world.setBlockState(pos, state.with(POWERED, isPowered), 3);
            }
        }
    }

    // ★ 修正: BlockPos pos を削除し、@Override を追加
    @Nullable
    @Override
    public <T extends net.minecraft.block.entity.BlockEntity> net.minecraft.block.entity.BlockEntityTicker<T> getTicker(World world, BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CROSSING_GATE, TileEntityCrossingGate::tick);
    }

    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }
}