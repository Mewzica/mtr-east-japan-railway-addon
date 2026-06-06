package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityCatenaryInsulator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock; // ★変更
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty; // ★追加
import net.minecraft.state.property.Properties;       // ★追加
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;             // ★追加
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

// HorizontalFacingBlock を継承するように変更
public class BlockCatenaryInsulator extends HorizontalFacingBlock implements BlockEntityProvider {

    // ★向きプロパティの定義
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // 当たり判定 (6～10なので中央4ドット幅)
    private static final VoxelShape SHAPE = Block.createCuboidShape(6, 0, 6, 10, 6, 10);

    public BlockCatenaryInsulator(Settings settings) {
        super(settings);
        // ★デフォルトの向きを北に設定
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // 回転させても中央の判定が変わらない場合はこのままでOK
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityCatenaryInsulator(pos, state);
    }

    // ★設置時にプレイヤーの反対方向を向くように設定
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    // ★プロパティの登録
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof TileEntityCatenaryInsulator insulator) {
                // 繋がっている相手側の碍子からも、自分への接続を削除する
                for (BlockPos targetPos : insulator.connections) {
                    BlockEntity targetBe = world.getBlockEntity(targetPos);
                    if (targetBe instanceof TileEntityCatenaryInsulator targetInsulator) {
                        targetInsulator.removeConnection(pos);
                    }
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }
}