package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityFourLightSignal;
import mtr.render.RenderSignalBase;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

public class RenderFourLightSignal extends RenderSignalBase<TileEntityFourLightSignal> {

    // 光の表現用 (白コンクリート)
    private static final Identifier LIGHT_TEXTURE = new Identifier("minecraft", "textures/block/white_concrete.png");

    public RenderFourLightSignal(BlockEntityRendererFactory.Context ctx) {
        // 親クラスのコンストラクタを呼び出す
        // dispatcher: 描画管理
        // isSingleSided: true (信号は片面だけ表示)
        super(ctx.getRenderDispatcher(), true, 4);
    }

    @Override
    protected void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, VertexConsumer vertexConsumer, TileEntityFourLightSignal entity, float tickDelta, Direction facing, int occupiedAspect, boolean isBackSide) {

        // --- レンズの発光 ---
        float lightZ = -0.13f;

        // --- 色の決定 ---
        // occupiedAspect の値は getOccupiedAspect メソッドから来ます
        // 1: 赤 (直前の区間が占有されている)
        // 2: 黄 (次の区間が占有されている)
        // 3: 黄 (その次の区間が占有されている = 減速/警戒)
        // 0: 緑 (クリア)

        // 1. 一番上 [黄] (Aspect 3 で点灯)
        boolean isY1 = (occupiedAspect == 3);
        drawLight(matrices, vertexConsumers, 0, 1.036f, lightZ, 0xFFA300, isY1);

        // 2. 二番目 [赤] (Aspect 1 で点灯)
        boolean isR = (occupiedAspect == 1);
        drawLight(matrices, vertexConsumers, 0, 0.723f, lightZ, 0xFF505A, isR);

        // 3. 三番目 [黄] (Aspect 2 で点灯 - 警戒現示用)
        boolean isY2 = (occupiedAspect == 2);
        drawLight(matrices, vertexConsumers, 0, 0.411f, lightZ, 0xFFA300, isY2);

        // 4. 一番下 [緑] (Aspect 0 で点灯)
        boolean isG = (occupiedAspect == 0 || occupiedAspect == 3);
        drawLight(matrices, vertexConsumers, 0, 0.098f, lightZ, 0x94FFDF, isG);
    }

    // ※もう一つの render メソッド (オーバーライド必須だが今回は使わない)
    @Override
    protected void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, VertexConsumer vertexConsumer, TileEntityFourLightSignal entity, float tickDelta, Direction facing, boolean isOccupied, boolean isBackSide) {
        // 使わないので空でOK
    }

    // レンズを描画するヘルパー
    private void drawLight(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float z, int colorRGB, boolean isOn) {
        int color;
        int light;

        if (isOn) {
            // 点灯: 鮮やかな色 + MAX明るさ
            color = 0xFF000000 | colorRGB;
            light = 0xF000F0;
        } else {
            // 消灯: 暗くする (RGBを1/5に)
            int r = (colorRGB >> 16) & 0xFF;
            int g = (colorRGB >> 8) & 0xFF;
            int b = colorRGB & 0xFF;
            color = 0xFF000000 | ((r/5) << 16) | ((g/5) << 8) | (b/5);
            light = 0; // 環境光
        }

        float radius = 0.11f; // レンズ半径

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        // MTR標準のVertexConsumerではなく、自分で取得したプロバイダを使うことで白コンクリートテクスチャを利用
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getText(LIGHT_TEXTURE));

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 1.0f;

        // 四角形を描画
        buffer.vertex(matrix, x - radius, y + radius, z).color(r, g, b, a).texture(0, 0).light(light).next();
        buffer.vertex(matrix, x + radius, y + radius, z).color(r, g, b, a).texture(1, 0).light(light).next();
        buffer.vertex(matrix, x + radius, y - radius, z).color(r, g, b, a).texture(1, 1).light(light).next();
        buffer.vertex(matrix, x - radius, y - radius, z).color(r, g, b, a).texture(0, 1).light(light).next();
    }
}