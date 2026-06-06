package com.magikal_hakase;

import com.magikal_hakase.blocks.ModBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModItemGroup {

    public static final ItemGroup JR_ADDON_GROUP = FabricItemGroupBuilder.build(
            new Identifier(ModBlocks.MOD_ID, "general"),
            () -> new ItemStack(ModBlocks.EAST_JAPAN_TICKET_IC_ONLY_BARRIER_ENTRANCE)); // タブのアイコン
}