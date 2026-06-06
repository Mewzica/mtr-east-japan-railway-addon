package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityJNRStationSign;
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class RenderJNRStationSign extends BlockEntityRendererMapper<TileEntityJNRStationSign> {

    private final TextRenderer textRenderer;

    public RenderJNRStationSign(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getRenderDispatcher());
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(TileEntityJNRStationSign entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;

        Direction facing = entity.getCachedState().get(HorizontalFacingBlock.FACING);

        matrices.push();
        matrices.translate(0.5, 1.25, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation()));

        // --- 表面 (Front) ---
        matrices.push();
        matrices.translate(0, 0, 0.0485); // Zファイティング防止（手前へ）
        matrices.scale(0.015F, -0.015F, 0.015F);
        // false = 表面 (左が前駅、右が次駅)
        renderFace(matrices, vertexConsumers, light, entity, false);
        matrices.pop();

        // --- 裏面 (Back) ---
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180)); // くるっと回す
        matrices.translate(0, 0, 0.0485); // 同じだけ手前（回転したので裏側）へ
        matrices.scale(0.015F, -0.015F, 0.015F);
        // true = 裏面 (左が次駅、右が前駅に入れ替わる)
        renderFace(matrices, vertexConsumers, light, entity, true);
        matrices.pop();

        matrices.pop();
    }

    // 片面を描画する共通メソッド
    private void renderFace(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, TileEntityJNRStationSign entity, boolean isBack) {
        int textColor = 0xFF000000;

        // (1) 中央・大：ひらがな駅名
        matrices.push();
        matrices.translate(0, -22, -0.1);
        matrices.scale(3.0f, 3.0f, 1.0f);
        drawTextFit(matrices, textRenderer, vertexConsumers, entity.stationHiragana, 0, 0, textColor, light, 34);
        matrices.pop();

        // (2) 中央下・小：漢字駅名
        matrices.push();
        matrices.translate(0, -6, -0.1);
        matrices.scale(1.0f, 1.0f, 1.0f);
        drawTextFit(matrices, textRenderer, vertexConsumers, entity.stationKanji, 0, 0, textColor, light, 72);
        matrices.pop();

        // (3) 漢字の下・中：英語駅名
        matrices.push();
        matrices.translate(0, 3, -0.1);
        matrices.scale(1.4f, 1.4f, 1.0f);
        drawTextFit(matrices, textRenderer, vertexConsumers, entity.stationEnglish, 0, 0, textColor, light, 50);
        matrices.pop();

        // (4) さらに下・極小：所在地
        matrices.push();
        matrices.translate(0, 13, -0.1);
        matrices.scale(0.65f, 0.65f, 1.0f);
        drawTextFit(matrices, textRenderer, vertexConsumers, entity.location, 0, 0, textColor, light, 80);
        matrices.pop();

        // (5) 下部左右：隣駅表示
        // 裏面の場合は左右（前駅・次駅）を入れ替える
        String leftHira = isBack ? entity.nextStationHiragana : entity.prevStationHiragana;
        String leftEng  = isBack ? entity.nextStationEnglish : entity.prevStationEnglish;

        String rightHira = isBack ? entity.prevStationHiragana : entity.nextStationHiragana;
        String rightEng  = isBack ? entity.prevStationEnglish : entity.nextStationEnglish;

        // 左側エリア (-25)
        matrices.push();
        matrices.translate(-25, 33, -0.1);
        matrices.scale(1.3f, 1.3f, 1.0f);
        drawTextFit(matrices, textRenderer, vertexConsumers, leftHira, 0, -3, textColor, light, 30);
        matrices.scale(0.4f, 0.4f, 1.0f);
        drawTextFit(matrices, textRenderer, vertexConsumers, leftEng, 0, 6, textColor, light, 40);
        matrices.pop();

        // 右側エリア (+27)
        matrices.push();
        matrices.translate(27, 33, -0.1);
        matrices.scale(1.3f, 1.3f, 1.0f);
        drawTextFit(matrices, textRenderer, vertexConsumers, rightHira, 0, -3, textColor, light, 30);
        matrices.scale(0.4f, 0.4f, 1.0f);
        drawTextFit(matrices, textRenderer, vertexConsumers, rightEng, 0, 6, textColor, light, 40);
        matrices.pop();
    }

    private void drawColorRect(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x1, float y1, float x2, float y2, int color, int light) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getText(new Identifier("minecraft", "textures/block/white_concrete.png")));
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 1.0f;
        buffer.vertex(matrix, x1, y2, 0).color(r, g, b, a).texture(0, 0).light(light).next();
        buffer.vertex(matrix, x2, y2, 0).color(r, g, b, a).texture(0, 0).light(light).next();
        buffer.vertex(matrix, x2, y1, 0).color(r, g, b, a).texture(0, 0).light(light).next();
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).texture(0, 0).light(light).next();
    }

    private void drawTextFit(MatrixStack matrices, TextRenderer textRenderer, VertexConsumerProvider vertexConsumers, String text, float x, float y, int color, int light, float maxWidth) {
        if (text == null || text.isEmpty()) return;
        matrices.push();
        int textWidth = textRenderer.getWidth(text);
        matrices.translate(x, y, 0);

        // デフォルト倍率
        float scaleX = 1.0f;

        // 文字幅が最大幅を超えている場合のみ、X方向の倍率を下げる（長体）
        if (textWidth > maxWidth) {
            scaleX = maxWidth / (float) textWidth;
        }

        // Y軸は1.0fのまま、X軸だけ縮める
        matrices.scale(scaleX, 1.0f, 1.0f);

        textRenderer.draw(Text.of(text), -textWidth / 2.0f, -4, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
        matrices.pop();
    }
}