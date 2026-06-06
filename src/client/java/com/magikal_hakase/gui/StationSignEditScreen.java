package com.magikal_hakase.gui;

import com.magikal_hakase.blockentities.TileEntityHangingStationSign;
import com.magikal_hakase.blockentities.TileEntityHangingStationSign.ArrowType;
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

public class StationSignEditScreen extends Screen {

    private final TileEntityHangingStationSign entity;
    private final BlockPos pos;

    private TextFieldWidget stationNameField;
    private TextFieldWidget stationHiraganaField;
    private TextFieldWidget stationRomajiField;
    private TextFieldWidget nextStationField;
    private TextFieldWidget nextStationRomajiField;
    private TextFieldWidget prevStationField;
    private TextFieldWidget prevStationRomajiField;
    private TextFieldWidget numberField;
    private TextFieldWidget numberPrefixField;
    private TextFieldWidget lineColorField;
    private TextFieldWidget stationColorField;

    private ArrowType currentArrowFront;
    private ArrowType currentArrowBack;
    private ButtonWidget arrowFrontButton;
    private ButtonWidget arrowBackButton;

    public StationSignEditScreen(TileEntityHangingStationSign entity) {
        super(Text.of("Edit Station Sign"));
        this.entity = entity;
        this.pos = entity.getPos();
        this.currentArrowFront = entity.arrowTypeFront;
        this.currentArrowBack = entity.arrowTypeBack;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.width / 2;
        int y = this.height / 4;

        // 駅名系
        this.addDrawableChild(stationNameField = new TextFieldWidget(textRenderer, x - 150, y, 100, 20, Text.of("Name")));
        stationNameField.setText(entity.stationName);

        this.addDrawableChild(stationHiraganaField = new TextFieldWidget(textRenderer, x - 150, y + 25, 100, 20, Text.of("Hiragana")));
        stationHiraganaField.setText(entity.stationHiragana);

        this.addDrawableChild(stationRomajiField = new TextFieldWidget(textRenderer, x - 150, y + 50, 100, 20, Text.of("Romaji")));
        stationRomajiField.setText(entity.stationRomaji);

        // 次駅・前駅
        this.addDrawableChild(nextStationField = new TextFieldWidget(textRenderer, x - 40, y, 80, 20, Text.of("Next St.")));
        nextStationField.setText(entity.nextStation);

        this.addDrawableChild(nextStationRomajiField = new TextFieldWidget(textRenderer, x + 50, y, 80, 20, Text.of("Next Roma")));
        nextStationRomajiField.setText(entity.nextStationRomaji);

        this.addDrawableChild(prevStationField = new TextFieldWidget(textRenderer, x - 40, y + 25, 80, 20, Text.of("Prev St.")));
        prevStationField.setText(entity.prevStation);

        this.addDrawableChild(prevStationRomajiField = new TextFieldWidget(textRenderer, x + 50, y + 25, 80, 20, Text.of("Prev Roma")));
        prevStationRomajiField.setText(entity.prevStationRomaji);

        // ナンバリング・色
        this.addDrawableChild(numberPrefixField = new TextFieldWidget(textRenderer, x - 150, y + 80, 40, 20, Text.of("Prefix")));
        numberPrefixField.setText(entity.stationNumberPrefix);

        this.addDrawableChild(numberField = new TextFieldWidget(textRenderer, x - 105, y + 80, 55, 20, Text.of("Num")));
        numberField.setText(entity.stationNumber);

        this.addDrawableChild(lineColorField = new TextFieldWidget(textRenderer, x - 40, y + 80, 60, 20, Text.of("Line Color")));
        lineColorField.setText(Integer.toHexString(entity.lineColor).toUpperCase());

        this.addDrawableChild(stationColorField = new TextFieldWidget(textRenderer, x + 30, y + 80, 60, 20, Text.of("Station Color")));
        stationColorField.setText(Integer.toHexString(entity.stationColor).toUpperCase());

        // 矢印ボタン
        this.addDrawableChild(arrowFrontButton = new ButtonWidget(x - 150, y + 105, 120, 20, getArrowButtonText("Front", currentArrowFront), button -> {
            this.currentArrowFront = this.currentArrowFront.next();
            button.setMessage(getArrowButtonText("Front", currentArrowFront));
        }));

        this.addDrawableChild(arrowBackButton = new ButtonWidget(x - 20, y + 105, 120, 20, getArrowButtonText("Back", currentArrowBack), button -> {
            this.currentArrowBack = this.currentArrowBack.next();
            button.setMessage(getArrowButtonText("Back", currentArrowBack));
        }));

        // 保存ボタン
        this.addDrawableChild(new ButtonWidget(x - 50, y + 135, 100, 20, Text.of("Save"), button -> {
            saveAndClose();
        }));
    }

    private Text getArrowButtonText(String label, ArrowType type) {
        String arrow = switch (type) {
            case NONE -> "None";
            case LEFT -> "<--";
            case RIGHT -> "-->";
            case BOTH -> "<-->";
        };
        return Text.of(label + ": " + arrow);
    }

    private void saveAndClose() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeString(stationNameField.getText());
        buf.writeString(stationHiraganaField.getText());
        buf.writeString(stationRomajiField.getText());
        buf.writeString(nextStationField.getText());
        buf.writeString(nextStationRomajiField.getText());
        buf.writeString(prevStationField.getText());
        buf.writeString(prevStationRomajiField.getText());
        buf.writeString(numberField.getText());
        buf.writeString(numberPrefixField.getText());
        buf.writeInt(parseHexColor(lineColorField.getText()));
        buf.writeInt(parseHexColor(stationColorField.getText()));

        // ▼▼▼ 矢印データを送信 ▼▼▼
        buf.writeInt(currentArrowFront.ordinal());
        buf.writeInt(currentArrowBack.ordinal());

        ClientPlayNetworking.send(ModPackets.PACKET_UPDATE_SIGN, buf);
        this.close();
    }

    private int parseHexColor(String hex) {
        try {
            return (int) Long.parseLong(hex.replace("#", ""), 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        int x = this.width / 2;
        int y = this.height / 4;
        drawCenteredText(matrices, textRenderer, "Station Name", x - 100, y - 10, 0xFFFFFF);
        drawCenteredText(matrices, textRenderer, "Next / Prev Station", x + 50, y - 10, 0xFFFFFF);
        drawCenteredText(matrices, textRenderer, "Number / Colors / Arrows", x, y + 70, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}