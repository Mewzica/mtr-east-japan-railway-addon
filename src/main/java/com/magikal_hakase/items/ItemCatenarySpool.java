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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCatenarySpool extends Item {

    public ItemCatenarySpool(Settings settings) {
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

        // クリックしたブロックが「繋げる対象（碍子）」かどうか確認
        boolean isCatenary = be instanceof TileEntityCatenaryInsulator;
        boolean isCommunication = be instanceof TileEntityCommunicationInsulator;

        // どちらの碍子でもなければ何もしない
        if (!isCatenary && !isCommunication) return ActionResult.PASS;

        NbtCompound tag = stack.getOrCreateNbt();

        // --- 1. 開始点が保存されている場合（2回目のクリック） ---
        if (tag.contains("StartPos")) {
            BlockPos startPos = NbtHelper.toBlockPos(tag.getCompound("StartPos"));

            // 同じ場所ならキャンセル
            if (startPos.equals(pos)) {
                tag.remove("StartPos");
                player.sendMessage(Text.literal("Selection cleared"), true);
                return ActionResult.SUCCESS;
            }

            // 距離制限
            if (startPos.getManhattanDistance(pos) > 64) {
                player.sendMessage(Text.literal("Too far!"), true);
                return ActionResult.FAIL;
            }

            BlockEntity startBe = world.getBlockEntity(startPos);

            // ★重要: 種類の一致チェック
            if (startBe instanceof TileEntityCatenaryInsulator && isCatenary) {
                // 架線碍子同士
                ((TileEntityCatenaryInsulator) startBe).addConnection(pos);
                ((TileEntityCatenaryInsulator) be).addConnection(startPos);
                player.sendMessage(Text.literal("Catenary Connected!"), true);
            }
            else if (startBe instanceof TileEntityCommunicationInsulator && isCommunication) {
                // 通信線碍子同士
                ((TileEntityCommunicationInsulator) startBe).addConnection(pos);
                ((TileEntityCommunicationInsulator) be).addConnection(startPos);
                player.sendMessage(Text.literal("Wire Connected!"), true);
            }
            else {
                // 種類が違う場合
                player.sendMessage(Text.literal("Cannot connect different insulator types!"), true);
                return ActionResult.FAIL;
            }

            // 接続完了したら選択解除
            tag.remove("StartPos");

        } else {
            // --- 2. 開始点を保存（1回目のクリック） ---
            tag.put("StartPos", NbtHelper.fromBlockPos(pos));
            player.sendMessage(Text.literal("Position set. Click another insulator of the same type."), true);
        }

        return ActionResult.SUCCESS;
    }
}