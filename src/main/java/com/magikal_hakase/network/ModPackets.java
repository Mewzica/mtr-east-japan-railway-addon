package com.magikal_hakase.network;

import com.magikal_hakase.blockentities.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModPackets {
    public static final Identifier PACKET_UPDATE_SIGN = new Identifier("mtr-east-japan-railway-addon", "update_sign");
    public static final Identifier PACKET_UPDATE_SIGNAL_PLATE = new Identifier("mtr-east-japan-railway-addon", "update_signal_plate");
    public static final Identifier PACKET_UPDATE_GUIDE_SIGN = new Identifier("mtr-east-japan-railway-addon", "update_guide_sign");
    public static final Identifier PACKET_UPDATE_STOPPING_POS = new Identifier("mtr-east-japan-railway-addon", "update_stopping_pos");
    public static final Identifier PACKET_UPDATE_GRADIENT_MARKER = new Identifier("mtr-east-japan-railway-addon", "update_gradient_marker");
    public static final Identifier PACKET_UPDATE_SPEED_LIMIT = new Identifier("mtr-east-japan-railway-addon", "update_speed_limit");
    public static final Identifier PACKET_UPDATE_JNR_SIGN = new Identifier("mtr-east-japan-railway-addon", "update_jnr_sign");
    public static final Identifier PACKET_UPDATE_CROSSING_GATE = new Identifier("mtr-east-japan-railway-addon", "update_crossing_gate");

    public static void registerC2SPackets() {

        ServerPlayNetworking.registerGlobalReceiver(PACKET_UPDATE_SIGN, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String stationName = buf.readString();
            String stationHiragana = buf.readString();
            String stationRomaji = buf.readString();
            String nextStation = buf.readString();
            String nextStationRomaji = buf.readString();
            String prevStation = buf.readString();
            String prevStationRomaji = buf.readString();
            String stationNumber = buf.readString();
            String stationNumberPrefix = buf.readString();
            int lineColor = buf.readInt();
            int stationColor = buf.readInt();
            int arrowFrontOrdinal = buf.readInt();
            int arrowBackOrdinal = buf.readInt();

            server.execute(() -> {
                if (player.world.getBlockEntity(pos) instanceof TileEntityHangingStationSign sign) {
                    sign.stationName = stationName;
                    sign.stationHiragana = stationHiragana;
                    sign.stationRomaji = stationRomaji;
                    sign.nextStation = nextStation;
                    sign.nextStationRomaji = nextStationRomaji;
                    sign.prevStation = prevStation;
                    sign.prevStationRomaji = prevStationRomaji;
                    sign.stationNumber = stationNumber;
                    sign.stationNumberPrefix = stationNumberPrefix;
                    sign.lineColor = lineColor;
                    sign.stationColor = stationColor;
                    if (arrowFrontOrdinal >= 0 && arrowFrontOrdinal < TileEntityHangingStationSign.ArrowType.values().length) {
                        sign.arrowTypeFront = TileEntityHangingStationSign.ArrowType.values()[arrowFrontOrdinal];
                    }
                    if (arrowBackOrdinal >= 0 && arrowBackOrdinal < TileEntityHangingStationSign.ArrowType.values().length) {
                        sign.arrowTypeBack = TileEntityHangingStationSign.ArrowType.values()[arrowBackOrdinal];
                    }
                    sign.markDirty();
                    player.world.updateListeners(pos, sign.getCachedState(), sign.getCachedState(), 3);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(PACKET_UPDATE_SIGNAL_PLATE, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String message = buf.readString();
            server.execute(() -> {
                if (player.world.getBlockEntity(pos) instanceof com.magikal_hakase.blockentities.TileEntitySignalPlate plate) {
                    plate.message = message;
                    plate.markDirty();
                    player.world.updateListeners(pos, plate.getCachedState(), plate.getCachedState(), 3);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(PACKET_UPDATE_GUIDE_SIGN, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();

            // 表面
            int color = buf.readInt();
            String text = buf.readString();
            String textEnglish = buf.readString();
            String icon = buf.readString();
            int align = buf.readInt();
            int lineColor = buf.readInt();
            boolean isTextBlackFront = buf.readBoolean();

            // 裏面
            int colorBack = buf.readInt();
            String textBack = buf.readString();
            String textEnglishBack = buf.readString();
            String iconBack = buf.readString();
            int alignBack = buf.readInt();
            int lineColorBack = buf.readInt();
            boolean isTextBlackBack = buf.readBoolean();

            server.execute(() -> {
                if (player.world.getBlockEntity(pos) instanceof TileEntityStationGuideSign sign) {
                    sign.setBackgroundColor(color);
                    sign.setSignText(text);
                    sign.setSignTextEnglish(textEnglish);
                    sign.setIconId(icon);
                    sign.setIconAlign(align);
                    sign.setLineColor(lineColor);
                    sign.setTextBlack(isTextBlackFront);

                    sign.setBackgroundColorBack(colorBack);
                    sign.setSignTextBack(textBack);
                    sign.setSignTextEnglishBack(textEnglishBack);
                    sign.setIconIdBack(iconBack);
                    sign.setIconAlignBack(alignBack);
                    sign.setLineColorBack(lineColorBack);
                    sign.setTextBlackBack(isTextBlackBack);

                    sign.markDirty();
                    player.world.updateListeners(pos, sign.getCachedState(), sign.getCachedState(), 3);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(PACKET_UPDATE_STOPPING_POS, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String value = buf.readString();

            server.execute(() -> {
                if (player.world.getBlockEntity(pos) instanceof TileEntityStoppingPositionSign sign) {
                    sign.setSignValue(value);
                    sign.markDirty();
                    player.world.updateListeners(pos, sign.getCachedState(), sign.getCachedState(), 3);
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(PACKET_UPDATE_GRADIENT_MARKER, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            int typeFrontOrdinal = buf.readInt();
            String valueFront = buf.readString();
            int typeBackOrdinal = buf.readInt();
            String valueBack = buf.readString();

            server.execute(() -> {
                // 1. TileEntity更新
                if (player.world.getBlockEntity(pos) instanceof TileEntityGradientMarker marker) {
                    marker.setTypeFront(typeFrontOrdinal);
                    marker.setValueFront(valueFront);
                    marker.setTypeBack(typeBackOrdinal);
                    marker.setValueBack(valueBack);
                    marker.markDirty(); // 必須: データが変わったことをマーク

                    // ★重要: 強制的にクライアントへ「更新しろ」と通知を送る
                    // これがないと、数値だけ変えた時に見た目が変わらないため、パケットが飛ばないことがあります
                    BlockState state = player.world.getBlockState(pos);
                    player.world.updateListeners(pos, state, state, 3);
                }

                // 2. BlockState更新 (モデル形状の変更)
                BlockState state = player.world.getBlockState(pos);
                if (state.getBlock() instanceof com.magikal_hakase.blocks.BlockGradientMarker) {
                    com.magikal_hakase.data.EnumGradientType[] types = com.magikal_hakase.data.EnumGradientType.values();

                    com.magikal_hakase.data.EnumGradientType frontType = types[typeFrontOrdinal % types.length];
                    com.magikal_hakase.data.EnumGradientType backType = types[typeBackOrdinal % types.length];

                    BlockState newState = state
                            .with(com.magikal_hakase.blocks.BlockGradientMarker.FRONT_TYPE, frontType)
                            .with(com.magikal_hakase.blocks.BlockGradientMarker.BACK_TYPE, backType);

                    // 状態が実際に変わる場合のみ setBlockState する (無駄な処理を省く)
                    if (!state.equals(newState)) {
                        player.world.setBlockState(pos, newState, 3);
                    }
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(PACKET_UPDATE_SPEED_LIMIT, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String value = buf.readString();
            int typeOrdinal = buf.readInt();

            server.execute(() -> {
                // 1. TileEntity更新 (数値)
                if (player.world.getBlockEntity(pos) instanceof TileEntitySpeedLimitSign sign) {
                    sign.setLimitValue(value);
                    sign.markDirty();

                    // 数値のみ変更時のための強制通知
                    BlockState state = player.world.getBlockState(pos);
                    player.world.updateListeners(pos, state, state, 3);
                }

                // 2. BlockState更新 (形状)
                BlockState state = player.world.getBlockState(pos);
                if (state.getBlock() instanceof com.magikal_hakase.blocks.BlockSpeedLimitSign) {
                    com.magikal_hakase.data.EnumSpeedLimitType[] types = com.magikal_hakase.data.EnumSpeedLimitType.values();
                    com.magikal_hakase.data.EnumSpeedLimitType newType = types[typeOrdinal % types.length];

                    BlockState newState = state.with(com.magikal_hakase.blocks.BlockSpeedLimitSign.TYPE, newType);

                    if (!state.equals(newState)) {
                        player.world.setBlockState(pos, newState, 3);
                    }
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(PACKET_UPDATE_JNR_SIGN, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();

            String hiragana = buf.readString();
            String kanji = buf.readString();
            String english = buf.readString();
            String location = buf.readString();
            String prevHira = buf.readString();
            String prevEng = buf.readString();
            String nextHira = buf.readString();
            String nextEng = buf.readString();

            server.execute(() -> {
                if (player.world.getBlockEntity(pos) instanceof TileEntityJNRStationSign sign) {
                    sign.stationHiragana = hiragana;
                    sign.stationKanji = kanji;
                    sign.stationEnglish = english;
                    sign.location = location;
                    sign.prevStationHiragana = prevHira;
                    sign.prevStationEnglish = prevEng;
                    sign.nextStationHiragana = nextHira;
                    sign.nextStationEnglish = nextEng;

                    sign.markDirty();
                    // クライアント側に変更を通知して再描画させる
                    BlockState state = player.world.getBlockState(pos);
                    player.world.updateListeners(pos, state, state, 3);
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(PACKET_UPDATE_CROSSING_GATE, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            int length = buf.readInt();
            int delayTicks = buf.readInt();

            server.execute(() -> {
                if (player.world.getBlockEntity(pos) instanceof TileEntityCrossingGate gate) {
                    gate.setGateLength(length);
                    gate.setDelayTicks(delayTicks);
                    gate.markDirty();
                }
            });
        });
    }
}