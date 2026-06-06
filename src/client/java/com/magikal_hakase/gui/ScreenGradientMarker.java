package com.magikal_hakase.gui;

import com.magikal_hakase.blockentities.TileEntityGradientMarker;
import com.magikal_hakase.network.ModPackets;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class ScreenGradientMarker extends ScreenMapper {

    private final BlockPos pos;
    private final TileEntityGradientMarker entity;

    private TextFieldWidget textFieldValue;
    private ButtonWidget buttonSide;
    private ButtonWidget buttonType;

    private boolean isEditingBack = false;

    private int typeFront, typeBack;
    private String valueFront, valueBack;
    private int align; // 0=Right, 1=Left

    public ScreenGradientMarker(BlockPos pos, TileEntityGradientMarker entity) {
        super(Text.literal("Gradient Marker Config"));
        this.pos = pos;
        this.entity = entity;

        this.typeFront = entity.getTypeFront();
        this.valueFront = entity.getValueFront();
        this.typeBack = entity.getTypeBack();
        this.valueBack = entity.getValueBack();

    }

    @Override
    protected void init() {
        super.init();
        int w = this.width;
        int startY = this.height / 2 - 50; // 少し上に調整

        // 2. 表裏切り替え
        this.buttonSide = new ButtonWidget(w / 2 - 100, startY + 25, 200, 20, Text.literal(""), button -> {
            saveTemp();
            isEditingBack = !isEditingBack;
            loadTemp();
            updateWidgets();
        });
        addDrawableChild(buttonSide);

        // 3. タイプ切り替え
        this.buttonType = new ButtonWidget(w / 2 - 100, startY + 50, 200, 20, Text.literal(""), button -> {
            if (isEditingBack) typeBack = (typeBack + 1) % 3;
            else typeFront = (typeFront + 1) % 3;
            updateWidgets();
            sendPacket();
        });
        addDrawableChild(buttonType);

        // 4. 数値入力
        this.textFieldValue = new TextFieldWidget(textRenderer, w / 2 - 100, startY + 75, 200, 20, Text.literal("Value"));
        this.textFieldValue.setMaxLength(5);
        this.textFieldValue.setChangedListener(text -> {
            saveTemp();
            sendPacket();
        });
        addDrawableChild(textFieldValue);

        loadTemp();
        updateWidgets();
    }

    private void updateWidgets() {

        // 表裏ボタン
        buttonSide.setMessage(Text.literal("Editing: " + (isEditingBack ? "BACK (裏面)" : "FRONT (表面)")));

        // タイプボタン
        int currentType = isEditingBack ? typeBack : typeFront;
        String typeText = switch (currentType) {
            case 0 -> "UP (上り)";
            case 1 -> "DOWN (下り)";
            case 2 -> "LEVEL (水平)";
            default -> "LEVEL";
        };
        buttonType.setMessage(Text.literal("Type: " + typeText));

        if (currentType == 2) textFieldValue.setEditable(false);
        else textFieldValue.setEditable(true);
    }

    private void saveTemp() {
        if (isEditingBack) this.valueBack = textFieldValue.getText();
        else this.valueFront = textFieldValue.getText();
    }

    private void loadTemp() {
        if (isEditingBack) textFieldValue.setText(valueBack);
        else textFieldValue.setText(valueFront);
    }

    @Override
    public void close() {
        saveTemp();
        sendPacket();
        super.close();
    }

    private void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeInt(typeFront);
        buf.writeString(valueFront);
        buf.writeInt(typeBack);
        buf.writeString(valueBack);
        buf.writeInt(align);
        ClientPlayNetworking.send(ModPackets.PACKET_UPDATE_GRADIENT_MARKER, buf);
    }
}