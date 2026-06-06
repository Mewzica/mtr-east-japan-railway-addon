package com.magikal_hakase.gui;

import com.magikal_hakase.blockentities.TileEntityStationGuideSign;
import com.magikal_hakase.network.ModPackets;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

public class ScreenStationGuideSign extends ScreenMapper {

    private final BlockPos pos;
    private final TileEntityStationGuideSign entity;

    private TextFieldWidget textFieldColor;
    private TextFieldWidget textFieldText;
    private TextFieldWidget textFieldTextEnglish;
    private ButtonWidget buttonIcon;
    private ButtonWidget buttonSideToggle;
    private ButtonWidget buttonAlign; // 配置切り替えボタン
    private ButtonWidget buttonTextColor;

    // 編集中のデータ保持用
    private boolean isEditingBack = false;

    // データ一時保存用
    private int colorFront, colorBack;
    private String textFront, textBack;
    private String textEnglishFront, textEnglishBack;
    private String iconFront, iconBack;
    private int alignFront, alignBack; // 配置データ
    private TextFieldWidget textFieldLineColor;
    private int currentLineColorFront, currentLineColorBack;
    private boolean isTextBlackFront;
    private boolean isTextBlackBack;

    private static final List<String> ICONS = Arrays.asList(
            "none", "line_icon", "arrow_straight", "arrow_down", "arrow_left", "arrow_right", "arrow_top_left", "arrow_top_right", "arrow_bottom_left", "arrow_bottom_right",
            "tickets", "train", "shinkansen", "bus", "stairs", "escalator_up", "escalator_down", "elevator", "restroom"
    );

    public ScreenStationGuideSign(BlockPos pos, TileEntityStationGuideSign entity, boolean isBack) {
        super(Text.literal("Station Guide Sign Config"));
        this.pos = pos;
        this.entity = entity;

        this.isEditingBack = isBack;

        // 初期データロード
        this.colorFront = entity.getBackgroundColor();
        this.textFront = entity.getSignText();
        this.textEnglishFront = entity.getSignTextEnglish();
        this.iconFront = entity.getIconId();
        this.alignFront = entity.getIconAlign();
        this.currentLineColorFront = entity.getLineColor();
        this.isTextBlackFront = entity.isTextBlack();

        this.colorBack = entity.getBackgroundColorBack();
        this.textBack = entity.getSignTextBack();
        this.textEnglishBack = entity.getSignTextEnglishBack();
        this.iconBack = entity.getIconIdBack();
        this.alignBack = entity.getIconAlignBack();
        this.currentLineColorBack = entity.getLineColorBack();
        this.isTextBlackBack = entity.isTextBlackBack();
    }

