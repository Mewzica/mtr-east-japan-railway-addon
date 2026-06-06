package com.magikal_hakase.blocks;

import com.magikal_hakase.data.GuideSignType;
import mtr.block.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class BlockStationGuideSignPole extends HorizontalFacingBlock {

    // 案内サインと同じ Enum を使います
    public static final EnumProperty<GuideSignType> TYPE = EnumProperty.of("type", GuideSignType.class);

    public BlockStationGuideSignPole(Settings settings) {
        super(settings);
        // デフォルトは SINGLE (中央支柱)
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, GuideSignType.SINGLE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    // 設置時に下の状態を確認
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = this.getDefaultState().with(FACING, ctx.getPlayerFacing());
        return state.with(TYPE, getTypeFromBelow(ctx.getWorld(), ctx.getBlockPos(), state.get(FACING)));
    }

    // 隣接ブロック更新時に下の状態を再確認
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // 下のブロックが変わった場合のみ再計算
        if (direction == Direction.DOWN) {
            return state.with(TYPE, getTypeFromBelow(world, pos, state.get(FACING)));
        }
        return state;
    }

    // ★核心部分: 下のブロックからタイプを取得するロジック
    private GuideSignType getTypeFromBelow(WorldAccess world, BlockPos pos, Direction myFacing) {
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);
        Block belowBlock = belowState.getBlock();

        // 1. 下が「案内サイン本体」の場合
        if (belowBlock instanceof BlockStationGuideSign) {
            // 向きが合っている場合のみ連動
            if (belowState.get(FACING) == myFacing) {
                return belowState.get(BlockStationGuideSign.TYPE);
            }
        }
        // 2. 下が「案内サインの支柱 (このブロック)」の場合 (上に伸ばすケース)
        else if (belowBlock instanceof BlockStationGuideSignPole) {
            if (belowState.get(FACING) == myFacing) {
                return belowState.get(TYPE);
            }
        }

        // 下が関係ないブロックならデフォルト(SINGLE=中央)にする
        return GuideSignType.SINGLE;
    }

    // 当たり判定 (細い棒状にする)
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        GuideSignType type = state.get(TYPE);
        Direction facing = state.get(FACING);

        // モデルに合わせて調整が必要ですが、とりあえず大まかな形状を返します
        // 中央支柱 (SINGLE / MIDDLE)
        VoxelShape shape = IBlock.getVoxelShapeByDirection(7, 0, 7, 9, 16, 9, facing);

        if (type == GuideSignType.LEFT) {
            // 左寄りの支柱 (モデル座標に合わせて調整してください)
            // 例: 左に6ドットずらす
            shape = IBlock.getVoxelShapeByDirection(0, 0, 7, 2, 16, 9, facing);
        } else if (type == GuideSignType.RIGHT) {
            // 右寄りの支柱
            shape = IBlock.getVoxelShapeByDirection(14, 0, 7, 16, 16, 9, facing);
        }

        return shape;
    }
}