package com.magikal_hakase.blockentities;

import com.magikal_hakase.blocks.BlockCrossingGate;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import mtr.mappings.BlockEntityMapper;

public class TileEntityCrossingGate extends BlockEntityMapper {

    // --- GUI設定値 ---
    private int gateLength = 6;
    private int delayTicks = 80;

    // --- 内部アニメーション状態 ---
    private int currentDelay = 0;      // 遅延カウンター
    private float currentAngle = 90.0f; // 現在の竿の角度
    private boolean lastPowered = false; // 前回のレッドストーン状態を記憶

    private static final float SPEED = 1.5f; // 竿の動く速度

    public TileEntityCrossingGate(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CROSSING_GATE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TileEntityCrossingGate entity) {
        // 1. ブロック状態からレッドストーン動力を直接取得
        boolean powered = world.getBlockState(pos).get(BlockCrossingGate.POWERED);

        if (!state.contains(Properties.POWERED)) {
            return; // 空気ブロック等の場合は何もせず終了
        }

        // ★ 修正: 信号の状態が変わった瞬間を検知
        if (powered != entity.lastPowered) {
            if (powered) {
                // 信号がONになった時（閉まる前）は、設定した遅延タイマーをセット
                entity.currentDelay = entity.delayTicks;
            } else {
                // 信号がOFFになった時（開く時）は、遅延タイマーを0にして即座に動かす
                entity.currentDelay = 0;
            }
            entity.lastPowered = powered;
            entity.markDirty();
        }

        // 3. 遅延タイマーのカウントダウン
        if (entity.currentDelay > 0) {
            entity.currentDelay--;
            return;
        }

        // 4. 目標角度の決定 (信号ON=0度[閉], 信号OFF=90度[開])
        float targetAngle = powered ? 0.0f : 90.0f;

        // 5. 角度のスムーズ移動
        if (entity.currentAngle != targetAngle) {
            if (entity.currentAngle < targetAngle) {
                entity.currentAngle = Math.min(entity.currentAngle + SPEED, targetAngle);
            } else {
                entity.currentAngle = Math.max(entity.currentAngle - SPEED, targetAngle);
            }
        }
    }

    // --- データの保存・読み込み ---
    @Override
    public void writeCompoundTag(NbtCompound nbt) {
        super.writeCompoundTag(nbt);
        nbt.putInt("GateLength", gateLength);
        nbt.putInt("DelayTicks", delayTicks);
        nbt.putBoolean("LastPowered", lastPowered);
        nbt.putFloat("CurrentAngle", currentAngle);
    }

    @Override
    public void readCompoundTag(NbtCompound nbt) {
        super.readCompoundTag(nbt);
        this.gateLength = nbt.getInt("GateLength");
        this.delayTicks = nbt.getInt("DelayTicks");
        this.lastPowered = nbt.getBoolean("LastPowered");
        this.currentAngle = nbt.getFloat("CurrentAngle");
    }

    // ★ 修正ポイント: GUI更新パケットを受けた直後に即座にクライアントへ同期をかける
    public NbtCompound getUpdateTag() {
        return createNbt();
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    // --- Getter / Setter ---
    public float getCurrentAngle() { return currentAngle; }
    public int getGateLength() { return gateLength; }
    public void setGateLength(int length) { this.gateLength = length; markDirty(); }
    public int getDelayTicks() { return delayTicks; }
    public void setDelayTicks(int ticks) { this.delayTicks = ticks; markDirty(); }
}