package com.magikal_hakase.items;

import com.magikal_hakase.blockentities.TileEntityCatenaryInsulator;
import com.magikal_hakase.blockentities.TileEntityCommunicationInsulator;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCatenaryRemover extends Item {

    public ItemCatenaryRemover(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(pos);

        // クリックしたブロックが「碍子（架線・通信線・ブラケット等）」かチェック
        boolean isCatenary = be instanceof TileEntityCatenaryInsulator;
        boolean isCommunication = be instanceof TileEntityCommunicationInsulator;

        if (!isCatenary && !isCommunication) return ActionResult.PASS;

        NbtCompound tag = stack.getOrCreateNbt();

        // --- 2回目のクリック（削除実行） ---
        if (tag.contains("StartPos")) {
            BlockPos startPos = NbtHelper.toBlockPos(tag.getCompound("StartPos"));

            // 同じ場所をクリックしたらキャンセル
            if (startPos.equals(pos)) {
                tag.remove("StartPos");
                player.sendMessage(Text.literal("Selection cleared").formatted(Formatting.YELLOW), true);
                return ActionResult.SUCCESS;
            }

            BlockEntity startBe = world.getBlockEntity(startPos);

            // 接続解除処理
            boolean removed = false;

            // ケース1: 架線碍子 (CatenaryInsulator / MovableBracket)
            if (startBe instanceof TileEntityCatenaryInsulator && isCatenary) {
                ((TileEntityCatenaryInsulator) startBe).removeConnection(pos);
                ((TileEntityCatenaryInsulator) be).removeConnection(startPos);
                removed = true;
            }
            // ケース2: 通信線碍子 (CommunicationInsulator / Structure)
            else if (startBe instanceof TileEntityCommunicationInsulator && isCommunication) {
                ((TileEntityCommunicationInsulator) startBe).removeConnection(pos);
                ((TileEntityCommunicationInsulator) be).removeConnection(startPos);
                removed = true;
            }

            if (removed) {
                player.sendMessage(Text.literal("Wire connection removed!").formatted(Formatting.RED), true);
            } else {
                player.sendMessage(Text.literal("Cannot remove connection (Type mismatch or not connected).").formatted(Formatting.GRAY), true);
            }

            // 選択解除
            tag.remove("StartPos");

        } else {
            // --- 1回目のクリック（開始点選択） ---
            tag.put("StartPos", NbtHelper.fromBlockPos(pos));
            player.sendMessage(Text.literal("Select another insulator to remove the wire.").formatted(Formatting.GOLD), true);
        }

        return ActionResult.SUCCESS;
    }
}