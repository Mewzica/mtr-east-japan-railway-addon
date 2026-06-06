package com.magikal_hakase.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class VerticalStationNameSignBlockEntity extends BlockEntity {

    // デフォルト値の設定
    private String stationName = "濡瑠";
    private String stationNameEn = "Null";
    private int stationColor = 0xFF8800;

    public VerticalStationNameSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.JR_EAST_VERTICAL_SIGN, pos, state);
    }

    // --- データの保存と読み込み (NBT) ---

    // ワールドデータから読み込むとき
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.stationName = nbt.getString(getStationName());
        this.stationNameEn = nbt.getString(getStationNameEn());
        if (nbt.contains("StationColor")) {
            this.stationColor = nbt.getInt("StationColor");
        }
    }

    // ワールドデータへ保存するとき
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("StationName", this.stationName);
        nbt.putString("StationNameEn", this.stationNameEn);
        nbt.putInt("StationColor", this.stationColor);
    }

    // --- サーバーとクライアントの同期 (パケット) ---
    // これを実装しないと、再ログインするまで看板の文字が変わらない等のバグが起きます

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    // --- Getter / Setter ---
    // レンダラーやGUIからアクセスするために必要です

    public String getStationName() {
        return stationName;
    }

    public String getStationNameEn() {
        return stationNameEn;
    }

    public int getStationColor() {
        return stationColor;
    }

    // データをセットした後は markDirty() で「変更があったこと」をゲームに伝えます
    public void setStationData(String name, String nameEn, int color) {
        this.stationName = name;
        this.stationNameEn = nameEn;
        this.stationColor = color;
        markDirty();
        // 変更を即座にクライアントに反映させるための処理
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
}