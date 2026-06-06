package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityStoppingPositionSign;
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

public class RenderStoppingPositionSign extends BlockEntityRendererMapper<TileEntityStoppingPositionSign> {

    public RenderStoppingPositionSign(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getRenderDispatcher());
    }

    @Override
    public void render(TileEntityStoppingPositionSign entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;

        Direction facing = entity.getCachedState().get(HorizontalFacingBlock.FACING);
        String text = entity.getSignValue();

        if (text.isEmpty()) return;

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation() + 180));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));

        // ★座標調整
        // モデルの表面に合わせて Z を調整してください
        // 例: 0.07f (少し手前)
        matrices.translate(0.0125, -0.045, -0.0475f);

        // スケール
        float scale = 0.02F;
        matrices.scale(scale, scale, scale);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // 文字色: 黒 (0xFF000000)
        // 枠制限: 30 (はみ出さないように)
        drawTextFit(matrices, textRenderer, vertexConsumers, text, 0, -4, 0xFF000000, light, 10);

        matrices.pop();
    }

    // PIDS等で使ったものと同じ自動縮小メソッド
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