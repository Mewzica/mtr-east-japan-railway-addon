package com.magikal_hakase.gui;

import com.magikal_hakase.blockentities.TileEntityStoppingPositionSign;
import com.magikal_hakase.network.ModPackets;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class ScreenStoppingPositionSign extends ScreenMapper {

    private final BlockPos pos;
    private final TileEntityStoppingPositionSign entity;
    private TextFieldWidget textField;

    public ScreenStoppingPositionSign(BlockPos pos, TileEntityStoppingPositionSign entity) {
        super(Text.literal("Stopping Position Config"));
        this.pos = pos;
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();
        int w = this.width;
        int h = this.height;

        this.textField = new TextFieldWidget(textRenderer, w / 2 - 50, h / 2 - 10, 100, 20, Text.literal("Value"));
        this.textField.setMaxLength(4); // 4桁もあれば十分
        this.textField.setText(entity.getSignValue());

        // ★重要: 数字以外入力させないフィルタ
        this.textField.setChangedListener(text -> {
            if (!text.matches("^[0-9]*$")) {
                // 数字以外が含まれていたら除去する
                this.textField.setText(text.replaceAll("[^0-9]", ""));
            }
            sendPacket();
        });

        addDrawableChild(textField);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, "Number", this.width / 2, this.height / 2 - 25, 0xFFFFFF);
    }

    @Override
    public void close() {
        sendPacket();
        super.close();
    }

    private void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeString(textField.getText());
        ClientPlayNetworking.send(ModPackets.PACKET_UPDATE_STOPPING_POS, buf);
    }
}