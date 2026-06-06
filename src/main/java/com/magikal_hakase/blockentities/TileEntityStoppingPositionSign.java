package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class TileEntityStoppingPositionSign extends BlockEntityMapper {

    private String signValue = ""; // 数字を保存する変数

    public TileEntityStoppingPositionSign(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STOPPING_POSITION_SIGN, pos, state);
    }

    @Override
    public void writeCompoundTag(NbtCompound nbt) {
        super.writeCompoundTag(nbt);
        nbt.putString("SignValue", signValue);
    }

    @Override
    public void readCompoundTag(NbtCompound nbt) {
        super.readCompoundTag(nbt);
        this.signValue = nbt.getString("SignValue");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public String getSignValue() { return signValue; }
    public void setSignValue(String value) {
        this.signValue = value;
        markDirty();
    }
}