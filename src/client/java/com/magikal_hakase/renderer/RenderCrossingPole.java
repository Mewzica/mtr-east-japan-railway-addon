package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityCrossingPole;
import com.magikal_hakase.blocks.BlockCrossingPole;
import com.magikal_hakase.data.EnumCrossingAttachment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f; // ★追加
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class RenderCrossingPole implements BlockEntityRenderer<TileEntityCrossingPole> {

    // レンズ用のテクスチャ
    private static final Identifier LENS_TEXTURE = new Identifier("mtr-east-japan-railway-addon", "textures/block/crossing_light_lens.png");

    public RenderCrossingPole(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(TileEntityCrossingPole entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState state = entity.getCachedState();
        if (!state.get(BlockCrossingPole.POWERED)) return;

        boolean blinkState = (System.currentTimeMillis() / 500) % 2 == 0;

        int maxLight = LightmapTextureManager.MAX_LIGHT_COORDINATE;
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(LENS_TEXTURE));

        // ★引数に overlay を追加
        renderLightForSide(state, Direction.NORTH, BlockCrossingPole.NORTH, blinkState, matrices, buffer, maxLight, overlay);
        renderLightForSide(state, Direction.EAST, BlockCrossingPole.EAST, blinkState, matrices, buffer, maxLight, overlay);
        renderLightForSide(state, Direction.SOUTH, BlockCrossingPole.SOUTH, blinkState, matrices, buffer, maxLight, overlay);
        renderLightForSide(state, Direction.WEST, BlockCrossingPole.WEST, blinkState, matrices, buffer, maxLight, overlay);
    }

    // ★引数に overlay を追加
    private void renderLightForSide(BlockState state, Direction dir, net.minecraft.state.property.EnumProperty<EnumCrossingAttachment> property, boolean blinkState, MatrixStack matrices, VertexConsumer buffer, int light, int overlay) {
        EnumCrossingAttachment attachment = state.get(property);
        if (attachment == EnumCrossingAttachment.NONE || attachment == EnumCrossingAttachment.SIGN) return;

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-dir.asRotation() + 180));

        float zOffset = -0.045f;
        float yOffset = -0.48f;

        if (attachment == EnumCrossingAttachment.LIGHT_H) {
            float xOffset = 0.4685f;

            if (blinkState) {
                drawLens(matrices, buffer, -xOffset, yOffset, zOffset, light, overlay); // ★ overlay を渡す
            } else {
                drawLens(matrices, buffer, xOffset + 0.6675f, yOffset, zOffset, light, overlay);
            }

        } else if (attachment == EnumCrossingAttachment.LIGHT_V) {
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270));
            float ySpread = 0.4f;

            if (blinkState) {
                drawLens(matrices, buffer, -0.47f, yOffset + ySpread - 0.38f, zOffset - 0.045f, light, overlay);
            } else {
                drawLens(matrices, buffer, -0.47f, yOffset - ySpread - 0.38f, zOffset - 0.045f, light, overlay);
            }

            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            if (blinkState) {
                drawLens(matrices, buffer, 1.135f, yOffset - ySpread - 0.38f, -zOffset - 0.135f, light, overlay);
            } else {
                drawLens(matrices, buffer, 1.135f, yOffset + ySpread - 0.38f, -zOffset - 0.135f, light, overlay);
            }

        }

        matrices.pop();
    }

    // ★引数に overlay を追加し、描画内容を修正
    private void drawLens(MatrixStack matrices, VertexConsumer buffer, float x, float y, float z, int light, int overlay) {
        float size = 0.485f;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix(); // ★追加
        float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;

        // ★ .overlay(overlay) と .normal(normalMatrix, 0, 0, -1) を追加
        buffer.vertex(matrix, x - size, y + size, z).color(r, g, b, a).texture(0, 0).overlay(overlay).light(light).normal(normalMatrix, 0, 0, -1).next();
        buffer.vertex(matrix, x + size, y + size, z).color(r, g, b, a).texture(1, 0).overlay(overlay).light(light).normal(normalMatrix, 0, 0, -1).next();
        buffer.vertex(matrix, x + size, y - size, z).color(r, g, b, a).texture(1, 1).overlay(overlay).light(light).normal(normalMatrix, 0, 0, -1).next();
        buffer.vertex(matrix, x - size, y - size, z).color(r, g, b, a).texture(0, 1).overlay(overlay).light(light).normal(normalMatrix, 0, 0, -1).next();
    }
}