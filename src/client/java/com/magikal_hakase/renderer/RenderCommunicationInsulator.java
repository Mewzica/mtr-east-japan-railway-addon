package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityCommunicationInsulator;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class RenderCommunicationInsulator implements BlockEntityRenderer<TileEntityCommunicationInsulator> {

    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/block/black_concrete.png"); // 黒コンクリートを使用

    public RenderCommunicationInsulator(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public boolean rendersOutsideBoundingBox(TileEntityCommunicationInsulator blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    @Override
    public void render(TileEntityCommunicationInsulator entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockPos startPos = entity.getPos();
        if (entity.connections.isEmpty()) return;

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));

        matrices.push();
        matrices.translate(0.5, 0.25, 0.5); // 碍子の中心オフセット

        for (BlockPos endPos : entity.connections) {
            if (startPos.asLong() < endPos.asLong()) {
                renderSingleWire(startPos, endPos, matrices, buffer, light);
            }
        }

        matrices.pop();
    }

    private void renderSingleWire(BlockPos startPos, BlockPos endPos, MatrixStack matrices, VertexConsumer buffer, int light) {
        double dx = endPos.getX() - startPos.getX();
        double dy = endPos.getY() - startPos.getY();
        double dz = endPos.getZ() - startPos.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        int segments = (int) Math.max(1, distance * 2);

        // ★通信線の設定
        double sag = distance * 0.02; // たるみ (0.02 = 程よい重み)
        float width = 0.04f;          // 少し細め

        // 線の色 (真っ黒)
        float r = 1.0f;
        float g = 1.0f;
        float b = 1.0f;
        float a = 1.0f;

        Vec3d prevPos = new Vec3d(0, 0, 0);

        for (int i = 1; i <= segments; i++) {
            double t = (double) i / segments;
            double x = dx * t;
            double z = dz * t;
            double parabolicY = 4 * sag * (t - t * t);
            double y = (dy * t) - parabolicY;

            Vec3d currentPos = new Vec3d(x, y, z);
            drawLineSegment(matrices, buffer, prevPos, currentPos, width, r, g, b, a, light);
            prevPos = currentPos;
        }
    }

    private void drawLineSegment(MatrixStack matrices, VertexConsumer buffer, Vec3d p1, Vec3d p2, float width, float r, float g, float b, float a, int light) {
        // 架線レンダラーと同じ addQuad ロジックを使用
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        Vec3d diff = p2.subtract(p1);
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d side = diff.crossProduct(up);
        if (side.lengthSquared() < 1.0E-5) side = diff.crossProduct(new Vec3d(0, 0, 1));
        side = side.normalize().multiply(width / 2.0);
        Vec3d vert = diff.crossProduct(side).normalize().multiply(width / 2.0);

        addQuad(buffer, matrix, normalMatrix, p1, p2, side, r, g, b, a, light);
        addQuad(buffer, matrix, normalMatrix, p1, p2, vert, r, g, b, a, light);
    }

    private void addQuad(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, Vec3d p1, Vec3d p2, Vec3d offset, float r, float g, float b, float a, int light) {
        Vec3d v1 = p1.add(offset);
        Vec3d v2 = p2.add(offset);
        Vec3d v3 = p2.subtract(offset);
        Vec3d v4 = p1.subtract(offset);

        int overlay = OverlayTexture.DEFAULT_UV;

        buffer.vertex(matrix, (float)v1.x, (float)v1.y, (float)v1.z).color(r, g, b, a).texture(0, 0).overlay(overlay).light(light).normal(normalMatrix, 0, 1, 0).next();
        buffer.vertex(matrix, (float)v2.x, (float)v2.y, (float)v2.z).color(r, g, b, a).texture(0, 1).overlay(overlay).light(light).normal(normalMatrix, 0, 1, 0).next();
        buffer.vertex(matrix, (float)v3.x, (float)v3.y, (float)v3.z).color(r, g, b, a).texture(1, 1).overlay(overlay).light(light).normal(normalMatrix, 0, 1, 0).next();
        buffer.vertex(matrix, (float)v4.x, (float)v4.y, (float)v4.z).color(r, g, b, a).texture(1, 0).overlay(overlay).light(light).normal(normalMatrix, 0, 1, 0).next();
        buffer.vertex(matrix, (float)v4.x, (float)v4.y, (float)v4.z).color(r, g, b, a).texture(1, 0).overlay(overlay).light(light).normal(normalMatrix, 0, -1, 0).next();
        buffer.vertex(matrix, (float)v3.x, (float)v3.y, (float)v3.z).color(r, g, b, a).texture(1, 1).overlay(overlay).light(light).normal(normalMatrix, 0, -1, 0).next();
        buffer.vertex(matrix, (float)v2.x, (float)v2.y, (float)v2.z).color(r, g, b, a).texture(0, 1).overlay(overlay).light(light).normal(normalMatrix, 0, -1, 0).next();
        buffer.vertex(matrix, (float)v1.x, (float)v1.y, (float)v1.z).color(r, g, b, a).texture(0, 0).overlay(overlay).light(light).normal(normalMatrix, 0, -1, 0).next();
    }
}