package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class TileEntityStationGuideSign extends BlockEntityMapper {

    private int backgroundColor = 0xFFFFFFFF;
    private String signText = "";
    private String signTextEnglish = "";
    private String iconId = "none";
    private int iconAlign = 0;
    private int lineColor = 0xFF00FF00;
    private boolean isTextBlack = true;

    private int backgroundColorBack = 0xFFFFFFFF;
    private String signTextBack = "";
    private String signTextEnglishBack = "";
    private String iconIdBack = "none";
    private int iconAlignBack = 0;
    private int lineColorBack = 0xFF00FF00;
    private boolean isTextBlackBack = true;

    public TileEntityStationGuideSign(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STATION_GUIDE_SIGN, pos, state);
    }

    @Override
    public void writeCompoundTag(NbtCompound nbt) {
        super.writeCompoundTag(nbt);
        nbt.putInt("BackgroundColor", backgroundColor);
        nbt.putString("SignText", signText);
        nbt.putString("SignTextEnglish", signTextEnglish);
        nbt.putString("IconId", iconId);
        nbt.putInt("IconAlign", iconAlign);
        nbt.putInt("LineColor", lineColor);
        nbt.putBoolean("IsTextBlack", isTextBlack);

        nbt.putInt("BackgroundColorBack", backgroundColorBack);
        nbt.putString("SignTextBack", signTextBack);
        nbt.putString("SignTextEnglishBack", signTextEnglishBack);
        nbt.putString("IconIdBack", iconIdBack);
        nbt.putInt("IconAlignBack", iconAlignBack);
        nbt.putInt("LineColorBack", lineColorBack);
        nbt.putBoolean("IsTextBlackBack", isTextBlackBack);
    }

    @Override
    public void readCompoundTag(NbtCompound nbt) {
        super.readCompoundTag(nbt);
        this.backgroundColor = nbt.getInt("BackgroundColor");
        this.signText = nbt.getString("SignText");
        this.signTextEnglish = nbt.getString("SignTextEnglish");
        this.iconId = nbt.getString("IconId");
        this.iconAlign = nbt.getInt("IconAlign");
        this.lineColor = nbt.getInt("LineColor");

        if (nbt.contains("BackgroundColorBack")) {
            this.backgroundColorBack = nbt.getInt("BackgroundColorBack");
            this.signTextBack = nbt.getString("SignTextBack");
            this.signTextEnglishBack = nbt.getString("SignTextEnglishBack");
            this.iconIdBack = nbt.getString("IconIdBack");
            this.iconAlignBack = nbt.getInt("IconAlignBack");
            this.lineColorBack = nbt.getInt("LineColorBack");
        } else {
            this.backgroundColorBack = this.backgroundColor;
            this.signTextBack = "";
            this.signTextEnglishBack = "";
            this.iconIdBack = "none";
            this.iconAlignBack = 0;
            this.lineColorBack = this.lineColor;
        }
        if (nbt.contains("IsTextBlack")) {
            this.isTextBlack = nbt.getBoolean("IsTextBlack");
            this.isTextBlackBack = nbt.getBoolean("IsTextBlackBack");
        } else {
            // 互換性のため、データがない場合はデフォルト値
            this.isTextBlack = true;
            this.isTextBlackBack = true;
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

    // --- Getter / Setter ---
    public int getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(int color) { this.backgroundColor = color; markDirty(); }
    public String getSignText() { return signText; }
    public void setSignText(String text) { this.signText = text; markDirty(); }
    public String getSignTextEnglish() { return signTextEnglish; }
    public void setSignTextEnglish(String text) { this.signTextEnglish = text; markDirty(); }
    public String getIconId() { return iconId; }
    public void setIconId(String id) { this.iconId = id; markDirty(); }
    public int getIconAlign() { return iconAlign; }
    public void setIconAlign(int align) { this.iconAlign = align; markDirty(); }
    public int getLineColor() { return lineColor; }
    public void setLineColor(int color) { this.lineColor = color; markDirty(); }
    public boolean isTextBlack() { return isTextBlack; }
    public void setTextBlack(boolean isBlack) { this.isTextBlack = isBlack; markDirty(); }

    public int getBackgroundColorBack() { return backgroundColorBack; }
    public void setBackgroundColorBack(int color) { this.backgroundColorBack = color; markDirty(); }
    public String getSignTextBack() { return signTextBack; }
    public void setSignTextBack(String text) { this.signTextBack = text; markDirty(); }
    public String getSignTextEnglishBack() { return signTextEnglishBack; }
    public void setSignTextEnglishBack(String text) { this.signTextEnglishBack = text; markDirty(); }
    public String getIconIdBack() { return iconIdBack; }
    public void setIconIdBack(String id) { this.iconIdBack = id; markDirty(); }
    public int getIconAlignBack() { return iconAlignBack; }
    public void setIconAlignBack(int align) { this.iconAlignBack = align; markDirty(); }
    public int getLineColorBack() { return lineColorBack; }
    public void setLineColorBack(int color) { this.lineColorBack = color; markDirty(); }
    public boolean isTextBlackBack() { return isTextBlackBack; }
    public void setTextBlackBack(boolean isBlack) { this.isTextBlackBack = isBlack; markDirty(); }
}