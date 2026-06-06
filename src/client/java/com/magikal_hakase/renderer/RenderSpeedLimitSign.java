package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntitySpeedLimitSign;
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

public class RenderSpeedLimitSign extends BlockEntityRendererMapper<TileEntitySpeedLimitSign> {

    public RenderSpeedLimitSign(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getRenderDispatcher());
    }

    @Override
    public void render(TileEntitySpeedLimitSign entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;

        Direction facing = entity.getCachedState().get(HorizontalFacingBlock.FACING);
        String text = entity.getLimitValue();

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation() + 180));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));

        // 表面に描画
        // 厚みに合わせて調整してください (例: 0.07)
        matrices.translate(0.025, -0.265, -0.05f);

        // 少し上にずらす等の調整が必要ならここ
        // matrices.translate(0, -0.1, 0);

        float scale = 0.055F;
        matrices.scale(scale, scale, scale);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // 黒文字 (0xFF000000) で描画
        // drawTextFitで枠内に収める (maxWidth = 16px相当のスケールなら 20～25 くらい)
        drawTextFit(matrices, textRenderer, vertexConsumers, text, 0, -4, 0xFF000000, light, 20);

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