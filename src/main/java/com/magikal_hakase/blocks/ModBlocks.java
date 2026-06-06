package com.magikal_hakase.blocks;

import com.magikal_hakase.ModItemGroup;
import mtr.block.BlockPIDSPole;
import mtr.block.BlockPlatform;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {

    // MOD IDを定数として保持しておくと便利です
    public static final String MOD_ID = "mtr-east-japan-railway-addon";

    // ブロックの定義 (硬さは石と同程度、要ツール)
    public static final Block EAST_JAPAN_TICKET_BARRIER_ENTRANCE = registerBlock("east_japan_ticket_barrier_entrance",
            new BlockEastJapanTicketBarrier(true));

    public static final Block EAST_JAPAN_TICKET_BARRIER_EXIT = registerBlock("east_japan_ticket_barrier_exit",
            new BlockEastJapanTicketBarrier(false));

    public static final Block EAST_JAPAN_TICKET_IC_ONLY_BARRIER_ENTRANCE = registerBlock("east_japan_ticket_barrier_ic_only_entrance",
            new BlockEastJapanTicketBarrier(true));

    public static final Block EAST_JAPAN_TICKET_IC_ONLY_BARRIER_EXIT = registerBlock("east_japan_ticket_barrier_ic_only_exit",
            new BlockEastJapanTicketBarrier(false));

    public static final Block EAST_JAPAN_TICKET_PROCESSOR = registerBlock("east_japan_ticket_processor",
            new BlockEastJapanTicketProcessor(true, true, true));

    public static final Block EAST_JAPAN_TICKET_PROCESSOR_ENTRANCE = registerBlock("east_japan_ticket_processor_entrance",
            new BlockEastJapanTicketProcessor(true, true, false));

    public static final Block EAST_JAPAN_TICKET_PROCESSOR_EXIT = registerBlock("east_japan_ticket_processor_exit",
            new BlockEastJapanTicketProcessor(true, false, true));

    public static final Block EAST_JAPAN_TICKET_MACHINE = registerBlock("east_japan_ticket_machine",
            new BlockEastJapanTicketMachine(FabricBlockSettings.of(Material.METAL).strength(2.0f, 6.0f)));

    public static final Block EAST_JAPAN_OVERCHARGE_MACHINE = registerBlock("east_japan_overcharge_machine",
            new BlockEastJapanTicketMachine(FabricBlockSettings.of(Material.METAL).strength(2.0f, 6.0f)));

    public static final Block SHINJUKU_PLATFORM = registerBlock("shinjuku_platform",
            new BlockPlatform(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f), true));

    public static final Block OLD_PLATFORM = registerBlock("old_platform",
            new BlockPlatform(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f), true));

    public static final Block BRIGHT_TILE = registerBlock("bright_tile",
            new Block(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()));

    public static final Block DARK_TILE = registerBlock("dark_tile",
            new Block(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()));

    public static final Block ALUMINUM_PANEL = registerBlock("aluminum_panel",
            new Block(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()));

    public static final Block FLUORESCENT_LIGHT = registerBlock("fluorescent_light",
            new BlockFluorescentLight(FabricBlockSettings.of(Material.METAL).strength(0.3f).luminance(15).nonOpaque()));

    public static final Block HANGING_BRACKET = registerBlock("hanging_bracket",
            new BlockHangingBracket(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block RACEWAY = registerBlock("raceway",
            new BlockRaceway(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block CABLE_RACK = registerBlock("cable_rack",
            new BlockCableRack(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block GUIDANCE_BELL = registerBlock("guidance_bell",
            new BlockGuidanceBell(FabricBlockSettings.of(Material.METAL).strength(2.0f, 6.0f).requiresTool()));

    public static final Block STATION_BENCH = registerBlock("station_bench",
            new BlockStationBench(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()));

    public static final Block STATION_CLOCK = registerBlock("station_clock",
            new BlockStationClock(FabricBlockSettings.of(Material.METAL).strength(2.0f, 6.0f).requiresTool()));

    public static final Block EAST_JAPAN_PIDS = registerBlock("east_japan_pids",
            new BlockEastJapanPIDS(FabricBlockSettings.of(Material.METAL).strength(2.25f, 6.0f).requiresTool()));

    public static final Block EAST_JAPAN_PIDS_POLE = registerBlock("east_japan_pids_pole",
            new BlockPIDSPole(FabricBlockSettings.of(Material.METAL).strength(2.0f, 6.0f).requiresTool()));

    public static final Block STATION_GUIDE_SIGN = registerBlock("station_guide_sign",
            new BlockStationGuideSign(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()));

    public static final Block STATION_GUIDE_SIGN_POLE = registerBlock("station_guide_sign_pole",
            new BlockStationGuideSignPole(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).requiresTool()));

    private static final FabricBlockSettings BRAILLE_SETTINGS = FabricBlockSettings.of(Material.STONE)
            .strength(1.5f, 6.0f)
            .requiresTool()
            .nonOpaque();

    public static final Block GUIDANCE_BLOCK = registerBlock("guidance_block",
            new BrailleBlock(BRAILLE_SETTINGS));

    public static final Block CAUTION_BLOCK = registerBlock("caution_block",
            new BrailleBlock(BRAILLE_SETTINGS));

    public static final Block CAUTION_BLOCK_ROW = registerBlock("caution_block_row",
            new BrailleBlock(BRAILLE_SETTINGS));

    public static final Block CAUTION_BLOCK_WITH_GUIDANCE_BLOCK = registerBlock("caution_block_with_guidance_block",
            new BrailleBlock(BRAILLE_SETTINGS));

    public static final Block CAUTION_BLOCK_ENCLOSEDLINE = registerBlock("caution_block_enclosedline",
            new BrailleBlock(BRAILLE_SETTINGS));

    public static final Block ROUTE_MARKER_YELLOW = registerBlock("route_marker_yellow",
            new BrailleBlock(BRAILLE_SETTINGS));

    public static final Block ROUTE_MARKER_WHITE = registerBlock("route_marker_white",
            new BrailleBlock(BRAILLE_SETTINGS));

    public static final Block JR_EAST_VERTICAL_SIGN_BLOCK = registerBlock("vertical_station_name_sign",
            new BlockJREastVerticalSign(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block HANGING_STATION_SIGN = registerBlock("hanging_station_sign",
            new BlockHangingStationSign(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block HANGING_STATION_SIGN_POLE = registerBlock("hanging_station_sign_pole",
            new BlockHangingStationSignPole(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block JNR_STATION_SIGN = registerBlock("jnr_station_sign",
            new BlockJNRStationSign(FabricBlockSettings.of(Material.WOOD).strength(1.0f).nonOpaque()));

    public static final Block SIGNAL_POLE = registerBlock("signal_pole",
            new BlockSignalPole(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque())
    );

    public static final Block EMERGENCY_BUTTON = registerBlock("emergency_stop_button",
            new BlockEmergencyStopButton(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block EMERGENCY_ALARM = registerBlock("emergency_stop_alarm",
            new BlockEmergencyStopAlarm(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block SIGNAL_PLATE = registerBlock("signal_plate",
            new BlockSignalPlate(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block TWO_LIGHT_SIGNAL = registerBlock("two_light_signal",
            new BlockTwoLightSignal(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block THREE_LIGHT_SIGNAL = registerBlock("three_light_signal",
            new BlockThreeLightSignal(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block FOUR_LIGHT_SIGNAL = registerBlock("four_light_signal",
            new BlockFourLightSignal(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block SWITCHING_SIGNAL = registerBlock("switching_signal",
            new BlockSwitchingSignal(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block STOPPING_POSITION_SIGN = registerBlock("stopping_position_sign",
            new BlockStoppingPositionSign(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block SPEED_LIMIT_SIGN = registerBlock("speed_limit_sign",
            new BlockSpeedLimitSign(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).nonOpaque()));

    public static final Block RESTRICTION_REMOVAL_SIGN = registerBlock("restriction_removal_sign",
            new BlockRestrictionRemovalSign(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).nonOpaque()));

    public static final Block GRADIENT_MARKER = registerBlock("gradient_marker",
            new BlockGradientMarker(FabricBlockSettings.of(Material.STONE).strength(1.5f, 6.0f).nonOpaque()));

    public static final Block CONCRETE_CATENARY_POLE = registerBlock("concrete_catenary_pole",
            new BlockConcreteCatenaryPole(FabricBlockSettings.of(Material.STONE).strength(2.0f).nonOpaque())
    );

    public static final Block TRUSS_CATENARY_POLE = registerBlock("truss_catenary_pole",
            new BlockTrussCatenaryPole(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque())
    );

    public static final Block STEEL_PIPE_CATENARY_POLE = registerBlock("steel_pipe_catenary_pole",
            new BlockSteelPipeCatenaryPole(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque())
    );

    public static final Block MOVABLE_BRACKET = registerBlock("movable_bracket",
            new BlockMovableBracket(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block CATENARY_INSULATOR = registerBlock("catenary_insulator",
            new BlockCatenaryInsulator(FabricBlockSettings.of(Material.METAL).strength(1.0f).nonOpaque())
    );

    public static final Block COMMUNICATION_INSULATOR = registerBlock("communication_insulator",
            new BlockCommunicationInsulator(FabricBlockSettings.of(Material.METAL).strength(1.0f).nonOpaque())
    );

    public static final Block CATENARY_STRUCTURE = registerBlock("catenary_structure",
            new BlockCatenaryStructure(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque())
    );

    public static final Block STEEL_PIPE_CATENARY_STRUCTURE = registerBlock("steel_pipe_catenary_structure",
            new BlockSteelPipeCatenaryStructure(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block CROSSING_SIGNMARK = registerBlock("crossing_signmark",
            new BlockCrossingSignmark(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block CROSSING_POLE = registerBlock("crossing_pole",
            new BlockCrossingPole(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block CROSSING_GATE = registerBlock("crossing_gate",
            new BlockCrossingGate(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block CROSSING_FENCE = registerBlock("crossing_fence",
            new BlockCrossingFence(FabricBlockSettings.of(Material.STONE).strength(1.5f).nonOpaque()));

    public static final Block CROSSING_CONTROL_PANEL = registerBlock("crossing_control_panel",
            new BlockCrossingControlPanel(FabricBlockSettings.of(Material.METAL).strength(2.0f).nonOpaque()));

    public static final Block CROSSING_SENSOR = registerBlock("crossing_sensor",
            new BlockCrossingSensor(FabricBlockSettings.of(Material.STONE).strength(1.5f).nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(ModItemGroup.JR_ADDON_GROUP)));
    }

    // 初期化用メソッド
    public static void registerModBlocks() {
        System.out.println("Registering ModBlocks for " + MOD_ID);
    }
}