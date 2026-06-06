package com.magikal_hakase.gui;

import com.magikal_hakase.blockentities.TileEntityGuidanceBell;
import com.magikal_hakase.data.GuidanceSound;
import com.magikal_hakase.packet.PacketHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class GuidanceBellEditScreen extends Screen {

    private final BlockPos pos;
    private final TileEntityGuidanceBell entity;

    // 現在の設定値
    private GuidanceSound selectedSound;
    private int currentIntervalSeconds;

    private TextFieldWidget intervalField;

    public GuidanceBellEditScreen(BlockPos pos, TileEntityGuidanceBell entity) {
        super(Text.of("Guidance Bell Settings"));
        this.pos = pos;
        this.entity = entity;
        // 初期値をロード
        this.selectedSound = entity.soundType;
        this.currentIntervalSeconds = entity.interval / 20;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int startY = 40;

        // --- 1. 音源選択ボタンのリスト ---
        // 左側に配置
        int buttonY = startY;
        for (GuidanceSound sound : GuidanceSound.values()) {
            // 現在選択されているボタンは緑色っぽくしたいが、標準ボタンだと難しいので
            // テキストの色や活性状態で表現します
            boolean isSelected = (sound == this.selectedSound);

            ButtonWidget btn = new ButtonWidget(centerX - 160, buttonY, 140, 20, Text.of(sound.name()), (b) -> {
                // ボタンが押されたら選択状態を更新
                this.selectedSound = sound;
                // デフォルト間隔を自動入力 (ユーザー補助)
                this.intervalField.setText(String.valueOf(sound.getDefaultInterval() / 20));
                // 画面再描画（選択状態の表示更新のため）
                this.init();
            });

            // 選択されているボタンは無効化して「押されている感」を出す
            if (isSelected) {
                btn.active = false;
            }

            this.addDrawableChild(btn);
            buttonY += 24; // 次のボタンへ
        }

        // --- 2. 間隔設定 ---
        // 右側に配置
        this.intervalField = new TextFieldWidget(this.textRenderer, centerX + 20, startY + 20, 100, 20, Text.of("Interval"));
        this.intervalField.setText(String.valueOf(this.currentIntervalSeconds));
        this.intervalField.setChangedListener(text -> {
            // 数字のみ許可
            if (!text.matches("\\d*")) {
                this.intervalField.setText(text.replaceAll("[^\\d]", ""));
            }
        });
        this.addDrawableChild(this.intervalField);

        // --- 3. 保存ボタン ---
        this.addDrawableChild(new ButtonWidget(centerX - 50, this.height - 40, 100, 20, Text.of("Save & Close"), (b) -> {
            saveAndClose();
        }));

        // --- 4. 視聴ボタン ---
        this.addDrawableChild(new ButtonWidget(centerX + 20, startY + 50, 60, 20, Text.of("Test Play"), (b) -> {
            // 試し鳴らし (クライアント側のみ)
            if (this.client != null && this.client.player != null) {
                this.client.player.playSound(this.selectedSound.getSound(), 1.0f, 1.0f);
            }
        }));
    }

    private void saveAndClose() {
        // 入力値をパース
        int newInterval = 5; // エラー時のデフォルト
        try {
            newInterval = Integer.parseInt(this.intervalField.getText());
        } catch (NumberFormatException e) {
            // 無視
        }

        // パケット作成
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.pos);
        buf.writeString(this.selectedSound.name());
        buf.writeInt(newInterval);

        // サーバーへ送信
        ClientPlayNetworking.send(PacketHandler.PACKET_UPDATE_BELL, buf);

        // 画面を閉じる
        this.client.setScreen(null);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        // タイトル
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);

        // ラベル
        int centerX = this.width / 2;
        int startY = 40;

        drawTextWithShadow(matrices, this.textRenderer, Text.of("Select Sound:"), centerX - 160, startY - 12, 0xA0A0A0);
        drawTextWithShadow(matrices, this.textRenderer, Text.of("Interval (sec):"), centerX + 20, startY + 8, 0xA0A0A0);

        super.render(matrices, mouseX, mouseY, delta);
    }
}