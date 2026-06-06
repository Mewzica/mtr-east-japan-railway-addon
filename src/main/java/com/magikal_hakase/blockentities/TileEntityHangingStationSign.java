package com.magikal_hakase.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class TileEntityHangingStationSign extends BlockEntity {

    public enum ArrowType {
        NONE, LEFT, RIGHT, BOTH;
        public ArrowType next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    public String stationName = "新  宿";
    public String stationHiragana = "しんじゅく";
    public String stationRomaji = "Shinjuku";
    public String nextStation = "中野";
    public String nextStationRomaji = "Nakano";
    public String prevStation = "四ツ谷";
    public String prevStationRomaji = "Yotsuya";
    public String stationNumber = "05";
    public String stationNumberPrefix = "JC";
    public int lineColor = 0x00CB00;
    public int stationColor = 0xFE5000;

    public ArrowType arrowTypeFront = ArrowType.NONE;
    public ArrowType arrowTypeBack = ArrowType.NONE;

    public TileEntityHangingStationSign(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HANGING_STATION_SIGN, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("StationName", stationName);
        nbt.putString("StationHiragana", stationHiragana);
        nbt.putString("StationRomaji", stationRomaji);
        nbt.putString("NextStation", nextStation);
        nbt.putString("NextStationRomaji", nextStationRomaji);
        nbt.putString("PrevStation", prevStation);
        nbt.putString("PrevStationRomaji", prevStationRomaji);
        nbt.putString("StationNumber", stationNumber);
        nbt.putString("StationNumberPrefix", stationNumberPrefix);
        nbt.putInt("LineColor", lineColor);
        nbt.putInt("StationColor", stationColor);
        // 保存
        nbt.putInt("ArrowTypeFront", arrowTypeFront.ordinal());
        nbt.putInt("ArrowTypeBack", arrowTypeBack.ordinal());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        stationName = nbt.getString("StationName");
        stationHiragana = nbt.getString("StationHiragana");
        stationRomaji = nbt.getString("StationRomaji");
        nextStation = nbt.getString("NextStation");
        nextStationRomaji = nbt.getString("NextStationRomaji");
        prevStation = nbt.getString("PrevStation");
        prevStationRomaji = nbt.getString("PrevStationRomaji");
        stationNumber = nbt.getString("StationNumber");
        stationNumberPrefix = nbt.getString("StationNumberPrefix");
        if (nbt.contains("LineColor")) lineColor = nbt.getInt("LineColor");
        if (nbt.contains("StationColor")) stationColor = nbt.getInt("StationColor");

        // 読み込み
        if (nbt.contains("ArrowTypeFront")) {
            arrowTypeFront = ArrowType.values()[nbt.getInt("ArrowTypeFront")];
            arrowTypeBack = ArrowType.values()[nbt.getInt("ArrowTypeBack")];
        } else if (nbt.contains("ArrowType")) {
            ArrowType oldType = ArrowType.values()[nbt.getInt("ArrowType")];
            arrowTypeFront = oldType;
            arrowTypeBack = oldType;
        }
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