package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityStoppingPositionSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class BlockStoppingPositionSign extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    // ★HANGINGを追加
    public static final BooleanProperty HANGING = Properties.HANGING;

    // 当たり判定
    // 床置き用 (Y=0～12)
    private static final VoxelShape SHAPE_STANDING = Block.createCuboidShape(7.25, 0, 7.25, 8.75, 12, 8.75);
    // 天井用 (Y=4～16) ※上からぶら下がる形
    private static final VoxelShape SHAPE_HANGING = Block.createCuboidShape(7.25, 5.5, 7.25, 8.75, 16, 8.75);

    public BlockStoppingPositionSign(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HANGING, false)); // デフォルトは床置き
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // HANGINGの状態によって判定を切り替え
        return state.get(HANGING) ? SHAPE_HANGING : SHAPE_STANDING;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityStoppingPositionSign(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // クリックした面が「下面(DOWN)」なら吊り下げ(true)にする
        boolean hanging = ctx.getSide() == Direction.DOWN;

        return this.getDefaultState()
                .with(FACING, ctx.getPlayerFacing().getOpposite())
                .with(HANGING, hanging);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        // TYPEは不要なので削除し、HANGINGを追加
        builder.add(FACING, HANGING);
    }
}