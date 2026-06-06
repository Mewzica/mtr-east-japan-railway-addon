package com.magikal_hakase.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {
    public static final EntityType<SeatEntity> SEAT_ENTITY = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("mtr-east-japan-railway-addon", "seat_entity"),
            FabricEntityTypeBuilder.<SeatEntity>create(SpawnGroup.MISC, SeatEntity::new)
                    .dimensions(EntityDimensions.fixed(0.1f, 0.1f))
                    .build()
    );

    public static void register() {
        // 初期化用（中身は空でOK、クラスロードを誘発するため）
    }
}
