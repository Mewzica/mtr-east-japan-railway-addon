package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityCrossingControlPanel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockCrossingControlPanel extends Block implements BlockEntityProvider {

    public static final BooleanProperty POWERED = Properties.POWERED;

    public BlockCrossingControlPanel(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityCrossingControlPanel(pos, state);
    }

    // ★重要: このブロックはレッドストーン動力を出力する
    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        // POWERED が true なら最大動力(15)を出力
        return state.get(POWERED) ? 15 : 0;
    }

    // スニーク(しゃがみ)＋右クリックでカウントを強制リセットする便利機能
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && player.isSneaking()) {
            if (world.getBlockEntity(pos) instanceof TileEntityCrossingControlPanel panel) {
                panel.resetCount();
                player.sendMessage(Text.literal("§e[制御盤] 列車カウントを強制リセット(0)しました。"), true);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }
}