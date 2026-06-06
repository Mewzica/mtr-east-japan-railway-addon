package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityStationGuideSign;
import com.magikal_hakase.blocks.BlockStationGuideSign; // Blockクラスをインポート
import com.magikal_hakase.data.GuideSignType;       // Enumをインポート
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
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

public class RenderStationGuideSign extends BlockEntityRendererMapper<TileEntityStationGuideSign> {

    public RenderStationGuideSign(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getRenderDispatcher());
    }

    @Override
    public void render(TileEntityStationGuideSign entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;

        Direction facing = entity.getCachedState().get(HorizontalFacingBlock.FACING);
        GuideSignType type = entity.getCachedState().get(BlockStationGuideSign.TYPE);

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation()));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));

        // --- 表面の描画 ---
        renderFace(matrices, vertexConsumers, light, type, false,
                entity.getBackgroundColor(), entity.getSignText(), entity.getSignTextEnglish(), entity.getIconId(), entity.getIconAlign(), entity.getLineColor(), entity.isTextBlack());

        // --- 裏面の描画 ---
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));

        renderFace(matrices, vertexConsumers, light, type, true,
                entity.getBackgroundColorBack(), entity.getSignTextBack(), entity.getSignTextEnglishBack(), entity.getIconIdBack(), entity.getIconAlignBack(), entity.getLineColorBack(), entity.isTextBlackBack());

        matrices.pop();
    }

    // 片面を描画する共通メソッド
    private void renderFace(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                            GuideSignType type, boolean isBack,
                            int bgColor, String text, String textEnglish, String iconId, int align, int lineColor, boolean isTextBlack) {

        matrices.push();

        // ブロックの端の場合、中心を少し内側にずらす
        float xShift = 0.0f;
        float shiftAmount = -0.09f; // ズらす量 (0.25ブロック分 = 約1/4) ※お好みで調整

        // 表面と裏面で「左・右」の概念が逆になるため、補正方向を逆転させる
        if (type == GuideSignType.LEFT) {
            // 左端ブロック: 表面なら右へ(+), 裏面なら左へ(-) ずらす必要がある
            // (※座標系は回転済みなので、Xプラス方向が「向かって右」とは限らない点に注意)
            // ここでは標準的なモデル座標系として設定します
            xShift = isBack ? shiftAmount : -shiftAmount;
        } else if (type == GuideSignType.RIGHT) {
            // 右端ブロック
            xShift = isBack ? -shiftAmount : shiftAmount;
        }

        matrices.translate(xShift, 0, -0.05f); // Zは手前に浮かす

        float scale = 0.03F;
        matrices.scale(scale, scale, scale);

        // 文字色・アイコン色の決定
        int contentColor = isTextBlack ? 0xFF000000 : 0xFFFFFFFF;

        // 1. 背景色の描画
        float width = 33.4f;
        float height = 32.5f;
        drawColorRect(matrices, vertexConsumers, -width/2, -height/2, width, height, bgColor, light);

        // 2. アイコンの描画
        float textXOffset = 0;
        if (iconId != null && !iconId.isEmpty() && !iconId.equals("none")) {
            float iconSize = 10;
            float iconX = 0;

            switch (align) {
                case 0: // Left (左寄せ)
                    iconX = -width / 2 + 10;
                    textXOffset = 100;         // 文字は右へずらす
                    break;

                case 1: // Center (中央)
                    iconX = -iconSize / 2;
                    textXOffset = 0;
                    break;

                case 2: // Right (右寄せ)
                    iconX = width / 2 - iconSize - 10;
                    textXOffset = -100;        // 文字は左へずらす
                    break;
            }

            boolean isLineIcon = iconId.equals("line_icon");

            // サイズ設定
            // 通常アイコンは16x16、路線バーは細長く
            float iconWidth = isLineIcon ? 4 : 27;
            float iconHeight = 27;

            // 配置ロジック (width は看板全体の幅 60)
            switch (align) {
                case 0: // Left
                    iconX = -width/2 + 5;
                    textXOffset = isLineIcon ? 5 : 10; // 文字ズレ量
                    break;
                case 1: // Center
                    iconX = -iconWidth/2;
                    textXOffset = 0;
                    break;
                case 2: // Right
                    iconX = width/2 - iconWidth - 5;
                    textXOffset = isLineIcon ? -5 : -10;
                    break;
            }

            matrices.push();
            matrices.translate(0, 0, -0.01);

            if (isLineIcon) {
                drawColorRect(matrices, vertexConsumers, iconX, -iconHeight/2, iconWidth, iconHeight, lineColor, light);
            } else {
                Identifier textureId = new Identifier("mtr-east-japan-railway-addon", "textures/block/icon_" + iconId + ".png");
                drawTextureRect(matrices, vertexConsumers, textureId, iconX, -iconHeight/2, iconWidth, iconHeight, contentColor, light);
            }

            matrices.pop();
        }

        // 3. 文字の描画
        if (text != null && !text.isEmpty()) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            matrices.push();
            boolean isNumber = text.matches("^[0-9·・･]+$");
            matrices.translate(0, 0, -0.02);

            if (textEnglish != null && !textEnglish.isEmpty()) {

                // --- メインテキスト (上段) ---
                matrices.push();
                matrices.translate(textXOffset, -6, -0.02);
                if (isNumber) {
                    matrices.scale(2.0f, 2.0f, 1.0f);
                    drawTextFit(matrices, textRenderer, vertexConsumers, text, 0, 0, contentColor, light, 8);
                } else {
                    drawTextFit(matrices, textRenderer, vertexConsumers, text, 0, 0, contentColor, light, 29);
                }
                matrices.pop();

                // --- サブテキスト (下段・英語) ---
                matrices.push();
                matrices.translate(textXOffset, 2, -0.02); // 下に移動
                float subScale = 0.5f; // サイズ
                matrices.scale(subScale, subScale, 1.0f);

                // 英語用の枠幅 (縮小してるので広めに計算: 50 / 0.6 = 約83)
                drawTextFit(matrices, textRenderer, vertexConsumers, textEnglish, 0, 0, contentColor, light, 50);
                matrices.pop();

            } else {
                matrices.translate(0, 0, -0.02);
                if (isNumber) {
                    matrices.translate(textXOffset, -13.0, 0);
                    float numberScale = 3.0f;
                    matrices.scale(numberScale, numberScale, 1.0f);
                    drawTextFit(matrices, textRenderer, vertexConsumers, text, 0, 0, contentColor, light, 8);
                } else {
                    drawTextFit(matrices, textRenderer, vertexConsumers, text, textXOffset, -4, contentColor, light, 29);
                }
            }
            matrices.pop();
        }

        matrices.pop();
    }

    private int getContrastColor(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        double luminance = 0.299 * r + 0.587 * g + 0.114 * b;
        return luminance > 128 ? 0xFF000000 : 0xFFFFFFFF;
    }

    private void drawColorRect(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float width, float height, int colorRGB, int light) {
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getText(new Identifier("minecraft", "textures/block/white_concrete.png")));
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float r = ((colorRGB >> 16) & 0xFF) / 255.0f;
        float g = ((colorRGB >> 8) & 0xFF) / 255.0f;
        float b = (colorRGB & 0xFF) / 255.0f;
        float a = 1.0f;
        buffer.vertex(matrix, x, y + height, 0).color(r, g, b, a).texture(0, 1).light(light).next();
        buffer.vertex(matrix, x + width, y + height, 0).color(r, g, b, a).texture(1, 1).light(light).next();
        buffer.vertex(matrix, x + width, y, 0).color(r, g, b, a).texture(1, 0).light(light).next();
        buffer.vertex(matrix, x, y, 0).color(r, g, b, a).texture(0, 0).light(light).next();
    }

    private void drawTextureRect(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float x, float y, float width, float height, int colorRGB, int light) {
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getText(textureId));
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float r = ((colorRGB >> 16) & 0xFF) / 255.0f;
        float g = ((colorRGB >> 8) & 0xFF) / 255.0f;
        float b = (colorRGB & 0xFF) / 255.0f;
        float a = ((colorRGB >> 24) & 0xFF) / 255.0f;
        buffer.vertex(matrix, x, y + height, 0).color(r, g, b, a).texture(0, 1).light(light).next();
        buffer.vertex(matrix, x + width, y + height, 0).color(r, g, b, a).texture(1, 1).light(light).next();
        buffer.vertex(matrix, x + width, y, 0).color(r, g, b, a).texture(1, 0).light(light).next();
        buffer.vertex(matrix, x, y, 0).color(r, g, b, a).texture(0, 0).light(light).next();
    }

    private void drawCenteredText(MatrixStack matrices, TextRenderer textRenderer, VertexConsumerProvider vertexConsumers, String text, float x, float y, int color, int light) {
        float width = textRenderer.getWidth(text);
        textRenderer.draw(Text.of(text), x - width / 2.0f, y, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
    }

    private void drawTextFit(MatrixStack matrices, TextRenderer textRenderer, VertexConsumerProvider vertexConsumers, String text, float x, float y, int color, int light, float maxWidth) {
        if (text == null || text.isEmpty()) return;
        matrices.push();
        int textWidth = textRenderer.getWidth(text);
        matrices.translate(x, y, 0);

        // 基準サイズ
        float baseScale = 1.0f;

        // X軸（横）とY軸（縦）の倍率を分けて管理
        float scaleX = baseScale;
        float scaleY = baseScale;

        // 文字幅が最大幅を超えていたら、横方向(X)だけ縮める
        if (textWidth * baseScale > maxWidth) {
            scaleX = maxWidth / (float) textWidth;
        }

        // 変更点: scale(X, Y, Z) の引数を別々に指定
        // これで「縦の高さ」は維持されたまま、横幅だけギュッと圧縮されます
        matrices.scale(scaleX, scaleY, 1.0f);

        textRenderer.draw(Text.of(text), -textWidth / 2.0f, 0, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
        matrices.pop();
    }
}