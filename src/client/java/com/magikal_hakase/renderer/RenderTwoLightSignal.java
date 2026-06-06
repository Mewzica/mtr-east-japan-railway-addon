package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityTwoLightSignal;
import mtr.render.RenderSignalBase;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

public class RenderTwoLightSignal extends RenderSignalBase<TileEntityTwoLightSignal> {

    private static final Identifier LIGHT_TEXTURE = new Identifier("minecraft", "textures/block/white_concrete.png");

    public RenderTwoLightSignal(BlockEntityRendererFactory.Context ctx) {
        // aspects: 2
        super(ctx.getRenderDispatcher(), true, 2);
    }

    @Override
    protected void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, VertexConsumer vertexConsumer, TileEntityTwoLightSignal entity, float tickDelta, Direction facing, int occupiedAspect, boolean isBackSide) {
        // レンズの手前位置
        float lightZ = -0.13f;

        // Aspect 1: 停止 (赤)
        // Aspect 0: 進行 (緑)

        // 1. 上 [赤] (Aspect 1 で点灯)
        // Y座標はモデルに合わせて調整してください (仮: 0.5f)
        boolean isR = (occupiedAspect == 1);
        drawLight(matrices, vertexConsumers, 0, 0.6f, lightZ, 0xFF505A, isR);

        // 2. 下 [緑] (Aspect 0 で点灯)
        // Y座標 (仮: 0.2f)
        boolean isG = (occupiedAspect == 0);
        drawLight(matrices, vertexConsumers, 0, 0.3f, lightZ, 0x94FFDF, isG);
    }

    @Override
    protected void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, VertexConsumer vertexConsumer, TileEntityTwoLightSignal entity, float tickDelta, Direction facing, boolean isOccupied, boolean isBackSide) {
    }

    private void drawLight(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float z, int colorRGB, boolean isOn) {
        int color;
        int light;

        if (isOn) {
            color = 0xFF000000 | colorRGB;
            light = 0xF000F0;
        } else {
            int r = (colorRGB >> 16) & 0xFF;
            int g = (colorRGB >> 8) & 0xFF;
            int b = colorRGB & 0xFF;
            color = 0xFF000000 | ((r/5) << 16) | ((g/5) << 8) | (b/5);
            light = 0;
        }

        float radius = 0.11f;
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getText(LIGHT_TEXTURE));

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 1.0f;

        buffer.vertex(matrix, x - radius, y + radius, z).color(r, g, b, a).texture(0, 0).light(light).next();
        buffer.vertex(matrix, x + radius, y + radius, z).color(r, g, b, a).texture(1, 0).light(light).next();
        buffer.vertex(matrix, x + radius, y - radius, z).color(r, g, b, a).texture(1, 1).light(light).next();
        buffer.vertex(matrix, x - radius, y - radius, z).color(r, g, b, a).texture(0, 1).light(light).next();
    }
}