package com.magikal_hakase.data;

import net.minecraft.util.StringIdentifiable;

public enum EnumCrossingAttachment implements StringIdentifiable {
    NONE("none"),
    SIGN("sign"),         // 踏切警標
    LIGHT_H("light_h"),   // 踏切警報灯 (水平)
    LIGHT_V("light_v"),
    SPEAKER("speaker");// 踏切警報灯 (垂直)

    private final String name;

    EnumCrossingAttachment(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}