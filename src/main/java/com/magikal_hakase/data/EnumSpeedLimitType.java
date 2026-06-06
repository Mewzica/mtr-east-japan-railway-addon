package com.magikal_hakase.data;

import net.minecraft.util.StringIdentifiable;

public enum EnumSpeedLimitType implements StringIdentifiable {
    NORMAL("normal"), // 通常 (黒塗りなし)
    LEFT("left"),     // 左分岐 (左側黒塗り)
    RIGHT("right");   // 右分岐 (右側黒塗り)

    private final String name;

    EnumSpeedLimitType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}