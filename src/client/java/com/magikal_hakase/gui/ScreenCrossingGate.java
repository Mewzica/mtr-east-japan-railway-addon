package com.magikal_hakase.gui;

import com.magikal_hakase.blockentities.TileEntityCrossingGate;
import com.magikal_hakase.network.ModPackets;
import mtr.mappings.ScreenMapper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ScreenCrossingGate extends ScreenMapper {

    private final BlockPos pos;
    private final TileEntityCrossingGate entity;

    private TextFieldWidget fieldLength;
    private TextFieldWidget fieldDelay;

    public ScreenCrossingGate(BlockPos pos, TileEntityCrossingGate entity) {
        super(Text.literal("Crossing Gate Config"));
        this.pos = pos;
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();
        int w = this.width;
        int h = this.height;
        int x = w / 2;
        int y = h / 4;

        // 竿の長さ設定欄
        this.fieldLength = new TextFieldWidget(textRenderer, x - 100, y, 200, 20, Text.literal("Gate Length"));
        this.fieldLength.setMaxLength(2);
        this.fieldLength.setText(String.valueOf(entity.getGateLength()));
        addDrawableChild(fieldLength);

        // 遅延設定欄（秒数を内部的にTicksに変換するため、ここでは秒数として入力させます）
        this.fieldDelay = new TextFieldWidget(textRenderer, x - 100, y + 40, 200, 20, Text.literal("Delay Seconds"));
        this.fieldDelay.setMaxLength(4);
        // Ticks を 秒 (float) に戻して表示
        this.fieldDelay.setText(String.valueOf(entity.getDelayTicks() / 20.0f));
        addDrawableChild(fieldDelay);

        // 保存ボタン
        addDrawableChild(new ButtonWidget(x - 50, y + 90, 100, 20, Text.literal("Save"), button -> {
            saveAndClose();
        }));
    }

    private void saveAndClose() {
        int length = 3;
        int delayTicks = 40;

        try {
            length = Math.max(1, Integer.parseInt(fieldLength.getText()));
        } catch (NumberFormatException e) {}

        try {
            // 秒数入力を受けて Ticks (1秒=20Ticks) に変換
            float seconds = Math.max(0.0f, Float.parseFloat(fieldDelay.getText()));
            delayTicks = Math.round(seconds * 20.0f);
        } catch (NumberFormatException e) {}

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeInt(length);
        buf.writeInt(delayTicks);

        ClientPlayNetworking.send(ModPackets.PACKET_UPDATE_CROSSING_GATE, buf);
        this.close();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        int x = this.width / 2;
        int y = this.height / 4;

        // ラベルの描画
        textRenderer.drawWithShadow(matrices, "Gate Length (Blocks):", x - 100, y - 12, 0xFFFFFF);
        textRenderer.drawWithShadow(matrices, "Action Delay (Seconds):", x - 100, y + 28, 0xFFFFFF);
    }
}