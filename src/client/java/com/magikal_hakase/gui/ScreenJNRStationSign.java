package com.magikal_hakase.gui;

import com.magikal_hakase.blockentities.TileEntityJNRStationSign;
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

public class ScreenJNRStationSign extends ScreenMapper {

    private final BlockPos pos;
    private final TileEntityJNRStationSign entity;

    private TextFieldWidget fieldHiragana, fieldKanji, fieldEnglish, fieldLocation;
    private TextFieldWidget fieldPrevHira, fieldPrevEng;
    private TextFieldWidget fieldNextHira, fieldNextEng;

    public ScreenJNRStationSign(BlockPos pos, TileEntityJNRStationSign entity) {
        super(Text.literal("JNR Sign Config"));
        this.pos = pos;
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();
        int w = this.width;
        int h = this.height;
        int x = w / 2;
        int y = h / 6;

        // 中央：自駅情報
        addDrawableChild(fieldHiragana = createField(x - 100, y, 200, entity.stationHiragana));
        addDrawableChild(fieldKanji = createField(x - 100, y + 25, 200, entity.stationKanji));
        addDrawableChild(fieldEnglish = createField(x - 100, y + 50, 200, entity.stationEnglish));
        addDrawableChild(fieldLocation = createField(x - 100, y + 75, 200, entity.location));

        // 左：前駅
        addDrawableChild(fieldPrevHira = createField(x - 150, y + 110, 90, entity.prevStationHiragana));
        addDrawableChild(fieldPrevEng = createField(x - 150, y + 135, 90, entity.prevStationEnglish));

        // 右：次駅
        addDrawableChild(fieldNextHira = createField(x + 60, y + 110, 90, entity.nextStationHiragana));
        addDrawableChild(fieldNextEng = createField(x + 60, y + 135, 90, entity.nextStationEnglish));

        // 保存ボタン
        addDrawableChild(new ButtonWidget(x - 40, h - 30, 80, 20, Text.literal("Save"), button -> sendPacket()));
    }

    private TextFieldWidget createField(int x, int y, int width, String text) {
        TextFieldWidget widget = new TextFieldWidget(textRenderer, x, y, width, 20, Text.literal(""));
        widget.setText(text);
        widget.setMaxLength(64);
        return widget;
    }

    private void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeString(fieldHiragana.getText());
        buf.writeString(fieldKanji.getText());
        buf.writeString(fieldEnglish.getText());
        buf.writeString(fieldLocation.getText());
        buf.writeString(fieldPrevHira.getText());
        buf.writeString(fieldPrevEng.getText());
        buf.writeString(fieldNextHira.getText());
        buf.writeString(fieldNextEng.getText());
        ClientPlayNetworking.send(ModPackets.PACKET_UPDATE_JNR_SIGN, buf);
        this.close();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, "JNR Station Sign", width / 2, 10, 0xFFFFFF);

        // ラベル
        int x = width / 2;
        int y = height / 6;
        textRenderer.draw(matrices, "Main (Hiragana)", x + 110, y + 6, 0xAAAAAA);
        textRenderer.draw(matrices, "Kanji", x + 110, y + 31, 0xAAAAAA);
        textRenderer.draw(matrices, "English", x + 110, y + 56, 0xAAAAAA);
        textRenderer.draw(matrices, "Location", x + 110, y + 81, 0xAAAAAA);

        drawCenteredText(matrices, textRenderer, "Prev Station", x - 105, y + 100, 0xAAAAAA);
        drawCenteredText(matrices, textRenderer, "Next Station", x + 105, y + 100, 0xAAAAAA);
    }
}