package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityGradientMarker;
import com.magikal_hakase.blocks.BlockGradientMarker;
import com.magikal_hakase.data.EnumGradientType;
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class RenderGradientMarker extends BlockEntityRendererMapper<TileEntityGradientMarker> {

    public RenderGradientMarker(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getRenderDispatcher());
    }

    @Override
    public void render(TileEntityGradientMarker entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;

        Direction facing = entity.getCachedState().get(HorizontalFacingBlock.FACING);
        EnumGradientType frontType = entity.getCachedState().get(BlockGradientMarker.FRONT_TYPE);
        EnumGradientType backType = entity.getCachedState().get(BlockGradientMarker.BACK_TYPE);

        matrices.push();
        matrices.translate(0.5, 0, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation() + 180));

        // --- 表面 (Z+) ---
        matrices.push();
        // モデルの厚みに合わせて手前に出す (0.16)
        matrices.translate(0, 0.9, -0.0685);
        // 表面描画 (isBack = false)
        renderText(matrices, vertexConsumers, light, frontType, entity.getValueFront(), false);
        matrices.pop();

        // --- 裏面 (Z-) ---
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        // モデルの厚みに合わせて手前に出す (0.16)
        matrices.translate(0, 0.9, -0.0685);
        // 裏面描画 (isBack = true)
        renderText(matrices, vertexConsumers, light, backType, entity.getValueBack(), true);
        matrices.pop();

        matrices.pop();
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EnumGradientType type, String value, boolean isBack) {
        matrices.push();

        // 初期値
        float angle = 0.0f;
        float xShift = 0.0f;
        float yShift = 0.0f;

        // ★ここでタイプごとの位置と角度を設定
        // ※「数値が2倍になる」現象を防ぐため、他の場所に matrices.multiply が残っていないか確認してください
        if (type == EnumGradientType.UP) {
            angle = 202.5f;
            xShift = -0.29f;
            yShift = 0.205f;
        }
        else if (type == EnumGradientType.DOWN) {
            angle = -202.5f;
            xShift = -0.48f;
            yShift = 0.2f;
        }
        else if (type == EnumGradientType.LEVEL) {
            angle = 180;
            xShift = -0.4f;
            yShift = 0.225f;
        }

        // 裏面補正 (反転処理)
        // 裏面はY軸で180度回転しているため、X座標と角度のプラスマイナスを逆転させる必要があります
        if (isBack) {
            angle = -angle;
            xShift = -xShift; // X座標も反転
        }

        // 1. 回転 (Z軸)
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(angle));

        // 2. 移動 (回転した軸に対して移動)
        // Z=-0.01f は文字浮かせ用
        matrices.translate(xShift, yShift, -0.01);

        // 文字の決定
        String text = (type == EnumGradientType.LEVEL) ? "L" : value;

        // 文字色 (黒)
        int color = 0xFF000000;

        // デバッグ用: もしLEVELの位置調整を確認したい場合、ここをコメントアウト解除して赤色にしてみてください
        // if (type == EnumGradientType.LEVEL) color = 0xFFFF0000;

        float scale = 0.02f;
        matrices.scale(scale, scale, scale);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // 文字描画 (中心基準)
        drawTextFit(matrices, textRenderer, vertexConsumers, text, 0, -4, color, light, 9);

        matrices.pop();
    }

    private void drawTextFit(MatrixStack matrices, TextRenderer textRenderer, VertexConsumerProvider vertexConsumers, String text, float x, float y, int color, int light, float maxWidth) {
        if (text == null || text.isEmpty()) return;
        matrices.push();
        int textWidth = textRenderer.getWidth(text);
        matrices.translate(x, y, 0);
        float scaleX = 1.0f;
        if (textWidth > maxWidth) {
            scaleX = maxWidth / (float) textWidth;
        }
        matrices.scale(scaleX, 1.0f, 1.0f);
        textRenderer.draw(Text.of(text), -textWidth / 2.0f, 0, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
        matrices.pop();
    }
}