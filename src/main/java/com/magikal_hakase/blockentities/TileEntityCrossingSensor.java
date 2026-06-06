package com.magikal_hakase.blockentities;

import com.magikal_hakase.blocks.BlockCrossingSensor;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import mtr.mappings.BlockEntityMapper;

public class TileEntityCrossingSensor extends BlockEntityMapper {

    private BlockPos linkedPanel = null;

    public TileEntityCrossingSensor(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CROSSING_SENSOR, pos, state);
    }

    public void setLinkedPanel(BlockPos panelPos) {
        this.linkedPanel = panelPos;
        markDirty();
    }

    public void triggerLinkedPanel() {
        if (linkedPanel == null || world == null) return;

        // ワイヤレスで制御盤のTileEntityを取得
        if (world.getBlockEntity(linkedPanel) instanceof TileEntityCrossingControlPanel panel) {
            boolean isEnter = getCachedState().get(BlockCrossingSensor.IS_ENTER);

            if (isEnter) {
                panel.addTrain(); // 進入時：カウント+1
            } else {
                panel.removeTrain(); // 退出時：カウント-1
            }
        }
    }

    @Override
    public void readCompoundTag(NbtCompound nbt) {
        super.readCompoundTag(nbt);
        if (nbt.getBoolean("HasLink")) {
            this.linkedPanel = new BlockPos(nbt.getInt("LinkX"), nbt.getInt("LinkY"), nbt.getInt("LinkZ"));
        }
    }

    @Override
    public void writeCompoundTag(NbtCompound nbt) {
        super.writeCompoundTag(nbt);
        if (linkedPanel != null) {
            nbt.putBoolean("HasLink", true);
            nbt.putInt("LinkX", linkedPanel.getX());
            nbt.putInt("LinkY", linkedPanel.getY());
            nbt.putInt("LinkZ", linkedPanel.getZ());
        } else {
            nbt.putBoolean("HasLink", false);
        }
    }
}