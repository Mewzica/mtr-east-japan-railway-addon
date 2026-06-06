package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.ModBlockEntities;
import mtr.block.BlockStationNameBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import static mtr.block.BlockStationNameTallBase.METAL;

public class BlockJREastVerticalSign extends BlockStationNameBase {

    public BlockJREastVerticalSign(Settings settings) {
        super(settings);
        // 初期状態の設定（向きだけ設定）
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, net.minecraft.util.math.Direction.NORTH));
    }

    // --- ここが前回不足していた重要なメソッドです ---
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        // ここで「このブロックは FACING（向き）というプロパティを持つ」と登録します
        // これがないと "Cannot set property... as it does not exist" でクラッシュします
        builder.add(FACING, METAL);
        // ※MTRの親クラスの仕様に合わせて METAL も念のため追加しておきます
    }
    // ----------------------------------------------

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // MTRの便利機能を使って、向きに合わせた薄い板の当たり判定を返します
        // IBlock.getVoxelShapeByDirection は使えない場合があるため、MTR標準の方法に合わせます
        return mtr.block.IBlock.getVoxelShapeByDirection(5.0, -2.0, 15.0, 11.0, 18.0, 16.0, state.get(FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // 設置された時の向きを決定
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityJREastVerticalSign(pos, state);
    }

    public static class TileEntityJREastVerticalSign extends BlockStationNameBase.TileEntityStationNameBase {
        public TileEntityJREastVerticalSign(BlockPos pos, BlockState state) {
            // 親クラスの引数に合わせて調整 (MTRのバージョンによって引数が異なる場合があります)
            // エラーが出る場合は前回の修正通り 0.0f を追加してください
            super(ModBlockEntities.JR_EAST_VERTICAL_SIGN, pos, state, 0.0f, 0.0f, false);
        }

        @Override
        public int getColor(BlockState blockState) {
            return 0;
        }
    }
}