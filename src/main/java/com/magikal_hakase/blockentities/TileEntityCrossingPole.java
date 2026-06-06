package com.magikal_hakase.blockentities;

import com.magikal_hakase.blocks.BlockCrossingPole;
import com.magikal_hakase.data.EnumCrossingAttachment;
import com.magikal_hakase.data.ModSounds;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityCrossingPole extends BlockEntityMapper {

    private int soundTimer = 0;

    public TileEntityCrossingPole(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CROSSING_POLE, pos, state);
    }

    // サーバーとクライアントの両方、あるいはサーバーのみで呼ばれるTickメソッド
    public static void tick(World world, BlockPos pos, BlockState state, TileEntityCrossingPole entity) {
        if (world.isClient) return; // 音の再生は通常サーバー側でトリガーして全クライアントへ送る

        // 柱が動力を持っていなければタイマーをリセットして終了
        if (!state.get(BlockCrossingPole.POWERED)) {
            entity.soundTimer = 0;
            return;
        }

        // この柱の東西南北どこかにスピーカーがついているかチェック
        boolean hasSpeaker =
                state.get(BlockCrossingPole.NORTH) == EnumCrossingAttachment.SPEAKER ||
                        state.get(BlockCrossingPole.EAST) == EnumCrossingAttachment.SPEAKER ||
                        state.get(BlockCrossingPole.SOUTH) == EnumCrossingAttachment.SPEAKER ||
                        state.get(BlockCrossingPole.WEST) == EnumCrossingAttachment.SPEAKER;

        if (hasSpeaker) {
            entity.soundTimer++;

            // 例: 10ティック(0.5秒)ごとに音を鳴らす
            if (entity.soundTimer >= 9) {
                // ★カスタムの警報音がある場合は、ModSounds.CROSSING_BELL などに置き換えてください
                SoundEvent sound = ModSounds.CROSSING_ALARM;

                // 1.0f(音量), 1.0f(ピッチ)
                world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.5f, 1.0f);

                entity.soundTimer = 0; // タイマーリセット
            }
        } else {
            entity.soundTimer = 0;
        }
    }
}