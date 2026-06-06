package com.magikal_hakase.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class SeatEntity extends Entity {
    public SeatEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    public SeatEntity(World world, double x, double y, double z) {
        this(ModEntities.SEAT_ENTITY, world);
        this.setPosition(x, y, z);
    }

    @Override
    protected void initDataTracker() {}
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
    @Override
    public Packet<?> createSpawnPacket() { return new EntitySpawnS2CPacket(this); }

    @Override
    public double getMountedHeightOffset() {
        return -0.1; // 座る高さを微調整
    }
}