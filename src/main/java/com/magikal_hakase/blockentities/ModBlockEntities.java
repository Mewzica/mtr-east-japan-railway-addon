package com.magikal_hakase.blockentities;

import com.magikal_hakase.blocks.BlockJREastVerticalSign;
import com.magikal_hakase.blocks.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities {
    public static BlockEntityType<BlockJREastVerticalSign.TileEntityJREastVerticalSign> JR_EAST_VERTICAL_SIGN;
    public static BlockEntityType<TileEntityHangingStationSign> HANGING_STATION_SIGN;
    public static BlockEntityType<TileEntityJNRStationSign> JNR_STATION_SIGN;
    public static BlockEntityType<TileEntitySignalPlate> SIGNAL_PLATE;
    public static BlockEntityType<TileEntityTwoLightSignal> TWO_LIGHT_SIGNAL;
    public static BlockEntityType<TileEntityThreeLightSignal> THREE_LIGHT_SIGNAL;
    public static BlockEntityType<TileEntityFourLightSignal> FOUR_LIGHT_SIGNAL;
    public static BlockEntityType<TileEntitySwitchingSignal> SWITCHING_SIGNAL;
    public static BlockEntityType<TileEntityGuidanceBell> GUIDANCE_BELL;
    public static BlockEntityType<TileEntityStationClock> STATION_CLOCK;
    public static BlockEntityType<TileEntityEastJapanPIDS> EAST_JAPAN_PIDS;
    public static BlockEntityType<TileEntityStationGuideSign> STATION_GUIDE_SIGN;
    public static BlockEntityType<TileEntityStoppingPositionSign> STOPPING_POSITION_SIGN;
    public static BlockEntityType<TileEntitySpeedLimitSign> SPEED_LIMIT_SIGN;
    public static BlockEntityType<TileEntityGradientMarker> GRADIENT_MARKER;
    public static BlockEntityType<TileEntityMovableBracket> MOVABLE_BRACKET;
    public static BlockEntityType<TileEntityCatenaryInsulator> CATENARY_INSULATOR;
    public static BlockEntityType<TileEntityCommunicationInsulator> COMMUNICATION_INSULATOR;
    public static BlockEntityType<TileEntityCrossingSignmark> CROSSING_SIGNMARK;
    public static BlockEntityType<TileEntityCrossingPole> CROSSING_POLE;
    public static BlockEntityType<TileEntityCrossingGate> CROSSING_GATE;
    public static BlockEntityType<TileEntityCrossingControlPanel> CROSSING_CONTROL_PANEL;
    public static BlockEntityType<TileEntityCrossingSensor> CROSSING_SENSOR;

    public static void registerBlockEntities() {
        JR_EAST_VERTICAL_SIGN = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "jr_east_vertical_sign"),
                FabricBlockEntityTypeBuilder.create(
                        BlockJREastVerticalSign.TileEntityJREastVerticalSign::new,
                        ModBlocks.JR_EAST_VERTICAL_SIGN_BLOCK
                ).build()
        );

        COMMUNICATION_INSULATOR = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("mtr-east-japan-railway-addon", "communication_insulator"),
                FabricBlockEntityTypeBuilder.create(TileEntityCommunicationInsulator::new,
                        ModBlocks.COMMUNICATION_INSULATOR,
                        ModBlocks.CATENARY_STRUCTURE,
                        ModBlocks.STEEL_PIPE_CATENARY_STRUCTURE
                ).build(null)
        );

        HANGING_STATION_SIGN = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "hanging_station_sign"),
                FabricBlockEntityTypeBuilder.create(TileEntityHangingStationSign::new, ModBlocks.HANGING_STATION_SIGN).build()
        );

        JNR_STATION_SIGN = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "jnr_station_sign"),
                FabricBlockEntityTypeBuilder.create(TileEntityJNRStationSign::new, ModBlocks.JNR_STATION_SIGN).build()
        );

        SIGNAL_PLATE = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "signal_plate"),
                FabricBlockEntityTypeBuilder.create(TileEntitySignalPlate::new, ModBlocks.SIGNAL_PLATE).build()
        );

        TWO_LIGHT_SIGNAL = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "two_light_signal"),
                FabricBlockEntityTypeBuilder.create(TileEntityTwoLightSignal::new, ModBlocks.TWO_LIGHT_SIGNAL).build()
        );

        THREE_LIGHT_SIGNAL = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "three_light_signal"),
                FabricBlockEntityTypeBuilder.create(TileEntityThreeLightSignal::new, ModBlocks.THREE_LIGHT_SIGNAL).build()
        );

        FOUR_LIGHT_SIGNAL = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "four_light_signal"),
                FabricBlockEntityTypeBuilder.create(TileEntityFourLightSignal::new, ModBlocks.FOUR_LIGHT_SIGNAL).build()
        );

        SWITCHING_SIGNAL = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "switching_signal"),
                FabricBlockEntityTypeBuilder.create(TileEntitySwitchingSignal::new, ModBlocks.SWITCHING_SIGNAL).build()
        );

        GUIDANCE_BELL = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "guidance_bell"),
                FabricBlockEntityTypeBuilder.create(TileEntityGuidanceBell::new, ModBlocks.GUIDANCE_BELL).build()
        );

        STATION_CLOCK = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "station_clock"),
                FabricBlockEntityTypeBuilder.create(TileEntityStationClock::new, ModBlocks.STATION_CLOCK).build()
        );

        EAST_JAPAN_PIDS = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "east_japan_pids"),
                FabricBlockEntityTypeBuilder.create(TileEntityEastJapanPIDS::new, ModBlocks.EAST_JAPAN_PIDS).build()
        );

        STATION_GUIDE_SIGN = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "station_guide_sign"),
                FabricBlockEntityTypeBuilder.create(TileEntityStationGuideSign::new, ModBlocks.STATION_GUIDE_SIGN).build()
        );

        STOPPING_POSITION_SIGN = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "stopping_position_sign"),
                FabricBlockEntityTypeBuilder.create(TileEntityStoppingPositionSign::new, ModBlocks.STOPPING_POSITION_SIGN).build()
        );

        SPEED_LIMIT_SIGN = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "speed_limit_sign"),
                FabricBlockEntityTypeBuilder.create(TileEntitySpeedLimitSign::new, ModBlocks.SPEED_LIMIT_SIGN).build()
        );

        GRADIENT_MARKER = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "gradient_marker"),
                FabricBlockEntityTypeBuilder.create(TileEntityGradientMarker::new, ModBlocks.GRADIENT_MARKER).build()
        );
        CATENARY_INSULATOR = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "catenary_insulator"),
                FabricBlockEntityTypeBuilder.create(TileEntityCatenaryInsulator::new, ModBlocks.CATENARY_INSULATOR).build()
        );
        MOVABLE_BRACKET = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "movable_bracket"),
                FabricBlockEntityTypeBuilder.create(TileEntityMovableBracket::new, ModBlocks.MOVABLE_BRACKET).build(null)
        );
        CROSSING_SIGNMARK = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "crossing_signmark"),
                FabricBlockEntityTypeBuilder.create(TileEntityCrossingSignmark::new, ModBlocks.CROSSING_SIGNMARK).build(null)
        );
        CROSSING_POLE = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "crossing_pole"),
                FabricBlockEntityTypeBuilder.create(TileEntityCrossingPole::new, ModBlocks.CROSSING_POLE).build(null)
        );
        CROSSING_GATE = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "crossing_gate"),
                FabricBlockEntityTypeBuilder.create(TileEntityCrossingGate::new, ModBlocks.CROSSING_GATE).build(null)
        );
        CROSSING_CONTROL_PANEL = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "crossing_control_panel"),
                FabricBlockEntityTypeBuilder.create(TileEntityCrossingControlPanel::new, ModBlocks.CROSSING_GATE).build(null)
        );
        CROSSING_SENSOR = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ModBlocks.MOD_ID, "crossing_sensor"),
                FabricBlockEntityTypeBuilder.create(TileEntityCrossingSensor::new, ModBlocks.CROSSING_SENSOR).build(null)
        );
    }
}