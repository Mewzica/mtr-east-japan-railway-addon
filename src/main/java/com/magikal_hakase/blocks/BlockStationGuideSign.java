package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityStationGuideSign;
import com.magikal_hakase.data.GuideSignType;
import mtr.block.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
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

public class BlockStationGuideSign extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final EnumProperty<GuideSignType> TYPE = EnumProperty.of("type", GuideSignType.class);

    public BlockStationGuideSign(Settings settings) {
        super(settings);
        // デフォルト: 北向き、単体
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, GuideSignType.SINGLE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    // ブロックエンティティの生成
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityStationGuideSign(pos, state);
    }

    // 右クリック時の動作
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // サーバー側は何もしない（クライアント側でGUIを開くため）
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            // ここで本当は openScreen 等を呼びたいが、
            // MTR/Fabricの仕様上、クライアント側でScreenを開くのが手っ取り早い場合がある。
            // しかし正しくは BlockEntityProvider なので、以下のようにします。
            return ActionResult.CONSUME;
        }
    }

    // 設置時の状態決定
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = this.getDefaultState().with(FACING, ctx.getPlayerFacing());
        return state.with(TYPE, getConnectionType(state, ctx.getWorld(), ctx.getBlockPos()));
    }

    // 隣のブロックが更新された時の状態更新 (連結処理)
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.with(TYPE, getConnectionType(state, world, pos));
    }

    // 連結タイプを判定するロジック
    private GuideSignType getConnectionType(BlockState state, WorldAccess world, BlockPos pos) {
        Direction facing = state.get(FACING);
        // ブロックの正面から見て「左」と「右」の方角を取得
        Direction leftDir = facing.rotateYCounterclockwise();  // 左
        Direction rightDir = facing.rotateYClockwise();        // 右

        // 左右のブロックを確認
        BlockState leftState = world.getBlockState(pos.offset(leftDir));
        BlockState rightState = world.getBlockState(pos.offset(rightDir));

        // 「同じブロック」かつ「同じ向き」なら繋がる
        boolean connectLeft = leftState.isOf(this) && leftState.get(FACING) == facing;
        boolean connectRight = rightState.isOf(this) && rightState.get(FACING) == facing;

        if (connectLeft && connectRight) {
            return GuideSignType.MIDDLE;
        } else if (connectLeft) {
            return GuideSignType.RIGHT; // 左と繋がっている＝自分は右端
        } else if (connectRight) {
            return GuideSignType.LEFT;  // 右と繋がっている＝自分は左端
        } else {
            return GuideSignType.SINGLE;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // 薄い板の形状 (モデルに合わせて調整してください)
        return IBlock.getVoxelShapeByDirection(0, 0, 7, 16, 16, 9, state.get(FACING));
    }
}