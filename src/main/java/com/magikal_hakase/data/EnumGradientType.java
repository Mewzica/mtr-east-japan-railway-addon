package com.magikal_hakase.data;

import net.minecraft.util.StringIdentifiable;

public enum EnumGradientType implements StringIdentifiable {
    UP("up"),
    DOWN("down"),
    LEVEL("level");

    private final String name;

    EnumGradientType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}