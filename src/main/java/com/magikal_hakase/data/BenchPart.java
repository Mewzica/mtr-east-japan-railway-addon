package com.magikal_hakase.data;

import net.minecraft.util.StringIdentifiable;

public enum BenchPart implements StringIdentifiable {
    SINGLE, // 単体
    LEFT,   // 左端
    MIDDLE, // 中間
    RIGHT;  // 右端

    @Override
    public String asString() {
        return this.toString().toLowerCase();
    }
}