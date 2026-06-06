package com.magikal_hakase;

import com.magikal_hakase.blockentities.ModBlockEntities;
import com.magikal_hakase.blocks.ModBlocks;
import com.magikal_hakase.data.ModSounds;
import com.magikal_hakase.entity.ModEntities;
import com.magikal_hakase.items.ModItems;
import com.magikal_hakase.network.ModPackets;
import com.magikal_hakase.packet.PacketHandler;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MTREastJapanRailwayAddon implements ModInitializer {
	public static final String MOD_ID = "mtr-east-japan-railway-addon";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
        ModBlocks.registerModBlocks();

        ModItems.registerModItems();

        ModBlockEntities.registerBlockEntities();

        ModPackets.registerC2SPackets();

        PacketHandler.registerServer();

        ModSounds.register();

        ModEntities.register();

		LOGGER.info("Hello Fabric world!");
	}
}