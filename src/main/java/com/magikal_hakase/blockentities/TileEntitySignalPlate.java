package com.magikal_hakase.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class TileEntitySignalPlate extends BlockEntity {

    // 表示する文字 (例: "4番場", "8外出")
    // 改行に対応するため、\n を使うか、リストで持つ手もありますが、
    // ここではシンプルに「|」区切りで2行対応などにしても良いですし、
    // 文字サイズを自動調整して1行で描画するのもありです。
    // 今回は「縦書き」に対応したいので、文字列として保持し、レンダラーで縦に並べます。
    public String message = "場内";

    public TileEntitySignalPlate(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGNAL_PLATE, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("Message", message);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        message = nbt.getString("Message");
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