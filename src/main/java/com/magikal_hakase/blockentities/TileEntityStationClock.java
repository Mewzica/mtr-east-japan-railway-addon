package com.magikal_hakase.blockentities;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class TileEntityStationClock extends BlockEntityMapper {
    public TileEntityStationClock(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STATION_CLOCK, pos, state);
    }
}