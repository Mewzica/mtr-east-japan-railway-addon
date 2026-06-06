package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityThreeLightSignal;
import mtr.render.RenderSignalBase;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

public class RenderThreeLightSignal extends RenderSignalBase<TileEntityThreeLightSignal> {

    private static final Identifier LIGHT_TEXTURE = new Identifier("minecraft", "textures/block/white_concrete.png");

    public RenderThreeLightSignal(BlockEntityRendererFactory.Context ctx) {
        // aspects: 3 (赤・黄まで判定)
        super(ctx.getRenderDispatcher(), true, 3);
    }

    @Override
    protected void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, VertexConsumer vertexConsumer, TileEntityThreeLightSignal entity, float tickDelta, Direction facing, int occupiedAspect, boolean isBackSide) {
        // 仮座標: 上から順に配置
        float lightZ = -0.13f;

        // 1. 一番上 [赤] (Aspect 1: 停止)
        boolean isR = (occupiedAspect == 1);
        drawLight(matrices, vertexConsumers, 0, 0.773f, lightZ, 0xFF505A, isR);

        // 2. 真ん中 [黄] (Aspect 2: 注意 / Aspect 3: 減速)
        boolean isY = (occupiedAspect == 2 || occupiedAspect == 3);
        drawLight(matrices, vertexConsumers, 0, 0.451f, lightZ, 0xFFA300, isY);

        // 3. 一番下 [緑] (Aspect 0: 進行)
        boolean isG = (occupiedAspect == 0);
        drawLight(matrices, vertexConsumers, 0, 0.148f, lightZ, 0x94FFDF, isG);
    }

    @Override
    protected void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, VertexConsumer vertexConsumer, TileEntityThreeLightSignal entity, float tickDelta, Direction facing, boolean isOccupied, boolean isBackSide) {
        // 空でOK
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