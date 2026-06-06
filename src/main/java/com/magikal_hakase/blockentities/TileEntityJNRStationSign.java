package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class TileEntityJNRStationSign extends BlockEntityMapper {

    // 保存するテキストデータ
    public String stationHiragana = "えきめい";
    public String stationKanji = "駅名";
    public String stationEnglish = "STATION NAME";
    public String location = "(所在地)"; // 追加: 所在地

    public String prevStationHiragana = "まえのえき";
    public String prevStationEnglish = "PREV ST.";
    public String nextStationHiragana = "つぎのえき";
    public String nextStationEnglish = "NEXT ST.";

    public TileEntityJNRStationSign(BlockPos pos, BlockState state) {
        super(ModBlockEntities.JNR_STATION_SIGN, pos, state);
    }

    @Override
    public void writeCompoundTag(NbtCompound nbt) {
        super.writeCompoundTag(nbt);
        nbt.putString("StationHiragana", stationHiragana);
        nbt.putString("StationKanji", stationKanji);
        nbt.putString("StationEnglish", stationEnglish);
        nbt.putString("Location", location);
        nbt.putString("PrevStationHiragana", prevStationHiragana);
        nbt.putString("PrevStationEnglish", prevStationEnglish);
        nbt.putString("NextStationHiragana", nextStationHiragana);
        nbt.putString("NextStationEnglish", nextStationEnglish);
    }

    @Override
    public void readCompoundTag(NbtCompound nbt) {
        super.readCompoundTag(nbt);
        stationHiragana = nbt.getString("StationHiragana");
        stationKanji = nbt.getString("StationKanji");
        stationEnglish = nbt.getString("StationEnglish");
        location = nbt.getString("Location");
        prevStationHiragana = nbt.getString("PrevStationHiragana");
        prevStationEnglish = nbt.getString("PrevStationEnglish");
        nextStationHiragana = nbt.getString("NextStationHiragana");
        nextStationEnglish = nbt.getString("NextStationEnglish");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}