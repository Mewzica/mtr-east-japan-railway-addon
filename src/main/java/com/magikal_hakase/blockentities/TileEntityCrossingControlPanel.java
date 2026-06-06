package com.magikal_hakase.blockentities;

import com.magikal_hakase.blocks.BlockCrossingControlPanel;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import mtr.mappings.BlockEntityMapper;

public class TileEntityCrossingControlPanel extends BlockEntityMapper {

    private int trainCount = 0;

    public TileEntityCrossingControlPanel(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CROSSING_CONTROL_PANEL, pos, state);
    }

    public void addTrain() {
        this.trainCount++;
        updatePowerState();
    }

    public void removeTrain() {
        if (this.trainCount > 0) {
            this.trainCount--;
        }
        updatePowerState();
    }

    public void resetCount() {
        this.trainCount = 0;
        updatePowerState();
    }

    private void updatePowerState() {
        boolean shouldBePowered = (this.trainCount > 0);
        BlockState currentState = getCachedState();

        if (currentState.get(BlockCrossingControlPanel.POWERED) != shouldBePowered) {
            if (world != null) {
                world.setBlockState(pos, currentState.with(BlockCrossingControlPanel.POWERED, shouldBePowered), 3);
                // 周囲のブロック（踏切柱など）に「動力状態が変わった」と通知する
                world.updateNeighborsAlways(pos, currentState.getBlock());
            }
        }
        markDirty();
    }

    @Override
    public void readCompoundTag(NbtCompound nbt) {
        super.readCompoundTag(nbt);
        this.trainCount = nbt.getInt("TrainCount");
    }

    @Override
    public void writeCompoundTag(NbtCompound nbt) {
        super.writeCompoundTag(nbt);
        nbt.putInt("TrainCount", trainCount);
    }
}