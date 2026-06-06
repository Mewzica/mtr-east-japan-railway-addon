package com.magikal_hakase.data;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModSounds {

    public static final Identifier PING_PONG_ID = new Identifier("mtr-east-japan-railway-addon", "ping_pong");
    public static final SoundEvent PING_PONG = new SoundEvent(PING_PONG_ID);

    public static final Identifier ELECTRONIC_BIRD_ID = new Identifier("mtr-east-japan-railway-addon", "electric_bird");
    public static final SoundEvent ELECTRONIC_BIRD = new SoundEvent(ELECTRONIC_BIRD_ID);

    public static final Identifier INFO_MAP_ID = new Identifier("mtr-east-japan-railway-addon", "info_map");
    public static final SoundEvent INFO_MAP = new SoundEvent(INFO_MAP_ID);

    public static final Identifier RESTROOM_ID = new Identifier("mtr-east-japan-railway-addon", "restroom");
    public static final SoundEvent RESTROOM = new SoundEvent(RESTROOM_ID);

    public static final Identifier CROSSING_ALARM_ID = new Identifier("mtr-east-japan-railway-addon", "crossing_alarm");
    public static final SoundEvent CROSSING_ALARM = new SoundEvent(CROSSING_ALARM_ID);

    public static void register() {
        Registry.register(Registry.SOUND_EVENT, PING_PONG_ID, PING_PONG);
        Registry.register(Registry.SOUND_EVENT, ELECTRONIC_BIRD_ID, ELECTRONIC_BIRD);
        Registry.register(Registry.SOUND_EVENT, INFO_MAP_ID, INFO_MAP);
        Registry.register(Registry.SOUND_EVENT, RESTROOM_ID, RESTROOM);
        Registry.register(Registry.SOUND_EVENT, CROSSING_ALARM_ID, CROSSING_ALARM);
    }
}