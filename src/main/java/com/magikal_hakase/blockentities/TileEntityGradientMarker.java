package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class TileEntityGradientMarker extends BlockEntityMapper {

    // 0=UP(上り), 1=DOWN(下り), 2=LEVEL(水平)
    private int typeFront = 0;
    private int typeBack = 1;

    private String valueFront = "10";
    private String valueBack = "10";

    public TileEntityGradientMarker(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRADIENT_MARKER, pos, state);
    }

    @Override
    public void writeCompoundTag(NbtCompound nbt) {
        super.writeCompoundTag(nbt);
        nbt.putInt("TypeFront", typeFront);
        nbt.putInt("TypeBack", typeBack);
        nbt.putString("ValueFront", valueFront);
        nbt.putString("ValueBack", valueBack);
    }

    @Override
    public void readCompoundTag(NbtCompound nbt) {
        super.readCompoundTag(nbt);
        this.typeFront = nbt.getInt("TypeFront");
        this.typeBack = nbt.getInt("TypeBack");
        this.valueFront = nbt.getString("ValueFront");
        this.valueBack = nbt.getString("ValueBack");
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
    public int getTypeFront() { return typeFront; }
    public void setTypeFront(int type) { this.typeFront = type; markDirty(); }

    public int getTypeBack() { return typeBack; }
    public void setTypeBack(int type) { this.typeBack = type; markDirty(); }

    public String getValueFront() { return valueFront; }
    public void setValueFront(String value) { this.valueFront = value; markDirty(); }

    public String getValueBack() { return valueBack; }
    public void setValueBack(String value) { this.valueBack = value; markDirty(); }
}