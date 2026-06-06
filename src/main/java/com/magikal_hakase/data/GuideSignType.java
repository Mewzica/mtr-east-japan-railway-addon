package com.magikal_hakase.data;

import net.minecraft.util.StringIdentifiable;

public enum GuideSignType implements StringIdentifiable {
    SINGLE, // 単体 (左右とも繋がっていない)
    LEFT,   // 左端 (右だけ繋がっている)
    MIDDLE, // 中間 (左右とも繋がっている)
    RIGHT;  // 右端 (左だけ繋がっている)

    @Override
    public String asString() {
        return this.toString().toLowerCase();
    }
}