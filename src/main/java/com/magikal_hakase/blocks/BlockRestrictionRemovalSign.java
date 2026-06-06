package com.magikal_hakase.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockRestrictionRemovalSign extends HorizontalFacingBlock {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // ★ここで当たり判定の形を作ります (x1, y1, z1, x2, y2, z2)
    // 16が1ブロックの幅です。
    // 例: 厚さ2ドットの板を中央に配置する場合

    // 北(NORTH)・南(SOUTH)向き用の形: Z軸方向に薄い (Z: 7～9)
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.createCuboidShape(7.5, 0, 7.5, 8.5, 16, 8.5);

    // 東(EAST)・西(WEST)向き用の形: X軸方向に薄い (X: 7～9)
    private static final VoxelShape SHAPE_EAST_WEST = Block.createCuboidShape(7.5, 0, 7.5, 8.5, 16, 8.5);

    public BlockRestrictionRemovalSign(Settings settings) {
        super(settings);
        // デフォルトの向きを北に設定
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    // ★重要: ブロックの形を返すメソッド
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        // 向きによって返す形を変える
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

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    // ブロックが持つプロパティ（向き）を登録
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}