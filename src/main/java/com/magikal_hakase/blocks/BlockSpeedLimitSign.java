package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntitySpeedLimitSign;
import com.magikal_hakase.data.EnumSpeedLimitType;
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

public class BlockSpeedLimitSign extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<EnumSpeedLimitType> TYPE = EnumProperty.of("type", EnumSpeedLimitType.class);

    // 板状の当たり判定 (必要に応じて調整してください)
    private static final VoxelShape SHAPE = Block.createCuboidShape(7.5, 0, 7.5, 8.5, 16, 8.5);

    public BlockSpeedLimitSign(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, EnumSpeedLimitType.NORMAL));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntitySpeedLimitSign(pos, state);
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
        builder.add(FACING, TYPE);
    }
}