    @Override
    protected void init() {
        super.init();
        int w = this.width;
        int h = this.height;
        int startY = h / 4;

        // 1. 表/裏 切り替えボタン
        this.buttonSideToggle = new ButtonWidget(w / 2 - 100, startY - 50, 200, 20, Text.literal(""), button -> {
            saveCurrentStateToTemp();
            isEditingBack = !isEditingBack;
            loadStateFromTemp();
            updateButtons();
        });
        addDrawableChild(buttonSideToggle);

        // 2. 色入力欄
        this.textFieldColor = new TextFieldWidget(textRenderer, w / 2 - 100, startY - 20, 135, 20, Text.literal("Color"));
        this.textFieldColor.setMaxLength(8);
        this.textFieldColor.setText(Integer.toHexString(isEditingBack ? colorBack : colorFront).toUpperCase());
        this.textFieldColor.setChangedListener(text -> {
            saveCurrentStateToTemp();
            sendPacket();
        });
        addDrawableChild(textFieldColor);

        // [白] ボタン
        addDrawableChild(new ButtonWidget(w / 2 + 40, startY - 20, 20, 20, Text.literal(""), button -> applyPresetColor(0xFFFFFF)) {
            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                int outline = this.isHovered() ? 0xFFFFFFA0 : 0xFFAAAAAA;
                fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, outline);
                fill(matrices, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, 0xFFFFFFFF);
            }
        });

        // [黒] ボタン
        addDrawableChild(new ButtonWidget(w / 2 + 65, startY - 20, 20, 20, Text.literal(""), button -> applyPresetColor(0x141414)) {
            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                int outline = this.isHovered() ? 0xFFFFFFA0 : 0xFFAAAAAA;
                fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, outline);
                fill(matrices, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, 0xFF141414);
            }
        });

        // [黄] ボタン
        addDrawableChild(new ButtonWidget(w / 2 + 90, startY - 20, 20, 20, Text.literal(""), button -> applyPresetColor(0xFFCC00)) {
            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                int outline = this.isHovered() ? 0xFFFFFFA0 : 0xFFAAAAAA;
                fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, outline);
                fill(matrices, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, 0xFFFFCC00);
            }
        });

        // 3. テキスト入力欄
        this.textFieldText = new TextFieldWidget(textRenderer, w / 2 - 100, startY + 10, 200, 20, Text.literal("Sign Text"));
        this.textFieldText.setMaxLength(64);
        this.textFieldText.setText(isEditingBack ? textBack : textFront);
        this.textFieldText.setChangedListener(text -> {
            saveCurrentStateToTemp();
            sendPacket();
        });
        addDrawableChild(textFieldText);

        this.textFieldTextEnglish = new TextFieldWidget(textRenderer, w / 2 - 100, startY + 40, 200, 20, Text.literal("Sub Text"));
        this.textFieldTextEnglish.setMaxLength(64);
        this.textFieldTextEnglish.setText(isEditingBack ? textEnglishBack : textEnglishFront);
        this.textFieldTextEnglish.setChangedListener(text -> { saveCurrentStateToTemp(); sendPacket(); });
        addDrawableChild(textFieldTextEnglish);

        // 4. アイコン選択ボタン
        this.buttonIcon = new ButtonWidget(w / 2 - 100, startY + 80, 200, 20, Text.literal(""), button -> {
            // ★左クリック時の動作 (通常通り次へ)
            cycleIcon(true);
            updateVisibility();
        }) {
            // ★ここが重要: マウスクリックイベントを割り込み処理する
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                // ボタンが有効で、かつマウスカーソルがボタンの上にある場合
                if (this.active && this.visible && this.clicked(mouseX, mouseY)) {
                    if (button == 1) { // button 1 = 右クリック
                        // 音を鳴らす
                        this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                        // 前へ戻る処理
                        cycleIcon(false);
                        updateVisibility();
                        return true; // イベント処理完了
                    }
                }
                // 左クリック(button 0)などは親クラス(super)に任せて、上のラムダ式を実行させる
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
        addDrawableChild(buttonIcon);

        // 5. 配置切り替えボタン
        this.buttonAlign = new ButtonWidget(w / 2 - 100, startY + 105, 200, 20, Text.literal(""), button -> {
            if (isEditingBack) {
                alignBack = (alignBack + 1) % 3;
            } else {
                alignFront = (alignFront + 1) % 3;
            }
            updateButtons();
            sendPacket();
        });
        addDrawableChild(buttonAlign);

        this.textFieldLineColor = new TextFieldWidget(textRenderer, w / 2 + 110, startY + 80, 60, 20, Text.literal("Line Color"));
        this.textFieldLineColor.setMaxLength(8);
        this.textFieldLineColor.setText(Integer.toHexString(isEditingBack ? currentLineColorBack : currentLineColorFront).toUpperCase());
        this.textFieldLineColor.setChangedListener(text -> {
            saveCurrentStateToTemp();
            sendPacket();
        });
        addDrawableChild(textFieldLineColor);

        this.buttonTextColor = new ButtonWidget(w / 2 + 110, startY + 10, 80, 20, Text.literal(""), button -> {
            if (isEditingBack) {
                isTextBlackBack = !isTextBlackBack;
            } else {
                isTextBlackFront = !isTextBlackFront;
            }
            updateButtons();
            sendPacket();
        });
        addDrawableChild(buttonTextColor);

        updateButtons();
        updateVisibility();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        // 各欄の左横に説明文を描画
        int w = this.width;
        int h = this.height;
        int startY = h / 4;

        // テキストフィールドの右端座標 + 10
        int labelX = w / 2 - 190;

        drawTextWithShadow(matrices, textRenderer, Text.literal("背景色 (Hex) →"), labelX + 33, startY - 20 + 6, 0xFFFFFF);
        drawTextWithShadow(matrices, textRenderer, Text.literal("主テキスト (日本語) →"), labelX, startY + 10 + 6, 0xFFFFFF);
        drawTextWithShadow(matrices, textRenderer, Text.literal("副テキスト (English) →"), labelX, startY + 40 + 6, 0xFFFFFF);

        // アイコンボタンの説明もあってもいいかもしれません
        // drawTextWithShadow(matrices, textRenderer, Text.literal("← アイコン選択"), labelX, startY + 80 + 6, 0xFFFFFF);
    }

    // saveCurrentStateToTemp, loadStateFromTemp は変更なし（前回のままでOK）
    private void saveCurrentStateToTemp() {
        if (isEditingBack) {
            this.textBack = textFieldText.getText();
            this.textEnglishBack = textFieldTextEnglish.getText();
        } else {
            this.textFront = textFieldText.getText();
            this.textEnglishFront = textFieldTextEnglish.getText();
        }

        try {
            String hex = textFieldLineColor.getText().replace("#", "");
            if (hex.length() == 6) hex = "FF" + hex;
            int color = (int) Long.parseLong(hex, 16);
            if (isEditingBack) this.currentLineColorBack = color;
            else this.currentLineColorFront = color;
        } catch (NumberFormatException e) {}

        try {
            String hex = textFieldColor.getText().replace("#", "");
            if (hex.length() == 6) hex = "FF" + hex;
            int color = (int) Long.parseLong(hex, 16);
            if (isEditingBack) this.colorBack = color;
            else this.colorFront = color;
        } catch (NumberFormatException e) {}
    }

    private void loadStateFromTemp() {
        if (isEditingBack) {
            textFieldColor.setText(Integer.toHexString(colorBack).toUpperCase());
            textFieldText.setText(textBack);
            textFieldTextEnglish.setText(textEnglishBack);
        } else {
            textFieldColor.setText(Integer.toHexString(colorFront).toUpperCase());
            textFieldText.setText(textFront);
            textFieldTextEnglish.setText(textEnglishFront);
        }

        if (isEditingBack) {
            textFieldLineColor.setText(Integer.toHexString(currentLineColorBack).toUpperCase());
        } else {
            textFieldLineColor.setText(Integer.toHexString(currentLineColorFront).toUpperCase());
        }
        updateVisibility();
    }

    private void updateVisibility() {
        String currentIcon = isEditingBack ? iconBack : iconFront;
        // アイコンが "line_icon" の時だけ表示かつ編集可能にする
        boolean isLineIcon = "line_icon".equals(currentIcon);

        this.textFieldLineColor.setVisible(isLineIcon);
        this.textFieldLineColor.setEditable(isLineIcon);
    }

    // ボタン表示更新
    private void updateButtons() {
        buttonSideToggle.setMessage(Text.literal("Editing: " + (isEditingBack ? "BACK (裏)" : "FRONT (表)")));
        buttonIcon.setMessage(Text.literal("Icon: " + (isEditingBack ? iconBack : iconFront)));

        int currentAlign = isEditingBack ? alignBack : alignFront;
        String alignText = switch(currentAlign) {
            case 0 -> "Left";
            case 1 -> "Center";
            case 2 -> "Right";
            default -> "Left";
        };
        buttonAlign.setMessage(Text.literal("Align: " + alignText));

        boolean currentBlack = isEditingBack ? isTextBlackBack : isTextBlackFront;
        buttonTextColor.setMessage(Text.literal("Text: " + (currentBlack ? "Black" : "White")));
    }

    private void cycleIcon(boolean forward) {
        String current = isEditingBack ? iconBack : iconFront;
        int index = ICONS.indexOf(current);

        if (forward) {
            // 次へ: (index + 1) % size
            int nextIndex = (index + 1) % ICONS.size();
            String nextIcon = ICONS.get(nextIndex);
            if (isEditingBack) iconBack = nextIcon;
            else iconFront = nextIcon;
        } else {
            // 前へ: (index - 1 + size) % size  (負の数にならないようにsizeを足す)
            int prevIndex = (index - 1 + ICONS.size()) % ICONS.size();
            String prevIcon = ICONS.get(prevIndex);
            if (isEditingBack) iconBack = prevIcon;
            else iconFront = prevIcon;
        }

        updateButtons(); // ボタンの文字更新
        sendPacket();    // 即送信
    }

    private void applyPresetColor(int color) {
        // 16進数文字列に変換 (0パディングなどはTextFieldが勝手に処理するが、念のためUpperCaseに)
        String hex = Integer.toHexString(color).toUpperCase();

        // テキストフィールドを更新
        textFieldColor.setText(hex);

        // 保存処理とパケット送信を実行
        saveCurrentStateToTemp();
        sendPacket();
    }

    private void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeInt(colorFront);
        buf.writeString(textFront);
        buf.writeString(textEnglishFront);
        buf.writeString(iconFront);
        buf.writeInt(alignFront);
        buf.writeInt(currentLineColorFront);
        buf.writeBoolean(isTextBlackFront);
        buf.writeInt(colorBack);
        buf.writeString(textBack);
        buf.writeString(textEnglishBack);
        buf.writeString(iconBack);
        buf.writeInt(alignBack);
        buf.writeInt(currentLineColorBack);
        buf.writeBoolean(isTextBlackBack);
        ClientPlayNetworking.send(ModPackets.PACKET_UPDATE_GUIDE_SIGN, buf);
    }
}