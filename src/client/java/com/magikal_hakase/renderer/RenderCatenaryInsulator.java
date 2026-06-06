package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityCatenaryInsulator;
import com.magikal_hakase.blocks.ModBlocks;        // 追加
import com.magikal_hakase.blocks.BlockMovableBracket; // 追加
import net.minecraft.block.BlockState;           // 追加
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;        // 追加
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class RenderCatenaryInsulator implements BlockEntityRenderer<TileEntityCatenaryInsulator> {

    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/block/white_concrete.png");

    public RenderCatenaryInsulator(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public boolean rendersOutsideBoundingBox(TileEntityCatenaryInsulator blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    @Override
    public void render(TileEntityCatenaryInsulator entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockPos startPos = entity.getPos();

        if (entity.connections.isEmpty()) return;

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));

        Vec3d startOffset = new Vec3d(0.5, 0.3, 0.5);

        matrices.push();
        // ここで自分の接続点へ移動
        matrices.translate(startOffset.x, startOffset.y, startOffset.z);

        for (BlockPos endPos : entity.connections) {
            // 2重描画防止
            if (startPos.asLong() < endPos.asLong()) {

                // ★修正2: 相手のブロック状態を取得し、オフセットを計算
                BlockState endState = entity.getWorld().getBlockState(endPos);
                Vec3d endOffset = getWireOffset(endState);

                // 補正付きで描画
                renderCatenarySystem(startPos, endPos, startOffset, endOffset, matrices, buffer, light);
            }
        }

        matrices.pop();
    }

    // ★追加: 相手ブロックに応じたオフセットを取得 (RenderMovableBracketのものと整合性を合わせる)
    private Vec3d getWireOffset(BlockState state) {
        // デフォルト (架線碍子の中心)
        // ※RenderCatenaryInsulatorの元のtranslate値に合わせるなら y=0.3
        double x = 0.5;
        double y = 0.3;
        double z = 0.5;

        // 可動ブラケットの場合
        if (state.getBlock() == ModBlocks.MOVABLE_BRACKET) {
            Direction facing = state.get(BlockMovableBracket.FACING);

            // RenderMovableBracket.java で設定した値と完全に一致させる必要があります
            double armLength = 1.0;
            y = 1.0; // ブラケットの高さ

            x += facing.getOffsetX() * armLength;
            z += facing.getOffsetZ() * armLength;
        }

        return new Vec3d(x, y, z);
    }

    private void renderCatenarySystem(BlockPos startPos, BlockPos endPos, Vec3d startOffset, Vec3d endOffset, MatrixStack matrices, VertexConsumer buffer, int light) {
        // 通常の座標差分
        double dx = endPos.getX() - startPos.getX();
        double dy = endPos.getY() - startPos.getY();
        double dz = endPos.getZ() - startPos.getZ();

        // ★重要: オフセット補正
        // (相手のオフセット) - (自分のオフセット)
        dx += (endOffset.x - startOffset.x);
        dy += (endOffset.y - startOffset.y);
        dz += (endOffset.z - startOffset.z);

        // --- 以下は元のコードと同じ ---
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        double sagMessenger = distance * 0.015;
        double sagContact   = distance * 0.002;
        double contactOffset = 1.0;
        double hangerInterval = 1.65;
        float widthWire = 0.05f;
        float widthHanger = 0.03f;
        float r = 0.15f; float g = 0.15f; float b = 0.15f; float a = 1.0f;
        int segments = (int) Math.max(1, distance * 2);

        drawCatenaryCurve(matrices, buffer, dx, dy, dz, segments, sagMessenger, 0.0, widthWire, r, g, b, a, light);
        drawCatenaryCurve(matrices, buffer, dx, dy, dz, segments, sagContact, -contactOffset, widthWire, r, g, b, a, light);

        double currentDist = hangerInterval;
        while (currentDist < distance - 0.1) {
            double t = currentDist / distance;
            double x = dx * t;
            double z = dz * t;
            double yMessenger = (dy * t) - (4 * sagMessenger * (t - t * t));
            double yContact = (dy * t) - contactOffset - (4 * sagContact * (t - t * t));
            Vec3d topPos = new Vec3d(x, yMessenger, z);
            Vec3d bottomPos = new Vec3d(x, yContact, z);
            drawLineSegment(matrices, buffer, topPos, bottomPos, widthHanger, r, g, b, a, light);
            currentDist += hangerInterval;
        }
    }

    // 曲線を描画するヘルパーメソッド
    private void drawCatenaryCurve(MatrixStack matrices, VertexConsumer buffer, double dx, double dy, double dz, int segments, double sag, double yOffset, float width, float r, float g, float b, float a, int light) {
        Vec3d prevPos = new Vec3d(0, yOffset, 0); // 開始点 (オフセット適用)

        for (int i = 1; i <= segments; i++) {
            double t = (double) i / segments;

            double x = dx * t;
            double z = dz * t;

            // ベースの高さ(dy * t) + オフセット - 放物線変位
            double parabolicY = 4 * sag * (t - t * t);
            double y = (dy * t) + yOffset - parabolicY;

            Vec3d currentPos = new Vec3d(x, y, z);

            drawLineSegment(matrices, buffer, prevPos, currentPos, width, r, g, b, a, light);
            prevPos = currentPos;
        }
    }

    private void drawLineSegment(MatrixStack matrices, VertexConsumer buffer, Vec3d p1, Vec3d p2, float width, float r, float g, float b, float a, int light) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        Vec3d diff = p2.subtract(p1);

        // 垂直に近い線(ハンガー)の場合の対策
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d side = diff.crossProduct(up);

        if (side.lengthSquared() < 1.0E-5) {
            // ほぼ垂直ならZ軸基準で広げる
            side = diff.crossProduct(new Vec3d(0, 0, 1));
        }
        side = side.normalize().multiply(width / 2.0);

        // 十字に組んで立体的に見せるためのもう一方の軸
        Vec3d vert = diff.crossProduct(side).normalize().multiply(width / 2.0);

        // 水平の板
        addQuad(buffer, matrix, normalMatrix, p1, p2, side, r, g, b, a, light);
        // 垂直の板
        addQuad(buffer, matrix, normalMatrix, p1, p2, vert, r, g, b, a, light);
    }

    private void addQuad(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, Vec3d p1, Vec3d p2, Vec3d offset, float r, float g, float b, float a, int light) {
        Vec3d v1 = p1.add(offset);
        Vec3d v2 = p2.add(offset);
        Vec3d v3 = p2.subtract(offset);
        Vec3d v4 = p1.subtract(offset);

        int overlay = OverlayTexture.DEFAULT_UV;

        // 表面
        buffer.vertex(matrix, (float)v1.x, (float)v1.y, (float)v1.z).color(r, g, b, a).texture(0, 0).overlay(overlay).light(light).normal(normalMatrix, 0, 1, 0).next();
        buffer.vertex(matrix, (float)v2.x, (float)v2.y, (float)v2.z).color(r, g, b, a).texture(0, 1).overlay(overlay).light(light).normal(normalMatrix, 0, 1, 0).next();
        buffer.vertex(matrix, (float)v3.x, (float)v3.y, (float)v3.z).color(r, g, b, a).texture(1, 1).overlay(overlay).light(light).normal(normalMatrix, 0, 1, 0).next();
        buffer.vertex(matrix, (float)v4.x, (float)v4.y, (float)v4.z).color(r, g, b, a).texture(1, 0).overlay(overlay).light(light).normal(normalMatrix, 0, 1, 0).next();

        // 裏面
        buffer.vertex(matrix, (float)v4.x, (float)v4.y, (float)v4.z).color(r, g, b, a).texture(1, 0).overlay(overlay).light(light).normal(normalMatrix, 0, -1, 0).next();
        buffer.vertex(matrix, (float)v3.x, (float)v3.y, (float)v3.z).color(r, g, b, a).texture(1, 1).overlay(overlay).light(light).normal(normalMatrix, 0, -1, 0).next();
        buffer.vertex(matrix, (float)v2.x, (float)v2.y, (float)v2.z).color(r, g, b, a).texture(0, 1).overlay(overlay).light(light).normal(normalMatrix, 0, -1, 0).next();
        buffer.vertex(matrix, (float)v1.x, (float)v1.y, (float)v1.z).color(r, g, b, a).texture(0, 0).overlay(overlay).light(light).normal(normalMatrix, 0, -1, 0).next();
    }
}