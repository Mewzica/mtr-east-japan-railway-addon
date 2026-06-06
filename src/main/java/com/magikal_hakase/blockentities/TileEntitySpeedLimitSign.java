package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class TileEntitySpeedLimitSign extends BlockEntityMapper {

    private String limitValue = "0";

    public TileEntitySpeedLimitSign(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPEED_LIMIT_SIGN, pos, state);
    }

    @Override
    public void writeCompoundTag(NbtCompound nbt) {
        super.writeCompoundTag(nbt);
        nbt.putString("LimitValue", limitValue);
    }

    @Override
    public void readCompoundTag(NbtCompound nbt) {
        super.readCompoundTag(nbt);
        this.limitValue = nbt.getString("LimitValue");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public String getLimitValue() { return limitValue; }
    public void setLimitValue(String value) {
        this.limitValue = value;
        markDirty();
    }
}