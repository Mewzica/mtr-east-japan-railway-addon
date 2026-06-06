package com.magikal_hakase.blocks;

import mtr.SoundEvents;
import mtr.block.BlockDirectionalDoubleBlockBase;
import mtr.block.BlockTicketProcessor;
import mtr.block.IBlock;
import mtr.data.TicketSystem;
import mtr.mappings.Utilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockEastJapanTicketProcessor extends BlockDirectionalDoubleBlockBase {
    public boolean hasLight;
    public boolean canEnter;
    public boolean canExit;
    public static final EnumProperty<BlockTicketProcessor.EnumTicketProcessorLights> LIGHTS = EnumProperty.of("lights", BlockTicketProcessor.EnumTicketProcessorLights.class);

    public BlockEastJapanTicketProcessor(boolean hasLight, boolean canEnter, boolean canExit) {
        super(Settings.of(Material.METAL, MapColor.GRAY).requiresTool().strength(2.0F).luminance((state) -> 5).nonOpaque());
        this.hasLight = hasLight;
        this.canEnter = canEnter;
        this.canExit = canExit;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand interactionHand, BlockHitResult blockHitResult) {
        if (!world.isClient && IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER) {
            TicketSystem.EnumTicketBarrierOpen open = TicketSystem.passThrough(world, pos, player, this.canEnter, this.canExit, SoundEvents.TICKET_PROCESSOR_ENTRY, SoundEvents.TICKET_PROCESSOR_ENTRY_CONCESSIONARY, SoundEvents.TICKET_PROCESSOR_EXIT, SoundEvents.TICKET_PROCESSOR_EXIT_CONCESSIONARY, SoundEvents.TICKET_PROCESSOR_FAIL, true);
            world.setBlockState(pos, (BlockState)state.with(LIGHTS, open.isOpen() ? BlockTicketProcessor.EnumTicketProcessorLights.GREEN : BlockTicketProcessor.EnumTicketProcessorLights.RED));
            Utilities.scheduleBlockTick(world, pos, this, 20);
        }

        return ActionResult.SUCCESS;
    }

    public void tick(BlockState state, ServerWorld world, BlockPos pos) {
        if (this.hasLight) {
            world.setBlockState(pos, (BlockState)state.with(LIGHTS, BlockTicketProcessor.EnumTicketProcessorLights.NONE));
        }

    }

    public VoxelShape getOutlineShape(BlockState state, BlockView blockGetter, BlockPos pos, ShapeContext collisionContext) {
        Direction facing = (Direction)IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER ? VoxelShapes.union(IBlock.getVoxelShapeByDirection((double)2.0F, (double)0.0F, (double)2.0F, (double)14.0F, (double)16.0F, (double)14.0F, facing)) : VoxelShapes.union(IBlock.getVoxelShapeByDirection((double)2.0F, (double)16.0F, (double)2.0F, (double)14.0F, (double)32.0F, (double)14.0F, facing));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, HALF, LIGHTS});
    }

    public static enum EnumTicketProcessorLights implements StringIdentifiable {
        NONE("none"),
        RED("red"),
        GREEN("green");

        private final String name;

        private EnumTicketProcessorLights(String nameIn) {
            this.name = nameIn;
        }

        public String asString() {
            return this.name;
        }
    }
}