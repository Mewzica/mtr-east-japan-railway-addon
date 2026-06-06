package com.magikal_hakase.renderer;

import com.magikal_hakase.blocks.BlockJREastVerticalSign;
import mtr.block.BlockStationNameBase;
import mtr.client.ClientData;
import mtr.data.Station;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.lang.reflect.Field;
import java.util.Set;

public class RenderJREastVerticalSign implements BlockEntityRenderer<BlockJREastVerticalSign.TileEntityJREastVerticalSign> {

    private final TextRenderer textRenderer;
    private static Field stationsField;
    private static boolean hasLoggedError = false;

    // --- 調整用パラメータ ---
    private static final float Z_OFFSET = -0.43f;
    private static final float Y_OFFSET = 0.0f;
    private static final float TEXT_SCALE = 0.015F;
    private static final float CHAR_SPACING = 11.0f;

    // 帯のサイズ設定
    private static final float STRIP_WIDTH = 8.0f;
    private static final float STRIP_TOP = -24.0f;
    private static final float STRIP_BOTTOM = -10.0f;

    // static初期化ブロック：起動時に一度だけフィールドを探す
    static {
        try {
            if (ClientData.DATA_CACHE != null) {
                // 【修正点】RailwayData.class 固定ではなく、DATA_CACHE のクラスからフィールドを探す
                // 親クラス(DataCache等)も遡って "stations" を探します
                Class<?> clazz = ClientData.DATA_CACHE.getClass();
                while (clazz != null) {
                    try {
                        stationsField = clazz.getDeclaredField("stations");
                        stationsField.setAccessible(true);
                        // 見つかったらループ終了
                        break;
                    } catch (NoSuchFieldException e) {
                        // このクラスになければ親クラスへ
                        clazz = clazz.getSuperclass();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RenderJREastVerticalSign(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(BlockJREastVerticalSign.TileEntityJREastVerticalSign entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockPos pos = entity.getPos();
        Station station = null;
        String debugStatus = "";

        // 1. 駅データの取得
        if (ClientData.DATA_CACHE == null) {
            debugStatus = "読み込み中|Loading...";
        } else if (stationsField == null) {
            // staticブロックでの取得に失敗している場合、ここで再試行またはエラー表示
            debugStatus = "Field取得失敗|Reflect Fail";
            // 再取得を試みるロジックを入れても良いですが、通常はstaticで取れているはずです
        } else {
            try {
                @SuppressWarnings("unchecked")
                Set<Station> stations = (Set<Station>) stationsField.get(ClientData.DATA_CACHE);

                if (stations == null) {
                    debugStatus = "データなし|No Data";
                } else {
                    for (Station s : stations) {
                        if (s.inArea(pos.getX(), pos.getZ())) {
                            station = s;
                            break;
                        }
                    }
                    if (station == null) {
                        debugStatus = String.format("エリア外|Pos: %d,%d", pos.getX(), pos.getZ());
                    }
                }
            } catch (Exception e) {
                if (!hasLoggedError) {
                    // コンソールに詳細なエラーを一度だけ出す
                    System.out.println("[MTR Addon] Data Access Error:");
                    e.printStackTrace();
                    hasLoggedError = true;
                }
                debugStatus = "内部エラー|Error";
            }
        }

        // 2. 表示内容の決定
        String stationName;
        int color;

        if (station != null) {
            stationName = station.name;
            color = station.color;
        } else {
            stationName = debugStatus;
            color = 0x888888;
        }

        String[] nameSplit = stationName.split("\\|");
        String jpName = nameSplit[0];
        String enName = nameSplit.length > 1 ? nameSplit[1] : "";

        BlockState state = entity.getCachedState();
        Direction facing = state.get(BlockStationNameBase.FACING); //

        matrices.push();

        // --- 座標変換 ---
        // 中心へ移動
        matrices.translate(0.5, 0.7 + Y_OFFSET, 0.5); // Y座標の基準位置を調整 (0.7 -> 0.5 に変更し、Y_OFFSETで微調整)

        // ★修正ポイント: 向きに応じた回転角度を明示的に指定
        float angle = 0;
        switch (facing) {
            case NORTH: angle = 180;   break; // そのまま
            case SOUTH: angle = 0; break; // 反転
            case EAST:  angle = 90; break; // 90度左回転 (-90)
            case WEST:  angle = 270;  break; // 90度右回転
        }

        // Y軸回転を適用
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angle));

        // Z軸方向へのオフセット (看板の表面に文字が出るように調整)
        // モデルの厚みや原点位置によっては、この値を微調整する必要があります
        matrices.translate(0, 0, Z_OFFSET); // Z_OFFSET は -0.43f 程度

        // テキストのスケールと上下反転 (MinecraftのフォントレンダラはY軸が下向き正なので、上下反転させるのが一般的)
        matrices.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);

        // --- 描画実行 ---
        drawColorRect(matrices, vertexConsumers, -STRIP_WIDTH, STRIP_TOP, STRIP_WIDTH, STRIP_BOTTOM, color, light);

        int textColor = 0x000000;
        float yPos = -6.0f;

        for (int i = 0; i < jpName.length(); i++) {
            String charStr = String.valueOf(jpName.charAt(i));
            float charWidth = this.textRenderer.getWidth(charStr);
            this.textRenderer.draw(charStr, -charWidth / 2.0f, yPos, textColor, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
            yPos += CHAR_SPACING;
        }

        if (!enName.isEmpty()) {
            matrices.push();
            float enScale = 0.6f;
            matrices.scale(enScale, enScale, enScale);
            float enWidth = this.textRenderer.getWidth(enName);
            float enY = (yPos + 5) / enScale;
            if (enY < 45.0f / enScale) enY = 45.0f / enScale;

            this.textRenderer.draw(enName, -enWidth / 2.0f, enY, textColor, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
            matrices.pop();
        }

        matrices.pop();
    }

    private void drawColorRect(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x1, float y1, float x2, float y2, int color, int light) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getText(new Identifier("minecraft", "textures/block/white_concrete.png")));

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 1.0f;

        buffer.vertex(matrix, x1, y2, 0).color(r, g, b, a).texture(0, 0).light(light).next();
        buffer.vertex(matrix, x2, y2, 0).color(r, g, b, a).texture(0, 0).light(light).next();
        buffer.vertex(matrix, x2, y1, 0).color(r, g, b, a).texture(0, 0).light(light).next();
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).texture(0, 0).light(light).next();
    }
}