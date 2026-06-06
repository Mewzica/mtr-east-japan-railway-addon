package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityCrossingGate;
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class RenderCrossingGate extends BlockEntityRendererMapper<TileEntityCrossingGate> {

    private static final Identifier ARM_TEXTURE = new Identifier("mtr-east-japan-railway-addon", "textures/block/crossing_gate_arm.png");
    // ★追加: 折損防止器（根元の金具）のテクスチャ
    private static final Identifier BASE_TEXTURE = new Identifier("mtr-east-japan-railway-addon", "textures/block/crossing_gate_base.png");

    public RenderCrossingGate(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getRenderDispatcher());
    }

    @Override
    public void render(TileEntityCrossingGate entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction facing = entity.getCachedState().get(HorizontalFacingBlock.FACING);

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation()));

        // --- ここからアニメーションするパーツの描画 ---
        matrices.push();

        // 回転軸(ヒンジ)へ移動
        matrices.translate(0.3, 0.5, -0.3);

        // ★ 回転を適用 (これ以降に描画したものはすべて連動して回ります)
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(entity.getCurrentAngle()));

        // ★追加: 1. 折損防止器（根元パーツ）を描画
        VertexConsumer baseBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(BASE_TEXTURE));
        drawBreakagePreventer(matrices, baseBuffer, light, overlay);

        // 2. 竿本体を描画
        VertexConsumer armBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(ARM_TEXTURE));
        drawArm(matrices, armBuffer, entity.getGateLength(), light, overlay);

        matrices.pop();
        matrices.pop();
    }

    // ★新規追加: 折損防止器を描画するメソッド
    private void drawBreakagePreventer(MatrixStack matrices, VertexConsumer buffer, int light, int overlay) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Matrix3f normal = matrices.peek().getNormalMatrix();

        // サイズ調整
        float w = 0.07f;     // 竿(0.05f)より少し太くする
        float startX = -0.1f; // 回転軸より少し後ろ(カウンターウェイト側)から開始
        float endX = 0.4f;    // 竿を包み込むように少し前まで伸ばす

        float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;

        // 前面
        buffer.vertex(matrix, startX, w, w).color(r,g,b,a).texture(0, 0).overlay(overlay).light(light).normal(normal, 0, 0, 1).next();
        buffer.vertex(matrix, startX, -w, w).color(r,g,b,a).texture(0, 1).overlay(overlay).light(light).normal(normal, 0, 0, 1).next();
        buffer.vertex(matrix, endX, -w, w).color(r,g,b,a).texture(1, 1).overlay(overlay).light(light).normal(normal, 0, 0, 1).next();
        buffer.vertex(matrix, endX, w, w).color(r,g,b,a).texture(1, 0).overlay(overlay).light(light).normal(normal, 0, 0, 1).next();

        // 背面
        buffer.vertex(matrix, endX, w, -w).color(r,g,b,a).texture(1, 0).overlay(overlay).light(light).normal(normal, 0, 0, -1).next();
        buffer.vertex(matrix, endX, -w, -w).color(r,g,b,a).texture(1, 1).overlay(overlay).light(light).normal(normal, 0, 0, -1).next();
        buffer.vertex(matrix, startX, -w, -w).color(r,g,b,a).texture(0, 1).overlay(overlay).light(light).normal(normal, 0, 0, -1).next();
        buffer.vertex(matrix, startX, w, -w).color(r,g,b,a).texture(0, 0).overlay(overlay).light(light).normal(normal, 0, 0, -1).next();

        // 上面
        buffer.vertex(matrix, startX, w, -w).color(r,g,b,a).texture(0, 0).overlay(overlay).light(light).normal(normal, 0, 1, 0).next();
        buffer.vertex(matrix, startX, w, w).color(r,g,b,a).texture(0, 1).overlay(overlay).light(light).normal(normal, 0, 1, 0).next();
        buffer.vertex(matrix, endX, w, w).color(r,g,b,a).texture(1, 1).overlay(overlay).light(light).normal(normal, 0, 1, 0).next();
        buffer.vertex(matrix, endX, w, -w).color(r,g,b,a).texture(1, 0).overlay(overlay).light(light).normal(normal, 0, 1, 0).next();

        // 下面
        buffer.vertex(matrix, endX, -w, -w).color(r,g,b,a).texture(1, 0).overlay(overlay).light(light).normal(normal, 0, -1, 0).next();
        buffer.vertex(matrix, endX, -w, w).color(r,g,b,a).texture(1, 1).overlay(overlay).light(light).normal(normal, 0, -1, 0).next();
        buffer.vertex(matrix, startX, -w, w).color(r,g,b,a).texture(0, 1).overlay(overlay).light(light).normal(normal, 0, -1, 0).next();
        buffer.vertex(matrix, startX, -w, -w).color(r,g,b,a).texture(0, 0).overlay(overlay).light(light).normal(normal, 0, -1, 0).next();
    }

    private void drawArm(MatrixStack matrices, VertexConsumer buffer, int length, int light, int overlay) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Matrix3f normal = matrices.peek().getNormalMatrix();

        // 竿の太さ (例: 0.1ブロック)
        float w = 0.05f;

        // 長さ (ブロック単位)
        float len = (float) length;

        // 面の色
        float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;

        // 長さに応じてUV(テクスチャの繰り返し)を調整
        float maxU = len; // 長さが3ならテクスチャを3回繰り返す

        // 前面
        buffer.vertex(matrix, 0, w, w).color(r,g,b,a).texture(0, 0).overlay(overlay).light(light).normal(normal, 0, 0, 1).next();
        buffer.vertex(matrix, 0, -w, w).color(r,g,b,a).texture(0, 1).overlay(overlay).light(light).normal(normal, 0, 0, 1).next();
        buffer.vertex(matrix, len, -w, w).color(r,g,b,a).texture(maxU, 1).overlay(overlay).light(light).normal(normal, 0, 0, 1).next();
        buffer.vertex(matrix, len, w, w).color(r,g,b,a).texture(maxU, 0).overlay(overlay).light(light).normal(normal, 0, 0, 1).next();

        // 背面
        buffer.vertex(matrix, len, w, -w).color(r,g,b,a).texture(maxU, 0).overlay(overlay).light(light).normal(normal, 0, 0, -1).next();
        buffer.vertex(matrix, len, -w, -w).color(r,g,b,a).texture(maxU, 1).overlay(overlay).light(light).normal(normal, 0, 0, -1).next();
        buffer.vertex(matrix, 0, -w, -w).color(r,g,b,a).texture(0, 1).overlay(overlay).light(light).normal(normal, 0, 0, -1).next();
        buffer.vertex(matrix, 0, w, -w).color(r,g,b,a).texture(0, 0).overlay(overlay).light(light).normal(normal, 0, 0, -1).next();

        // 上面・下面などの描画も必要であれば同様に `buffer.vertex` を追加します。
    }
}