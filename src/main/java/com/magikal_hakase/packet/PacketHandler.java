package com.magikal_hakase.packet;

import com.magikal_hakase.blockentities.TileEntityGuidanceBell;
import com.magikal_hakase.data.GuidanceSound;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PacketHandler {

    // パケットのID
    public static final Identifier PACKET_UPDATE_BELL = new Identifier("mtr-east-japan-railway-addon", "update_guidance_bell");

    // サーバー側の受信処理を登録 (Modの初期化時に呼び出してください)
    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_UPDATE_BELL, PacketHandler::receiveUpdateBell);
    }

    // パケットを受け取った時の処理
    private static void receiveUpdateBell(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // データの読み取り
        BlockPos pos = buf.readBlockPos();
        String soundName = buf.readString();
        int intervalSeconds = buf.readInt();

        // サーバーのスレッドで実行
        server.execute(() -> {
            if (player.world.isChunkLoaded(pos)) {
                BlockEntity be = player.world.getBlockEntity(pos);
                if (be instanceof TileEntityGuidanceBell) {
                    TileEntityGuidanceBell bell = (TileEntityGuidanceBell) be;

                    // データを更新
                    try {
                        bell.soundType = GuidanceSound.valueOf(soundName);
                    } catch (IllegalArgumentException e) {
                        bell.soundType = GuidanceSound.PING_PONG;
                    }
                    bell.interval = intervalSeconds * 20; // 秒 → Tick変換

                    // 変更を保存してクライアントに同期
                    bell.markDirty();
                    BlockState state = player.world.getBlockState(pos);
                    player.world.updateListeners(pos, state, state, 3);
                }
            }
        });
    }
}