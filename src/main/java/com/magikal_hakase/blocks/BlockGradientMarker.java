package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityGradientMarker;
import com.magikal_hakase.data.EnumGradientType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class BlockGradientMarker extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<EnumGradientType> FRONT_TYPE = EnumProperty.of("front_type", EnumGradientType.class);
    public static final EnumProperty<EnumGradientType> BACK_TYPE = EnumProperty.of("back_type", EnumGradientType.class);

    private static final VoxelShape SHAPE = Block.createCuboidShape(7, 0, 7, 9, 13.5, 9);

    public BlockGradientMarker(Settings settings) {
        super(settings);
        // デフォルト: Right (右側に板がある)
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FRONT_TYPE, EnumGradientType.UP)
                .with(BACK_TYPE, EnumGradientType.DOWN));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityGradientMarker(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FRONT_TYPE, BACK_TYPE);
    }
}