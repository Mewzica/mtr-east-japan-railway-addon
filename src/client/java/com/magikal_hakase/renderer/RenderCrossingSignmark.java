package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityCrossingSignmark;
import com.magikal_hakase.blocks.BlockCrossingSignmark;
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
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

public class RenderCrossingSignmark extends BlockEntityRendererMapper<TileEntityCrossingSignmark> {

    // ★光る部分のテクスチャ (発光させたい模様だけを描いた透過PNGなど)
    private static final Identifier LENS_TEXTURE = new Identifier("mtr-east-japan-railway-addon", "textures/block/crossing_signmark_lens.png");

    public RenderCrossingSignmark(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getRenderDispatcher());
    }

    @Override
    public void render(TileEntityCrossingSignmark entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState state = entity.getCachedState();

        // レッドストーンONの時だけ光らせる
        if (!state.get(BlockCrossingSignmark.POWERED)) return;

        Direction facing = state.get(BlockCrossingSignmark.FACING);
        int maxLight = LightmapTextureManager.MAX_LIGHT_COORDINATE;
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(LENS_TEXTURE));

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation() + 180));

        // JSONモデルの「ライトの表面」に重なるように位置を微調整します
        // 例: 手前(Z軸マイナス方向)に0.25ずらす
        float zOffset = -0.13f;
        float yOffset = 0.0f;
        float size = 0.465f; // レンズ面の大きさ

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
        float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;

        // レンズ面を描画
        buffer.vertex(matrix, -size, yOffset + size, zOffset).color(r, g, b, a).texture(0, 0).overlay(overlay).light(maxLight).normal(normalMatrix, 0, 0, -1).next();
        buffer.vertex(matrix, size, yOffset + size, zOffset).color(r, g, b, a).texture(1, 0).overlay(overlay).light(maxLight).normal(normalMatrix, 0, 0, -1).next();
        buffer.vertex(matrix, size, yOffset - size, zOffset).color(r, g, b, a).texture(1, 1).overlay(overlay).light(maxLight).normal(normalMatrix, 0, 0, -1).next();
        buffer.vertex(matrix, -size, yOffset - size, zOffset).color(r, g, b, a).texture(0, 1).overlay(overlay).light(maxLight).normal(normalMatrix, 0, 0, -1).next();

        matrices.pop();
    }
}