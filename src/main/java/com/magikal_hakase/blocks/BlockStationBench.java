package com.magikal_hakase.blocks;

import com.magikal_hakase.data.BenchPart;
import com.magikal_hakase.entity.SeatEntity;
import mtr.block.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class BlockStationBench extends HorizontalFacingBlock {

    public static final EnumProperty<BenchPart> PART = EnumProperty.of("part", BenchPart.class);
    public static final BooleanProperty HAS_POLE = BooleanProperty.of("has_pole");

    public BlockStationBench(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(PART, BenchPart.SINGLE)
                .with(HAS_POLE, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, HAS_POLE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        Item mtrBrush = Registry.ITEM.get(new Identifier("mtr", "brush"));

        // 1. ブラシを持っている場合：支柱の表示切り替え
        if (stack.getItem() == mtrBrush) {
            if (!world.isClient) {
                world.setBlockState(pos, state.cycle(HAS_POLE));
            }
            return ActionResult.SUCCESS;
        }
        // 2. ブラシを持っていない場合：座る処理
        else {
            if (!world.isClient) {
                // 座席エンティティをスポーンさせる
                // Y座標(+0.4)はベンチの高さに合わせて調整してください
                SeatEntity seat = new SeatEntity(world, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5);
                world.spawnEntity(seat);
                player.startRiding(seat);
            }
            return ActionResult.SUCCESS;
        }
    }

    // ... (以下、getPlacementState等は元のままでOK) ...

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx).with(FACING, ctx.getPlayerFacing().getOpposite());
        return state.with(PART, getPartState(state, ctx.getWorld(), ctx.getBlockPos()));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.with(PART, getPartState(state, world, pos));
    }

    private BenchPart getPartState(BlockState state, WorldAccess world, BlockPos pos) {
        Direction facing = state.get(FACING);
        Direction leftDir = facing.rotateYCounterclockwise();
        Direction rightDir = facing.rotateYClockwise();

        BlockState leftNeighbor = world.getBlockState(pos.offset(leftDir));
        BlockState rightNeighbor = world.getBlockState(pos.offset(rightDir));

        boolean hasLeft = leftNeighbor.isOf(this) && leftNeighbor.get(FACING) == facing;
        boolean hasRight = rightNeighbor.isOf(this) && rightNeighbor.get(FACING) == facing;

        if (hasLeft && hasRight) {
            return BenchPart.MIDDLE;
        } else if (hasLeft) {
            return BenchPart.RIGHT;
        } else if (hasRight) {
            return BenchPart.LEFT;
        } else {
            return BenchPart.SINGLE;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(2, 0, 2, 14, 16, 14, facing); // 仮設定: 一旦フルブロック判定にして動作確認推奨
    }
}