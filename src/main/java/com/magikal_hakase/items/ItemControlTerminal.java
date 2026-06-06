package com.magikal_hakase.items;

import com.magikal_hakase.blockentities.TileEntityCrossingSensor;
import com.magikal_hakase.blocks.BlockCrossingControlPanel;
import com.magikal_hakase.blocks.BlockCrossingSensor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemControlTerminal extends Item {

    public ItemControlTerminal(Settings settings) {
        super(settings.maxCount(1)); // スタック不可にする
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;

        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        // 1. 制御盤をクリックした場合：座標をアイテムに記憶
        if (state.getBlock() instanceof BlockCrossingControlPanel) {
            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putInt("PanelX", pos.getX());
            nbt.putInt("PanelY", pos.getY());
            nbt.putInt("PanelZ", pos.getZ());
            nbt.putBoolean("HasPanel", true);

            if (player != null) {
                player.sendMessage(Text.literal("§a[制御端末] 制御盤の座標を記録しました！ (X:" + pos.getX() + " Y:" + pos.getY() + " Z:" + pos.getZ() + ")"), true);
            }
            return ActionResult.SUCCESS;
        }

        // 2. センサーをクリックした場合：記憶した座標をセンサーに書き込む
        if (state.getBlock() instanceof BlockCrossingSensor) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.getBoolean("HasPanel")) {
                BlockPos panelPos = new BlockPos(nbt.getInt("PanelX"), nbt.getInt("PanelY"), nbt.getInt("PanelZ"));

                if (world.getBlockEntity(pos) instanceof TileEntityCrossingSensor sensor) {
                    sensor.setLinkedPanel(panelPos);
                    if (player != null) {
                        player.sendMessage(Text.literal("§b[制御端末] センサーを制御盤にリンクしました！"), true);
                    }
                }
            } else {
                if (player != null) {
                    player.sendMessage(Text.literal("§c[エラー] 先に踏切制御盤を右クリックして座標を記録してください。"), true);
                }
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}