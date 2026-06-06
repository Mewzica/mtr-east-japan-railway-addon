package com.magikal_hakase.renderer;

import com.magikal_hakase.blockentities.TileEntityEastJapanPIDS;
import mtr.client.ClientData;
import mtr.MTRClient;
import mtr.data.*;
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.util.*;
import java.util.stream.Collectors; // ★追加

public class RenderEastJapanPIDS extends BlockEntityRendererMapper<TileEntityEastJapanPIDS> {

    // LED部分の色（固定）
    private static final int COLOR_ORANGE = 0xFFFF8800;
    private static final int COLOR_GREEN = 0xFF00FF00;
    private static final int COLOR_RED = 0xFFFF0000;

    // 看板部分の文字色（白）
    private static final int COLOR_WHITE = 0xFFFFFFFF;

    private static final int SWITCH_LANGUAGE_TICKS = 140;

    public RenderEastJapanPIDS(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getRenderDispatcher());
    }

    @Override
    public void render(TileEntityEastJapanPIDS entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;

        BlockPos pos = entity.getPos();
        Direction facing = entity.getCachedState().get(HorizontalFacingBlock.FACING);

        Vec3f renderNormal = new Vec3f(0, 0, 1);
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
        renderNormal.transform(normalMatrix);

        Vec3f blockFacingVec = facing.getUnitVector();
        float dot = renderNormal.dot(blockFacingVec);
        boolean isBack = dot < 0;

        // [表面] / [裏面] の設定
        float zOffsetFront = 11.0f;
        float xOffsetFront = 0.5f;
        float zOffsetBack = 11.0f;
        float xOffsetBack = 0.5f;

        float zOffset = isBack ? zOffsetBack : zOffsetFront;
        float xOffset = isBack ? xOffsetBack : xOffsetFront;

        // --- データ取得フェーズ ---
        String platformNum = "";
        String[] lineNames = {"", ""};
        int lineColor = 0xFF444444;
        String[] destNames = {"", ""};

        try {
            // ★修正: ブラシで選択された番線(複数)に対応
            Set<Long> platformIds = entity.getPlatformIds(); // ブラシで設定されたIDセットを取得
            List<String> platformNameList = new ArrayList<>();

            if (!platformIds.isEmpty()) {
                // 1. ブラシ設定がある場合: 選択された全番線を取得
                for (Long id : platformIds) {
                    Platform platform = ClientData.DATA_CACHE.platformIdMap.get(id);
                    if (platform != null) {
                        platformNameList.add(platform.name);
                    }
                }
                // 見栄え良くするためソート (例: "2", "1" -> "1", "2")
                Collections.sort(platformNameList);
            } else {
                // 2. ブラシ設定がない場合: 従来通り最寄りの番線を自動検知
                long platformId = entity.getPlatformId(ClientData.PLATFORMS, ClientData.DATA_CACHE);
                if (platformId != 0) {
                    Platform platform = ClientData.DATA_CACHE.platformIdMap.get(platformId);
                    if (platform != null) {
                        platformNameList.add(platform.name);
                    }
                }
            }

            // 番線名を中黒「・」で結合 (例: "1・2")
            if (!platformNameList.isEmpty()) {
                platformNum = String.join("·", platformNameList);
            }


            // スケジュール情報の取得 (行先や路線名の決定)
            Map<Long, String> dummyMap = new HashMap<>();
            List<ScheduleEntry> schedules = getSchedules(entity, pos, dummyMap);

            if (!schedules.isEmpty()) {
                ScheduleEntry firstSchedule = schedules.get(0);
                Route route = ClientData.DATA_CACHE.routeIdMap.get(firstSchedule.routeId);
                if (route != null) {
                    lineNames = route.name.split("\\|");
                    lineColor = route.color + 0xFF000000;

                    if (!route.platformIds.isEmpty()) {
                        long lastPlatformId = route.platformIds.get(route.platformIds.size() - 1).platformId;
                        Station lastStation = ClientData.DATA_CACHE.platformIdToStation.get(lastPlatformId);
                        if (lastStation != null) {
                            String[] stationSplit = lastStation.name.split("\\|");
                            destNames = new String[stationSplit.length];
                            for(int i=0; i<stationSplit.length; i++) {
                                if (i == 0) destNames[i] = stationSplit[i] + " 方面";
                                else destNames[i] = "For " + stationSplit[i];
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lineNames.length < 2) lineNames = new String[]{lineNames[0], lineNames[0]};
        if (destNames.length < 2 && destNames.length > 0) destNames = new String[]{destNames[0], destNames[0]};
        else if (destNames.length == 0) destNames = new String[]{"", ""};

        // --- 描画フェーズ ---
        matrices.push();
        matrices.translate(0.5, 0.2, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.asRotation() + 90));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
        matrices.translate(xOffset, 0, 0);

        float scale = 0.015F;
        matrices.scale(scale, scale, scale);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int lightLED = 0xF000F0;

        matrices.push();
        matrices.translate(0, -25, -zOffset);

        renderHeader(matrices, textRenderer, vertexConsumers, light, platformNum, lineNames, lineColor, destNames);
        renderLED(matrices, textRenderer, vertexConsumers, entity, pos, lightLED);

        matrices.pop();
        matrices.pop();
    }

    // 看板部分
    private void renderHeader(MatrixStack matrices, TextRenderer textRenderer, VertexConsumerProvider vertexConsumers, int light, String platformNum, String[] lineNames, int lineColor, String[] destNames) {

        float y = -7;
        float x_platform = -55;
        float x_line     = -19;
        float x_bar      = -1;
        float x_dest     = 30;

        float barWidth   = 4;
        float barHeight  = 13;

        // 1. 番線 (拡大)
        matrices.push();
        float platformScale = 2.2f;
        // 文字数が多い場合(1・2など)は少しスケールを調整するか、DrawTextFitが自動で縮小してくれるのに任せる
        matrices.translate(x_platform, y, 0);
        matrices.scale(platformScale, platformScale, 1.0f);
        // 番線表示エリアの幅制限(15)を指定しているので、文字が増えても自動で長体がかかって収まります
        drawTextFit(matrices, textRenderer, vertexConsumers, platformNum, 0, -2, COLOR_WHITE, light, 15);
        matrices.pop();

        // 2. 路線名
        drawTextFit(matrices, textRenderer, vertexConsumers, lineNames[0], x_line, y - 2, COLOR_WHITE, light, 30);
        matrices.push();
        matrices.translate(x_line, y + 7, 0);
        matrices.scale(0.6f, 0.6f, 1.0f);
        drawTextFit(matrices, textRenderer, vertexConsumers, lineNames[1], 0, 0, COLOR_WHITE, light, 50);
        matrices.pop();

        // 3. カラーバー
        drawRect(matrices, vertexConsumers, x_bar, y - 1, barWidth, barHeight, lineColor, light);

        // 4. 行先
        drawTextFit(matrices, textRenderer, vertexConsumers, destNames[0], x_dest, y - 2, COLOR_WHITE, light, 35);
        matrices.push();
        matrices.translate(x_dest, y + 7, 0);
        matrices.scale(0.6f, 0.6f, 1.0f);
        drawTextFit(matrices, textRenderer, vertexConsumers, destNames[1], 0, 0, COLOR_WHITE, light, 58);
        matrices.pop();
    }

    // LED部分 (既存のまま)
    private void renderLED(MatrixStack matrices, TextRenderer textRenderer, VertexConsumerProvider vertexConsumers, TileEntityEastJapanPIDS entity, BlockPos pos, int light) {
        // ... (以前のコードと同じなので省略。ただしgetSchedulesはentity.getPlatformIds()を考慮しているため、フィルタリングは正常に動作します) ...
        try {
            Map<Long, String> dummyMap = new HashMap<>();
            List<ScheduleEntry> schedules = getSchedules(entity, pos, dummyMap);

            int maxArrivals = 2;
            int languageIndex = (int)Math.floor((double)MTRClient.getGameTick() / SWITCH_LANGUAGE_TICKS);
            boolean isArriving = false;
            long currentTime = System.currentTimeMillis();

            if (!schedules.isEmpty()) {
                long timeLeft = schedules.get(0).arrivalMillis - System.currentTimeMillis();
                if (timeLeft <= 25000 && timeLeft > 0) {
                    isArriving = true;
                }
            }

            for (int i = 0; i < maxArrivals; i++) {
                float y = i * 12 + 11;

                if (i >= schedules.size()) {
                    if (i == 1 && isArriving) {
                        boolean blink = (System.currentTimeMillis() / 500) % 2 == 0;
                        if (blink) {
                            String arriveText = (languageIndex % 2 == 0) ? "列車がまいります" : "Train Approaching";
                            drawTextFit(matrices, textRenderer, vertexConsumers, arriveText, 0, y, COLOR_RED, light, 80);
                        }
                    }
                    continue;
                }

                if (i == 1 && isArriving) {
                    boolean blink = (System.currentTimeMillis() / 500) % 2 == 0;
                    if (blink) {
                        String arriveText = (languageIndex % 2 == 0) ? "列車がまいります" : "Train Approaching";
                        drawTextFit(matrices, textRenderer, vertexConsumers, arriveText, 0, y, COLOR_RED, light, 80);
                    }
                    continue;
                }

                ScheduleEntry schedule = schedules.get(i);
                Route route = ClientData.DATA_CACHE.routeIdMap.get(schedule.routeId);
                if (route == null) continue;

                String[] typeSplit = route.name.split("\\|");
                String typeText = typeSplit[languageIndex % typeSplit.length];
                String[] destSplit = ClientData.DATA_CACHE.getFormattedRouteDestination(route, schedule.currentStationIndex, "").split("\\|");
                String destText = destSplit[languageIndex % destSplit.length];

                String timeText;
                long timeLeftForEntry = schedule.arrivalMillis - currentTime;

                if (timeLeftForEntry <= 0) {
                    timeText = (languageIndex % 2 == 0) ? "現在時刻" : "Current time";
                } else {
                    timeText = getGameTime(entity.getWorld(), schedule.arrivalMillis);
                }

                drawTextFit(matrices, textRenderer, vertexConsumers, typeText, -43, y, COLOR_GREEN, light, 28);
                drawTextFit(matrices, textRenderer, vertexConsumers, destText, -3, y, COLOR_ORANGE, light, 48);
                drawTextFit(matrices, textRenderer, vertexConsumers, timeText, 45, y, COLOR_GREEN, light, 30);
            }

            if (schedules.isEmpty()) {
                drawTextFit(matrices, textRenderer, vertexConsumers, "発着予定はありません", 0, 10, COLOR_ORANGE, light, 100);
            }
        } catch (Exception e) {}
    }

    private void drawTextFit(MatrixStack matrices, TextRenderer textRenderer, VertexConsumerProvider vertexConsumers, String text, float x, float y, int color, int light, float maxWidth) {
        if (text == null || text.isEmpty()) return;
        matrices.push();
        int textWidth = textRenderer.getWidth(text);
        matrices.translate(x, y, 0);
        if (textWidth > maxWidth) {
            float scale = maxWidth / (float) textWidth;
            matrices.scale(scale, 1.0f, 1.0f);
        }
        textRenderer.draw(Text.of(text), -textWidth / 2.0f, 0, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
        matrices.pop();
    }

    private void drawRect(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float width, float height, int colorRGB, int light) {
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getText(new Identifier("minecraft", "textures/block/white_concrete.png")));
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float r = ((colorRGB >> 16) & 0xFF) / 255.0f;
        float g = ((colorRGB >> 8) & 0xFF) / 255.0f;
        float b = (colorRGB & 0xFF) / 255.0f;
        float a = 1.0f;
        buffer.vertex(matrix, x, y + height, 0).color(r, g, b, a).texture(0, 1).light(light).next();
        buffer.vertex(matrix, x + width, y + height, 0).color(r, g, b, a).texture(1, 1).light(light).next();
        buffer.vertex(matrix, x + width, y, 0).color(r, g, b, a).texture(1, 0).light(light).next();
        buffer.vertex(matrix, x, y, 0).color(r, g, b, a).texture(0, 0).light(light).next();
    }

    private String getGameTime(net.minecraft.world.World world, long arrivalMillis) {
        if (world == null) return "--:--";
        long delayMs = arrivalMillis - System.currentTimeMillis();
        if (delayMs < 0) delayMs = 0;
        long ticksToAdd = delayMs / 50;
        long currentTick = world.getTimeOfDay();
        long targetTick = (currentTick + ticksToAdd) % 24000;
        if (targetTick < 0) targetTick += 24000;
        int totalHours = (int) (targetTick / 1000) + 6;
        int hours = totalHours % 24;
        int minutes = (int) ((targetTick % 1000) * 60 / 1000);
        return String.format("%02d:%02d", hours, minutes);
    }

    public List<ScheduleEntry> getSchedules(TileEntityEastJapanPIDS entity, BlockPos pos, Map<Long, String> platformIdToName) {
        Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, pos);
        if (station == null) { return new ArrayList<>(); }
        Map<Long, Platform> platforms = ClientData.DATA_CACHE.requestStationIdToPlatforms(station.id);
        if (platforms.isEmpty()) { return new ArrayList<>(); }
        Set<Long> platformIds = entity.getPlatformIds();
        Set<ScheduleEntry> schedules = new HashSet<>();
        platforms.values().forEach((platform) -> {
            if (platformIds.isEmpty() || platformIds.contains(platform.id)) {
                Set<ScheduleEntry> scheduleForPlatform = ClientData.SCHEDULES_FOR_PLATFORM.get(platform.id);
                if (scheduleForPlatform != null) {
                    scheduleForPlatform.forEach((scheduleEntry) -> {
                        Route route = ClientData.DATA_CACHE.routeIdMap.get(scheduleEntry.routeId);
                        if (route != null && scheduleEntry.currentStationIndex < route.platformIds.size() - 1) {
                            schedules.add(scheduleEntry);
                            platformIdToName.put(platform.id, platform.name);
                        }
                    });
                }
            }
        });
        List<ScheduleEntry> scheduleList = new ArrayList<>(schedules);
        Collections.sort(scheduleList);
        return scheduleList;
    }
}