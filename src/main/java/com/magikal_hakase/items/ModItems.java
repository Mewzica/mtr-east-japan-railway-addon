package com.magikal_hakase.items;

import com.magikal_hakase.ModItemGroup; // ModBlocksで使用しているタブ設定クラス
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final String MOD_ID = "mtr-east-japan-railway-addon";
    
    public static final Item CATENARY_SPOOL = registerItem("catenary_spool",
            new ItemCatenarySpool(new FabricItemSettings()
                    .group(ModItemGroup.JR_ADDON_GROUP)
                    .maxCount(1)));

    public static final Item CATENARY_REMOVER = registerItem("catenary_remover",
            new ItemCatenaryRemover(new FabricItemSettings()
                    .group(ModItemGroup.JR_ADDON_GROUP)
                    .maxCount(1)));

    public static final Item CONTROL_TERMINAL = registerItem("control_terminal",
            new ItemControlTerminal(new FabricItemSettings()
                    .group(ModItemGroup.JR_ADDON_GROUP)
                    .maxCount(1)));

    public static final Item CROSSING_SIGN = registerItem("crossing_sign",
            new Item(new FabricItemSettings()
                    .group(ModItemGroup.JR_ADDON_GROUP)
                    .maxCount(64)));

    public static final Item CROSSING_LIGHT_H = registerItem("crossing_light_h",
            new Item(new FabricItemSettings()
                    .group(ModItemGroup.JR_ADDON_GROUP)
                    .maxCount(64)));

    public static final Item CROSSING_LIGHT_V = registerItem("crossing_light_v",
            new Item(new FabricItemSettings()
                    .group(ModItemGroup.JR_ADDON_GROUP)
                    .maxCount(64)));

    public static final Item CROSSING_SPEAKER = registerItem("crossing_speaker",
            new Item(new FabricItemSettings()
                    .group(ModItemGroup.JR_ADDON_GROUP)
                    .maxCount(64)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), item);
    }

    // メインクラスから呼び出すための初期化メソッド
    public static void registerModItems() {
        System.out.println("Registering ModItems for " + MOD_ID);
    }
}