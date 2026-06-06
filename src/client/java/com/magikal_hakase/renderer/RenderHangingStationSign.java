package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityHangingStationSign;
import com.magikal_hakase.blockentities.TileEntityHangingStationSign.ArrowType;
import com.magikal_hakase.blocks.BlockHangingStationSign;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class RenderHangingStationSign implements BlockEntityRenderer<TileEntityHangingStationSign> {

    private final TextRenderer textRenderer;
    private static final float SCALE = 0.015F;

    public RenderHangingStationSign(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(TileEntityHangingStationSign entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;
        Direction facing = entity.getCachedState().get(BlockHangingStationSign.FACING);

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation()));

        // --- 表面 ---
        matrices.push();
        matrices.translate(0, 0, -0.05);
        renderSignFace(entity, matrices, vertexConsumers, light, false);
        matrices.pop();

        // --- 裏面 ---
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        matrices.translate(0, 0, -0.05);
        renderSignFace(entity, matrices, vertexConsumers, light, true);
        matrices.pop();

        matrices.pop();
    }

    private void renderSignFace(TileEntityHangingStationSign entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean isBackSide) {
        matrices.push();
        matrices.scale(-SCALE, -SCALE, SCALE);

        String centerName = entity.stationName;
        String centerHira = entity.stationHiragana;
        String centerRoma = entity.stationRomaji;

        String leftName = isBackSide ? entity.nextStation : entity.prevStation;
        String leftRoma = isBackSide ? entity.nextStationRomaji : entity.prevStationRomaji;
        String rightName = isBackSide ? entity.prevStation : entity.nextStation;
        String rightRoma = isBackSide ? entity.prevStationRomaji : entity.nextStationRomaji;

        int color = entity.lineColor;
        int stationColor = entity.stationColor;

        ArrowType arrowType = isBackSide ? entity.arrowTypeBack : entity.arrowTypeFront;

        boolean isBright = isBrightColor(color);
        int bandTextColor = isBright ? 0x000000 : 0xFFFFFF;

        // --- 1. ラインカラー帯 ---
        float bandY = 12.0f;
        float bandHeight = 8.0f;
        float width = 82.0f;
        drawArrowBand(matrices, vertexConsumers, -width, bandY, width, bandY + bandHeight, color, light, arrowType);

        // --- 2. 駅カラー (中央) ---
        matrices.push();
        matrices.translate(0, 0, -0.001f);
        float stationColorWidth = 8.0f;
        drawColorRect(matrices, vertexConsumers, -stationColorWidth/2, bandY, stationColorWidth/2, bandY + bandHeight, stationColor, light);
        matrices.pop();

        // --- 3. 文字描画 (drawTextFitに変更) ---
        matrices.push();
        matrices.translate(0, 0, -0.002f);

        // ★中央駅名
        // maxWidth: 中央エリアの幅 (隣駅名と被らない程度、約90)
        drawTextFit(matrices, vertexConsumers, centerName, 0, -14, 0x000000, light, 80.0f, 2.0f); // 漢字
        drawTextFit(matrices, vertexConsumers, centerHira, 0, 5, 0x000000, light, 85.0f, 0.7f);   // ひらがな
        drawTextFit(matrices, vertexConsumers, centerRoma, 0, 22, 0x000000, light, 90.0f, 0.8f);  // Romaji

        // ★隣駅名
        float sideX = 55.0f;
        // maxWidth: 端のエリアの幅 (約50)
        drawTextFit(matrices, vertexConsumers, leftName, -sideX, 13, bandTextColor, light, 45.0f, 0.7f);
        drawTextFit(matrices, vertexConsumers, leftRoma, -sideX, 21, 0x000000, light, 45.0f, 0.5f);
        drawTextFit(matrices, vertexConsumers, rightName, sideX, 13, bandTextColor, light, 45.0f, 0.7f);
        drawTextFit(matrices, vertexConsumers, rightRoma, sideX, 21, 0x000000, light, 45.0f, 0.5f);

        matrices.pop();

        // --- 4. ナンバリング ---
        matrices.push();
        float numX = -48.0f;
        float numY = -5.0f;
        matrices.translate(0, 0, -0.005f);
        drawColorRect(matrices, vertexConsumers, numX - 6, numY - 6, numX + 6, numY + 6, 0x222222, light);
        matrices.translate(0, 0, -0.001f);
        drawColorRect(matrices, vertexConsumers, numX - 5, numY - 5, numX + 5, numY + 5, 0xFFFFFF, light);
        matrices.translate(0, 0, -0.001f);
        drawColorRect(matrices, vertexConsumers, numX - 5, numY - 5, numX + 5, numY - 1, stationColor, light);
        matrices.translate(0, 0, -0.001f);

        // ナンバリング文字もFit対応 (枠内に収めるため maxWidth: 9 程度)
        drawTextFit(matrices, vertexConsumers, entity.stationNumberPrefix, numX, numY - 5, 0xFFFFFF, light, 9.0f, 0.45f);
        drawTextFit(matrices, vertexConsumers, entity.stationNumber, numX, numY + 0, 0x000000, light, 9.0f, 0.6f);

        matrices.pop();
        matrices.pop();
    }

    private void drawTextFit(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String text, float x, float y, int color, int light, float maxWidth, float maxScale) {
        if (text == null || text.isEmpty()) return;
        matrices.push();
        int textWidth = textRenderer.getWidth(text);
        matrices.translate(x, y, 0);

        // 縦（高さ）は常に指定された倍率をキープ
        float scaleY = maxScale;

        // 横（幅）は、基準倍率で枠からはみ出る場合のみ縮小する
        float scaleX = maxScale;
        if (textWidth * scaleX > maxWidth) {
            scaleX = maxWidth / (float) textWidth;
        }

        // ★ここが変更点: XとYに別々の倍率を適用 (Zは1.0)
        matrices.scale(scaleX, scaleY, 1.0f);

        // 中心基準で描画
        textRenderer.draw(Text.of(text), -textWidth / 2.0f, 0, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
        matrices.pop();
    }

    private void drawArrowBand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x1, float y1, float x2, float y2, int color, int light, ArrowType type) {
        float arrowDepth = (y2 - y1) / 2.0f;
        float rectX1 = x1;
        float rectX2 = x2;

        if (type == ArrowType.LEFT || type == ArrowType.BOTH) rectX1 += arrowDepth;
        if (type == ArrowType.RIGHT || type == ArrowType.BOTH) rectX2 -= arrowDepth;

        drawColorRect(matrices, vertexConsumers, rectX1, y1, rectX2, y2, color, light);

        if (type == ArrowType.LEFT || type == ArrowType.BOTH) {
            float centerY = (y1 + y2) / 2.0f;
            drawTriangleAsRect(matrices, vertexConsumers, x1, centerY, rectX1, y2, rectX1, y1, color, light);
        }
        if (type == ArrowType.RIGHT || type == ArrowType.BOTH) {
            float centerY = (y1 + y2) / 2.0f;
            drawTriangleAsRect(matrices, vertexConsumers, rectX2, y2, x2, centerY, rectX2, y1, color, light);
        }
    }

    private void drawTriangleAsRect(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float v1x, float v1y, float v2x, float v2y, float v3x, float v3y, int color, int light) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getText(new Identifier("minecraft", "textures/block/white_concrete.png")));
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 1.0f;
        buffer.vertex(matrix, v1x, v1y, 0).color(r, g, b, a).texture(0, 0.5f).light(light).next();
        buffer.vertex(matrix, v2x, v2y, 0).color(r, g, b, a).texture(1, 1).light(light).next();
        buffer.vertex(matrix, v3x, v3y, 0).color(r, g, b, a).texture(1, 0).light(light).next();
        buffer.vertex(matrix, v1x, v1y, 0).color(r, g, b, a).texture(0, 0.5f).light(light).next();
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

    private boolean isBrightColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        double luma = 0.299 * r + 0.587 * g + 0.114 * b;
        return luma > 140;
    }
}