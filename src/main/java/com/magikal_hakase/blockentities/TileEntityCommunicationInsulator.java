package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class TileEntityCommunicationInsulator extends BlockEntityMapper {

    public final Set<BlockPos> connections = new HashSet<>();

    public TileEntityCommunicationInsulator(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMMUNICATION_INSULATOR, pos, state);
    }

    public void addConnection(BlockPos targetPos) {
        if (targetPos.equals(this.pos)) return;
        connections.add(targetPos);
        markDirty();
        if (world != null) world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    public void removeConnection(BlockPos targetPos) {
        connections.remove(targetPos);
        markDirty();
        if (world != null) world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    @Override
    public void writeCompoundTag(NbtCompound nbt) {
        super.writeCompoundTag(nbt);
        NbtList list = new NbtList();
        for (BlockPos connection : connections) {
            list.add(NbtHelper.fromBlockPos(connection));
        }
        nbt.put("Connections", list);
    }

    @Override
    public void readCompoundTag(NbtCompound nbt) {
        super.readCompoundTag(nbt);
        connections.clear();
        if (nbt.contains("Connections")) {
            NbtList list = nbt.getList("Connections", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                connections.add(NbtHelper.toBlockPos(list.getCompound(i)));
            }
        }
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