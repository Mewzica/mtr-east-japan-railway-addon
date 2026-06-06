package com.magikal_hakase.gui;

import com.magikal_hakase.blockentities.TileEntitySpeedLimitSign;
import com.magikal_hakase.blocks.BlockSpeedLimitSign;
import com.magikal_hakase.data.EnumSpeedLimitType;
import com.magikal_hakase.network.ModPackets;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class ScreenSpeedLimitSign extends ScreenMapper {

    private final BlockPos pos;
    private final TileEntitySpeedLimitSign entity;

    private TextFieldWidget textFieldValue;
    private ButtonWidget buttonType;

    private String limitValue;
    private int typeOrdinal;

    public ScreenSpeedLimitSign(BlockPos pos, TileEntitySpeedLimitSign entity) {
        super(Text.literal("Speed Limit Sign Config"));
        this.pos = pos;
        this.entity = entity;
        this.limitValue = entity.getLimitValue();

        // 現在のタイプを取得
        EnumSpeedLimitType type = entity.getCachedState().get(BlockSpeedLimitSign.TYPE);
        this.typeOrdinal = type.ordinal();
    }

    @Override
    protected void init() {
        super.init();
        int w = this.width;
        int h = this.height;
        int startY = h / 2 - 20;

        // 1. タイプ切り替えボタン
        this.buttonType = new ButtonWidget(w / 2 - 100, startY, 200, 20, Text.literal(""), button -> {
            typeOrdinal = (typeOrdinal + 1) % EnumSpeedLimitType.values().length;
            updateButtons();
            sendPacket();
        });
        addDrawableChild(buttonType);

        // 2. 数値入力欄
        this.textFieldValue = new TextFieldWidget(textRenderer, w / 2 - 100, startY + 30, 200, 20, Text.literal("Value"));
        this.textFieldValue.setMaxLength(3); // 3桁あれば十分 (例: 130)
        this.textFieldValue.setText(limitValue);
        this.textFieldValue.setChangedListener(text -> {
            if (!text.matches("^[0-9]*$")) {
                this.textFieldValue.setText(text.replaceAll("[^0-9]", ""));
            }
            limitValue = textFieldValue.getText();
            sendPacket();
        });
        addDrawableChild(textFieldValue);

        updateButtons();
    }

    private void updateButtons() {
        EnumSpeedLimitType type = EnumSpeedLimitType.values()[typeOrdinal];
        String label = switch (type) {
            case NORMAL -> "Normal (通常)";
            case LEFT -> "Branch Left (左分岐)";
            case RIGHT -> "Branch Right (右分岐)";
        };
        buttonType.setMessage(Text.literal("Type: " + label));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, "Speed Limit", this.width / 2, this.height / 2 - 35, 0xFFFFFF);
    }

    @Override
    public void close() {
        sendPacket();
        super.close();
    }

    private void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeString(limitValue);
        buf.writeInt(typeOrdinal);
        ClientPlayNetworking.send(ModPackets.PACKET_UPDATE_SPEED_LIMIT, buf);
    }
}