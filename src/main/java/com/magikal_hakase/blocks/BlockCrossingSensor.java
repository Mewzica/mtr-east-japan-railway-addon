package com.magikal_hakase.blocks;

import com.magikal_hakase.blockentities.TileEntityCrossingSensor;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockCrossingSensor extends Block implements BlockEntityProvider {

    public static final BooleanProperty POWERED = Properties.POWERED;
    // true = 進入(Enter), false = 退出(Exit)
    public static final BooleanProperty IS_ENTER = BooleanProperty.of("is_enter");

    public BlockCrossingSensor(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(POWERED, false).with(IS_ENTER, true));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityCrossingSensor(pos, state);
    }

    // 右クリックでモード切り替え
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && player.getStackInHand(hand).isEmpty()) {
            boolean isEnter = !state.get(IS_ENTER);
            world.setBlockState(pos, state.with(IS_ENTER, isEnter), 3);

            String modeName = isEnter ? "§c[進入 (ON)]" : "§9[退出 (OFF)]";
            player.sendMessage(Text.literal("センサーモードを " + modeName + " に切り替えました。"), true);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    // ディテクターレール等からの信号を検知
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean isPowered = world.isReceivingRedstonePower(pos);
            if (state.get(POWERED) != isPowered) {
                world.setBlockState(pos, state.with(POWERED, isPowered), 3);

                // 信号がONになった瞬間だけ、制御盤に通信する
                if (isPowered) {
                    if (world.getBlockEntity(pos) instanceof TileEntityCrossingSensor sensor) {
                        sensor.triggerLinkedPanel();
                    }
                }
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, IS_ENTER);
    }
}