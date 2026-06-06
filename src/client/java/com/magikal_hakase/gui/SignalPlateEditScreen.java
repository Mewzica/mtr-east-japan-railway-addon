package com.magikal_hakase.gui;

import com.magikal_hakase.blockentities.TileEntitySignalPlate;
import com.magikal_hakase.network.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignalPlateEditScreen extends Screen {

    private final TileEntitySignalPlate entity;
    private final BlockPos pos;
    private TextFieldWidget messageField;

    public SignalPlateEditScreen(TileEntitySignalPlate entity) {
        super(Text.of("Edit Signal Plate"));
        this.entity = entity;
        this.pos = entity.getPos();
    }

    @Override
    protected void init() {
        super.init();
        int x = this.width / 2;
        int y = this.height / 2;

        this.addDrawableChild(messageField = new TextFieldWidget(textRenderer, x - 100, y - 20, 200, 20, Text.of("Message")));
        messageField.setText(entity.message);
        messageField.setMaxLength(16); // あまり長くてもはみ出るので制限

        this.addDrawableChild(new ButtonWidget(x - 50, y + 20, 100, 20, Text.of("Save"), button -> {
            saveAndClose();
        }));
    }

    private void saveAndClose() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeString(messageField.getText());
        ClientPlayNetworking.send(ModPackets.PACKET_UPDATE_SIGNAL_PLATE, buf);
        this.close();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, textRenderer, "Signal Plate Text (Vertical)", this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
