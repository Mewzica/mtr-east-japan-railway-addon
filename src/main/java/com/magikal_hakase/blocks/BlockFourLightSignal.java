package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityFourLightSignal;
import mtr.block.BlockSignalLightBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockFourLightSignal extends BlockSignalLightBase {

    private static final VoxelShape SHAPE = Block.createCuboidShape(2, -6, 2, 14, 22, 14);

    public BlockFourLightSignal(Settings settings) {
        // ★修正点: superの引数に「4」(=最大アスペクト値、4灯対応) と 「16」(=テクスチャ高さ) を指定
        // これによりMTRの信号ロジックが4灯式として扱ってくれます
        super(settings, 4, 16);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    // ★修正点: 戻り値の型を BlockEntity から BlockEntityMapper に変更
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityFourLightSignal(pos, state);
    }
}