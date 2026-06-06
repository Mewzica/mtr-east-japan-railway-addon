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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BrailleBlock extends HorizontalFacingBlock {
    // 向きのプロパティを定義
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // 見た目の形状: 高さ1ピクセル (カーペットと同じ見た目)
    // 0.0D(底) から 1.0D(高さ1/16ブロック) まで
    private static final VoxelShape OUTLINE_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public BrailleBlock(Settings settings) {
        super(settings);
        // デフォルトの向きを北に設定
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    // 1. 当たり判定 (Collision Shape)
    // "カーペットより薄い" = プレイヤーが引っかからないように、当たり判定を空(0)にします。
    // これにより、レールの上を歩くようにスムーズに移動できます。
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    // 2. 選択判定・見た目の枠 (Outline Shape)
    // カーソルを合わせた時の枠の大きさです。ここは視認性のために厚さ1ピクセル確保します。
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}