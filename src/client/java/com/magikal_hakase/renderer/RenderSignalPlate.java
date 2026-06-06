package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntitySignalPlate;
import com.magikal_hakase.blocks.BlockSignalPlate;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class RenderSignalPlate implements BlockEntityRenderer<TileEntitySignalPlate> {

    private final TextRenderer textRenderer;
    // 背景用の白いテクスチャ (バニラの白コンクリートを使用)

    public RenderSignalPlate(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(TileEntitySignalPlate entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // ブロックの向きを取得
        Direction facing = entity.getCachedState().get(BlockSignalPlate.FACING);
        String text = entity.message;

        matrices.push();

        // 1. ブロックの中心へ移動し、向きに合わせて回転
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation() + 180));

        // --- 文字の描画 (縦書き) ---
        matrices.push();
        // プレートの表面 (Z=0.155) に文字が来るように移動
        matrices.translate(0, 0, 0.47);

        // 文字のサイズ調整 (小さくする)
        float scale = 0.02F;
        // Minecraftの文字描画は上下左右が反転しているため、スケールで補正
        matrices.scale(-scale, -scale, scale);

        int color = 0xFFFFFF; // 白文字

        // 縦書きの計算
        // 文字列全体の高さを計算 (1文字あたり10.0fと仮定)
        float totalHeight = text.length() * 10.0f;
        // 中央揃えにするための開始Y座標 (上端)
        float yPos = -totalHeight / 2.0f + 2.0f; // 微調整(+2.0f)

        // 1文字ずつ描画
        for (int i = 0; i < text.length(); i++) {
            String charStr = String.valueOf(text.charAt(i));

            // 文字の幅を取得して、X軸(横方向)の中央に配置
            float charWidth = this.textRenderer.getWidth(charStr);

            this.textRenderer.draw(charStr, -charWidth / 2.0f, yPos, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);

            // 次の文字のY座標へ移動 (下にずらす)
            yPos += 10.0f;
        }

        matrices.pop(); // 文字描画終了
        matrices.pop(); // ブロック座標変換終了
    }
}