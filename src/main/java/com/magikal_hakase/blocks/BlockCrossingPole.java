package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.ModBlockEntities;
import com.magikal_hakase.blockentities.TileEntityCrossingPole;
import com.magikal_hakase.data.EnumCrossingAttachment;
import com.magikal_hakase.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class BlockCrossingPole extends Block implements BlockEntityProvider {

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");

    public static final EnumProperty<EnumCrossingAttachment> NORTH = EnumProperty.of("north", EnumCrossingAttachment.class);
    public static final EnumProperty<EnumCrossingAttachment> EAST = EnumProperty.of("east", EnumCrossingAttachment.class);
    public static final EnumProperty<EnumCrossingAttachment> SOUTH = EnumProperty.of("south", EnumCrossingAttachment.class);
    public static final EnumProperty<EnumCrossingAttachment> WEST = EnumProperty.of("west", EnumCrossingAttachment.class);

    private static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 16, 11);

    public BlockCrossingPole(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(POWERED, false).with(UP, false).with(DOWN, false)
                .with(NORTH, EnumCrossingAttachment.NONE)
                .with(EAST, EnumCrossingAttachment.NONE)
                .with(SOUTH, EnumCrossingAttachment.NONE)
                .with(WEST, EnumCrossingAttachment.NONE));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityCrossingPole(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE; // 必要に応じてアームの当たり判定を追加できます
    }

    // --- アイテム取り付け処理 ---
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();
        Direction side = hit.getSide();

        // 上下面をクリックした場合は、プレイヤーの向きを基準にする
        if (side.getAxis().isVertical()) {
            side = player.getHorizontalFacing().getOpposite();
        }

        EnumProperty<EnumCrossingAttachment> property = getPropertyForDirection(side);
        EnumCrossingAttachment currentAttachment = state.get(property);

        // すでに取り付けられている場合は何もしない
        if (currentAttachment != EnumCrossingAttachment.NONE) return ActionResult.PASS;

        EnumCrossingAttachment newAttachment = EnumCrossingAttachment.NONE;

        // ★ ここは実際のアイテム変数名に合わせてください
        if (item == ModItems.CROSSING_SIGN) newAttachment = EnumCrossingAttachment.SIGN;
        else if (item == ModItems.CROSSING_LIGHT_H) newAttachment = EnumCrossingAttachment.LIGHT_H;
        else if (item == ModItems.CROSSING_LIGHT_V) newAttachment = EnumCrossingAttachment.LIGHT_V;
        else if (item == ModItems.CROSSING_SPEAKER) newAttachment = EnumCrossingAttachment.SPEAKER;

        if (newAttachment != EnumCrossingAttachment.NONE) {
            if (!world.isClient) {
                world.setBlockState(pos, state.with(property, newAttachment), 3);
                world.playSound(null, pos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                if (!player.isCreative()) stack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private EnumProperty<EnumCrossingAttachment> getPropertyForDirection(Direction dir) {
        switch (dir) {
            case EAST: return EAST;
            case SOUTH: return SOUTH;
            case WEST: return WEST;
            default: return NORTH;
        }
    }

    // --- レッドストーン＆接続処理 ---
    private boolean connectsTo(BlockState state) {
        return state.getBlock() instanceof BlockCrossingPole;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        // ★ 修正: 設置時も「柱全体」のレッドストーン状態を確認する
        boolean isPowered = isColumnPowered(world, pos);

        return this.getDefaultState()
                .with(UP, connectsTo(world.getBlockState(pos.up())))
                .with(DOWN, connectsTo(world.getBlockState(pos.down())))
                .with(POWERED, isPowered);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP) state = state.with(UP, connectsTo(neighborState));
        else if (direction == Direction.DOWN) state = state.with(DOWN, connectsTo(neighborState));
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    // ★ 修正: 隣接ブロックが更新された際の処理
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            // この柱の連なりの「どこか」にレッドストーン信号が来ているかスキャンする
            boolean shouldBePowered = isColumnPowered(world, pos);

            if (state.get(POWERED) != shouldBePowered) {
                world.setBlockState(pos, state.with(POWERED, shouldBePowered), 3);
                // 自身が更新されたら、周囲（側面のライトや上下の柱）にも更新を伝播させる
                world.updateNeighborsAlways(pos, this);
            }
        }
    }

    // ★ 新規追加: 上下に連なる柱をすべてチェックし、どこかに直接信号が来ているか判定するメソッド
    private boolean isColumnPowered(World world, BlockPos startPos) {
        // 1. まず自分自身に信号が来ているか
        if (world.isReceivingRedstonePower(startPos)) return true;

        // 2. 下に向かって柱が続く限りチェック
        BlockPos currentPos = startPos.down();
        while (world.getBlockState(currentPos).getBlock() instanceof BlockCrossingPole) {
            if (world.isReceivingRedstonePower(currentPos)) return true;
            currentPos = currentPos.down();
        }

        // 3. 上に向かって柱が続く限りチェック
        currentPos = startPos.up();
        while (world.getBlockState(currentPos).getBlock() instanceof BlockCrossingPole) {
            if (world.isReceivingRedstonePower(currentPos)) return true;
            currentPos = currentPos.up();
        }

        // どこにも信号が来ていなければ false
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.block.entity.BlockEntityTicker<T> getTicker(World world, BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CROSSING_POLE, TileEntityCrossingPole::tick);
    }

    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}