package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityStationClock;
import com.magikal_hakase.blocks.BlockStationClock;
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.client.render.OverlayTexture;

public class RenderStationClock extends BlockEntityRendererMapper<TileEntityStationClock> {

    private static final Identifier HAND_TEXTURE = new Identifier("minecraft", "textures/block/white_concrete.png");

    public RenderStationClock(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getRenderDispatcher());
    }

    @Override
    public void render(TileEntityStationClock entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;

        Direction facing = entity.getCachedState().get(BlockStationClock.FACING);
        long time = entity.getWorld().getTimeOfDay();

        // --- 時間と角度の計算 ---
        // Minecraftの1日は24000tick
        // 0tick = 6:00 AM
        // 6000tick = 12:00 PM (真昼)
        // 18000tick = 0:00 AM (真夜中)

        // 計算しやすいように 6:00開始 を 12:00開始(0度) に補正 (+6000)
        double adjustedTime = time + 6000;

        // 短針 (Hour Hand)
        // 12時間(12000tick)で1周(360度)する
        float hourAngle = (float) ((adjustedTime % 12000) / 12000.0 * 360.0);

        // 長針 (Minute Hand)
        // 1時間(1000tick)で1周(360度)する
        float minuteAngle = (float) ((adjustedTime % 1000) / 1000.0 * 360.0);

        // --- 描画開始 ---
        matrices.push();

        // 1. ブロックの中心へ移動
        matrices.translate(0.5, 0.5, 0.5);

        // 2. ブロックの向きに合わせて回転
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation()));

        // 時計の盤面の中心座標 (モデルに合わせて調整してください)
        // ここでは Y=-0.2 (中心より少し下), Z=0.13 (厚みの表面) と仮定
        float centerY = 0.0f; // ブロックの中心高さと合わせるなら調整
        float surfaceZ = -0.105f; // 盤面より少し手前

        // =======================
        //   表面 (Front Side)
        // =======================
        matrices.push();
        matrices.translate(0, centerY, surfaceZ); // 表面の位置へ

        // 短針を描画
        drawHand(matrices, vertexConsumers, -hourAngle, 0.225f, 0.04f);
        // 長針を描画 (少し手前にずらして重なり防止)
        matrices.translate(0, 0, 0.001f);
        drawHand(matrices, vertexConsumers, -minuteAngle, 0.425f, 0.03f);

        matrices.pop();

        // =======================
        //   裏面 (Back Side)
        // =======================
        // 日本の駅時計は両面が多いので裏側も描く
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180)); // 反転
        matrices.translate(0, centerY, surfaceZ); // 裏面の位置へ

        // 裏面は時計の進む向きが逆に見えないよう、角度計算は同じでOK（Y軸180度回転してるので見た目は合う）
        drawHand(matrices, vertexConsumers, -hourAngle, 0.225f, 0.04f); // 回転方向を逆にする
        matrices.translate(0, 0, 0.001f);
        drawHand(matrices, vertexConsumers, -minuteAngle, 0.425f, 0.03f);

        matrices.pop();

        matrices.pop();
    }

    /**
     * 針を描画するメソッド
     * @param angle 回転角度 (度)
     * @param length 針の長さ
     * @param width 針の太さ
     */
    private void drawHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float angle, float length, float width) {
        matrices.push();

        // 1. 角度分だけZ軸回転 (時計回りなのでマイナス)
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-angle));

        // 2. 針の形状を描画 (細長い長方形)
        // 中心から上に伸びるように描く
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(HAND_TEXTURE));
        Matrix4f mat = matrices.peek().getPositionMatrix();

        float minX = -width / 2;
        float maxX = width / 2;
        float minY = -length * 0.2f; // 回転中心を少しずらす（本物の時計っぽく）
        float maxY = length;

        // シンプルな四角形 (ライトの影響を受けないように明るさMAXにしています)
        int light = 0xF000F0;

        int overlay = OverlayTexture.DEFAULT_UV;

        // 色を黒(0,0,0)に設定
        buffer.vertex(mat, minX, maxY, 0).color(0, 0, 0, 255).texture(0, 0).overlay(overlay).light(light).normal(0, 0, 1).next();
        buffer.vertex(mat, maxX, maxY, 0).color(0, 0, 0, 255).texture(1, 0).overlay(overlay).light(light).normal(0, 0, 1).next();
        buffer.vertex(mat, maxX, minY, 0).color(0, 0, 0, 255).texture(1, 1).overlay(overlay).light(light).normal(0, 0, 1).next();
        buffer.vertex(mat, minX, minY, 0).color(0, 0, 0, 255).texture(0, 1).overlay(overlay).light(light).normal(0, 0, 1).next();

        matrices.pop();
    }
}