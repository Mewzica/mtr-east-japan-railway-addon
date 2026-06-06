package com.magikal_hakase.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockRaceway extends HorizontalFacingBlock {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    // 金具がついているかどうかのフラグ
    public static final BooleanProperty WITH_BRACKET = BooleanProperty.of("with_bracket");

    // 当たり判定 (細長いビーム)
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.createCuboidShape(6.25, 0, 0, 9.75, 3, 16);

    private static final VoxelShape SHAPE_EAST_WEST = Block.createCuboidShape(0, 0, 6.25, 16, 3, 9.75);

    public BlockRaceway(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WITH_BRACKET, false)); // デフォルトは金具なし
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

    // ★右クリック時の動作
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);

        // 金具がついている場合は何もしない
        if (state.get(WITH_BRACKET)) return ActionResult.PASS;

        // 手に持っているのが「吊り下げ金具」アイテムかチェック
        Item bracketItem = ModBlocks.HANGING_BRACKET.asItem(); // ModBlocksからアイテム取得

        if (stack.getItem() == bracketItem) {
            if (!world.isClient) {
                // 金具付きの状態に変更
                world.setBlockState(pos, state.with(WITH_BRACKET, true), 3);

                // クリエイティブでなければアイテムを1つ消費
                if (!player.isCreative()) {
                    stack.decrement(1);
                }

                // 金属を叩くような音を鳴らす
                world.playSound(null, pos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WITH_BRACKET);
    }
}