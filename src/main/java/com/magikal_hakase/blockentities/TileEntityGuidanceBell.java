package com.magikal_hakase.blockentities;

import com.magikal_hakase.data.GuidanceSound;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntityGuidanceBell extends BlockEntityMapper {

    // 保存データ
    public GuidanceSound soundType = GuidanceSound.PING_PONG;
    public int interval = GuidanceSound.PING_PONG.getDefaultInterval();

    // 内部カウンター (保存不要)
    private int timer = 0;

    public TileEntityGuidanceBell(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GUIDANCE_BELL, pos, state);
    }

    // ▼ 毎Tick呼ばれる処理 (サーバー側で実行)
    public static void tick(World world, BlockPos pos, BlockState state, TileEntityGuidanceBell entity) {
        if (world.isClient) return; // 音はサーバーから全員に送信するため、クライアント側処理はスキップ

        entity.timer++;

        // 設定された間隔を超えたら音を鳴らす
        if (entity.timer >= entity.interval) {
            entity.timer = 0;

            // 音の再生 (半径広め、音量大きめ)
            // ※将来的にはModSounds.MY_SOUNDなどを指定
            world.playSound(null, pos, entity.soundType.getSound(), SoundCategory.BLOCKS, 1.25f, 1.0f);
        }
    }

    @Override
    public void writeCompoundTag(NbtCompound nbt) {
        super.writeCompoundTag(nbt); // 親クラスの保存処理
        nbt.putString("SoundType", soundType.name());
        nbt.putInt("Interval", interval);
    }

    @Override
    public void readCompoundTag(NbtCompound nbt) {
        super.readCompoundTag(nbt); // 親クラスの読み込み処理
        try {
            soundType = GuidanceSound.valueOf(nbt.getString("SoundType"));
        } catch (IllegalArgumentException e) {
            soundType = GuidanceSound.PING_PONG;
        }
        interval = nbt.getInt("Interval");
        if (interval <= 0) interval = soundType.getDefaultInterval();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}