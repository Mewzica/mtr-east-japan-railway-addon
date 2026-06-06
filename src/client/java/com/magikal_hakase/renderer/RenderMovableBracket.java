package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityMovableBracket;
import com.magikal_hakase.blocks.BlockMovableBracket;
import com.magikal_hakase.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class RenderMovableBracket implements BlockEntityRenderer<TileEntityMovableBracket> {

    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/block/white_concrete.png");

    public RenderMovableBracket(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public boolean rendersOutsideBoundingBox(TileEntityMovableBracket blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    @Override
    public void render(TileEntityMovableBracket entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.connections.isEmpty()) return;

        BlockPos startPos = entity.getPos();
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));

        // ★自分のオフセットを取得 (アームの先端など)
        Vec3d startOffset = getWireOffset(entity.getCachedState());

        matrices.push();
        // ブロックの原点ではなく、計算したオフセット位置へ移動
        matrices.translate(startOffset.x, startOffset.y, startOffset.z);

        for (BlockPos endPos : entity.connections) {
            if (startPos.asLong() < endPos.asLong()) {
                // 相手ブロックの状態を取得
                BlockState endState = entity.getWorld().getBlockState(endPos);
                // ★相手のオフセットも取得 (相手もブラケットなら先端、普通の碍子なら中心)
                Vec3d endOffset = getWireOffset(endState);

                renderCatenaryWithOffsets(startPos, endPos, startOffset, endOffset, matrices, buffer, light);
            }
        }

        matrices.pop();
    }

    // ブロックの種類と向きに応じて「線の接続点」を返すメソッド
    private Vec3d getWireOffset(BlockState state) {
        // デフォルト (ブロック中心)
        double x = 0.5;
        double y = 0.4;
        double z = 0.5;

        // 可動ブラケットの場合、向きに合わせて位置をずらす
        if (state.getBlock() == ModBlocks.MOVABLE_BRACKET) {
            Direction facing = state.get(BlockMovableBracket.FACING);

            // ★調整ポイント: アームの長さ (ブロック中心からの距離)
            // 例: 0.8ブロック分突き出す
            double armLength = 1.0;

            // アームの高さ調整 (少し上げる等)
            y = 1.0;

            // 向きに合わせて座標を加算
            x += facing.getOffsetX() * armLength;
            z += facing.getOffsetZ() * armLength;
        }

        return new Vec3d(x, y, z);
    }

    private void renderCatenaryWithOffsets(BlockPos startPos, BlockPos endPos, Vec3d startOffset, Vec3d endOffset, MatrixStack matrices, VertexConsumer buffer, int light) {
        // 通常の座標差分
        double dx = endPos.getX() - startPos.getX();
        double dy = endPos.getY() - startPos.getY();
        double dz = endPos.getZ() - startPos.getZ();

        // ★重要: ここでオフセット分を補正
        // (相手のオフセット) - (自分のオフセット) を加えることで、正しい相対ベクトルになる
        dx += (endOffset.x - startOffset.x);
        dy += (endOffset.y - startOffset.y);
        dz += (endOffset.z - startOffset.z);

        // 以下、いつもの架線描画ロジック
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int segments = (int) Math.max(1, distance * 2);

        // 設定値 (架線碍子と同じ)
        double sagMessenger = distance * 0.015;
        double sagContact = distance * 0.002;
        double contactOffset = 1.0;
        double hangerInterval = 1.65;

        float widthWire = 0.05f;
        float widthHanger = 0.03f;
        float r = 0.15f; float g = 0.15f; float b = 0.15f; float a = 1.0f;

        // 1. ちょう架線
        drawCatenaryCurve(matrices, buffer, dx, dy, dz, segments, sagMessenger, 0, widthWire, r, g, b, a, light);
        // 2. トロリ線
        drawCatenaryCurve(matrices, buffer, dx, dy, dz, segments, sagContact, -contactOffset, widthWire, r, g, b, a, light);

        // 3. ハンガー
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

    // (drawCatenaryCurve, drawLineSegment, addQuad は RenderCatenaryInsulator と同じため省略せずに記述してください。
    //  もし手間であれば、RenderCatenaryInsulatorを継承してメソッドをprotectedにするとコードを減らせます)

    // 省略されていた描画メソッド（RenderCatenaryInsulatorからコピーしてください）
    private void drawCatenaryCurve(MatrixStack matrices, VertexConsumer buffer, double dx, double dy, double dz, int segments, double sag, double yOffset, float width, float r, float g, float b, float a, int light) {
        Vec3d prevPos = new Vec3d(0, yOffset, 0);
        for (int i = 1; i <= segments; i++) {
            double t = (double) i / segments;
            double x = dx * t;
            double z = dz * t;
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