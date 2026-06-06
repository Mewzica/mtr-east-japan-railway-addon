package com.magikal_hakase.data;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;

public enum GuidanceSound implements StringIdentifiable {
    // 定義: 名前(ID), デフォルト間隔(秒), 仮のサウンドイベント
    PING_PONG("ping_pong", 10, ModSounds.PING_PONG),
    INFO_MAP("info_map", 13, ModSounds.INFO_MAP),
    RESTROOM("restroom", 6, ModSounds.RESTROOM),
    ELECTRONIC_BIRD("electronic_bird", 22, ModSounds.ELECTRONIC_BIRD);

    private final String name;
    private final int defaultIntervalTicks;
    private final SoundEvent soundEvent;

    GuidanceSound(String name, int defaultIntervalSeconds, SoundEvent soundEvent) {
        this.name = name;
        this.defaultIntervalTicks = defaultIntervalSeconds * 20;
        this.soundEvent = soundEvent;
    }

    // 将来的にオリジナルのSoundEventを登録する場合はここを書き換えます
    public SoundEvent getSound() {
        return this.soundEvent;
    }

    public int getDefaultInterval() {
        return this.defaultIntervalTicks;
    }

    @Override
    public String asString() {
        return this.name;
    }
}