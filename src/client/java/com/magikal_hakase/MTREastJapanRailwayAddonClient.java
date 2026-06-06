package com.magikal_hakase;

import com.magikal_hakase.blockentities.*;
import com.magikal_hakase.blocks.ModBlocks;
import com.magikal_hakase.entity.ModEntities;
import com.magikal_hakase.gui.*;
import com.magikal_hakase.renderer.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class MTREastJapanRailwayAddonClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // レンダラー登録 (既存のまま)
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GUIDANCE_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CAUTION_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CAUTION_BLOCK_ROW, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CAUTION_BLOCK_WITH_GUIDANCE_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CAUTION_BLOCK_ENCLOSEDLINE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROUTE_MARKER_YELLOW, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROUTE_MARKER_WHITE, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.JR_EAST_VERTICAL_SIGN, RenderJREastVerticalSign::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.JR_EAST_VERTICAL_SIGN_BLOCK, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.HANGING_STATION_SIGN, RenderHangingStationSign::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HANGING_STATION_SIGN, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.SIGNAL_PLATE, RenderSignalPlate::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGNAL_PLATE, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.TWO_LIGHT_SIGNAL, RenderTwoLightSignal::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TWO_LIGHT_SIGNAL, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.THREE_LIGHT_SIGNAL, RenderThreeLightSignal::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.THREE_LIGHT_SIGNAL, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.FOUR_LIGHT_SIGNAL, RenderFourLightSignal::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FOUR_LIGHT_SIGNAL, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.SWITCHING_SIGNAL, RenderSwitchingSignal::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SWITCHING_SIGNAL, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.STATION_CLOCK, RenderStationClock::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STATION_CLOCK, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.EAST_JAPAN_PIDS, RenderEastJapanPIDS::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EAST_JAPAN_PIDS, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.STATION_GUIDE_SIGN, RenderStationGuideSign::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STATION_GUIDE_SIGN, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STATION_GUIDE_SIGN_POLE, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.STOPPING_POSITION_SIGN, RenderStoppingPositionSign::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STOPPING_POSITION_SIGN, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.SPEED_LIMIT_SIGN, RenderSpeedLimitSign::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPEED_LIMIT_SIGN, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.GRADIENT_MARKER, RenderGradientMarker::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GRADIENT_MARKER, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CONCRETE_CATENARY_POLE, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TRUSS_CATENARY_POLE, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STEEL_PIPE_CATENARY_POLE, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.CATENARY_INSULATOR, RenderCatenaryInsulator::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CATENARY_INSULATOR, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.COMMUNICATION_INSULATOR, RenderCommunicationInsulator::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COMMUNICATION_INSULATOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CATENARY_STRUCTURE, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STEEL_PIPE_CATENARY_STRUCTURE, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.MOVABLE_BRACKET, RenderMovableBracket::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOVABLE_BRACKET, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.JNR_STATION_SIGN, RenderJNRStationSign::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.JNR_STATION_SIGN, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.CROSSING_SIGNMARK, RenderCrossingSignmark::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CROSSING_SIGNMARK, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.CROSSING_POLE, RenderCrossingPole::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CROSSING_POLE, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.CROSSING_GATE, RenderCrossingGate::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CROSSING_GATE, RenderLayer.getCutout());

        EntityRendererRegistry.register(ModEntities.SEAT_ENTITY, EmptyEntityRenderer::new);

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient) return ActionResult.PASS;
            if (hand != Hand.MAIN_HAND) return ActionResult.PASS;

            Item mtrBrush = Registry.ITEM.get(new Identifier("mtr", "brush"));
            ItemStack stack = player.getStackInHand(hand);

            if (stack.getItem() == mtrBrush) {
                BlockPos pos = hitResult.getBlockPos();
                var state = world.getBlockState(pos);
                var block = state.getBlock();

                if (block == ModBlocks.SIGNAL_PLATE) {
                    if (world.getBlockEntity(pos) instanceof TileEntitySignalPlate plate) {
                        MinecraftClient.getInstance().setScreen(new SignalPlateEditScreen(plate));
                        return ActionResult.SUCCESS;
                    }
                }
                else if (block == ModBlocks.HANGING_STATION_SIGN) {
                    if (world.getBlockEntity(pos) instanceof TileEntityHangingStationSign sign) {
                        MinecraftClient.getInstance().setScreen(new StationSignEditScreen(sign));
                        return ActionResult.SUCCESS;
                    }
                }
                else if (block == ModBlocks.GUIDANCE_BELL) {
                    if (world.getBlockEntity(pos) instanceof TileEntityGuidanceBell entity) {
                        MinecraftClient.getInstance().setScreen(new GuidanceBellEditScreen(pos, entity));
                        return ActionResult.SUCCESS;
                    }
                }
                if (block == ModBlocks.STATION_GUIDE_SIGN) {
                    if (world.getBlockEntity(pos) instanceof TileEntityStationGuideSign entity) {
                        Direction facing = state.get(HorizontalFacingBlock.FACING);
                        Direction hitSide = hitResult.getSide();

                        boolean isBack = (hitSide == facing);

                        MinecraftClient.getInstance().setScreen(new ScreenStationGuideSign(pos, entity, isBack));
                        return ActionResult.SUCCESS;
                    }
                }
                else if (block == ModBlocks.STOPPING_POSITION_SIGN) {
                    if (world.getBlockEntity(pos) instanceof TileEntityStoppingPositionSign entity) {
                        MinecraftClient.getInstance().setScreen(new ScreenStoppingPositionSign(pos, entity));
                        return ActionResult.SUCCESS;
                    }
                }
                else if (block == ModBlocks.SPEED_LIMIT_SIGN) {
                    if (world.getBlockEntity(pos) instanceof TileEntitySpeedLimitSign entity) {
                        MinecraftClient.getInstance().setScreen(new ScreenSpeedLimitSign(pos, entity));
                        return ActionResult.SUCCESS;
                    }
                }
                else if (block == ModBlocks.GRADIENT_MARKER) {
                    if (world.getBlockEntity(pos) instanceof TileEntityGradientMarker entity) {
                        MinecraftClient.getInstance().setScreen(new ScreenGradientMarker(pos, entity));
                        return ActionResult.SUCCESS;
                    }
                }
                else if (block == ModBlocks.JNR_STATION_SIGN) {
                    if (world.getBlockEntity(pos) instanceof TileEntityJNRStationSign entity) {
                        MinecraftClient.getInstance().setScreen(new ScreenJNRStationSign(pos, entity));
                        return ActionResult.SUCCESS;
                    }
                }
                else if (block == ModBlocks.CROSSING_GATE) {
                    if (world.getBlockEntity(pos) instanceof TileEntityCrossingGate entity) {
                        MinecraftClient.getInstance().setScreen(new ScreenCrossingGate(pos, entity));
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}