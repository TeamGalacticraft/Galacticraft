/*
 * Copyright (c) 2019 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.block.machines;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.MachineBlock;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.block.entity.BasicSolarPanelBlockEntity;
import com.hrznstudio.galacticraft.block.entity.BasicSolarPanelPartBlockEntity;
import com.hrznstudio.galacticraft.screen.GalacticraftScreenHandlers;
import com.hrznstudio.galacticraft.util.MultiBlock;
import com.hrznstudio.galacticraft.util.Rotatable;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelBlock extends ConfigurableElectricMachineBlock implements AttributeProvider, Rotatable, MultiBlock, MachineBlock {

    private static final EnumProperty<SideOption> FRONT_SIDE_OPTION = EnumProperty.of("north", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT);
    private static final EnumProperty<SideOption> BACK_SIDE_OPTION = EnumProperty.of("south", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT);
    private static final EnumProperty<SideOption> RIGHT_SIDE_OPTION = EnumProperty.of("east", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT);
    private static final EnumProperty<SideOption> LEFT_SIDE_OPTION = EnumProperty.of("west", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT);
    private static final EnumProperty<SideOption> TOP_SIDE_OPTION = EnumProperty.of("up", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT);
    private static final EnumProperty<SideOption> BOTTOM_SIDE_OPTION = EnumProperty.of("down", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT);

    public BasicSolarPanelBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Property<SideOption> getProperty(@Nonnull BlockFace direction) {
        switch (direction) {
            case FRONT:
                return FRONT_SIDE_OPTION;
            case RIGHT:
                return RIGHT_SIDE_OPTION;
            case LEFT:
                return LEFT_SIDE_OPTION;
            case BACK:
                return BACK_SIDE_OPTION;
            case TOP:
                return TOP_SIDE_OPTION;
            case BOTTOM:
                return BOTTOM_SIDE_OPTION;
        }
        throw new NullPointerException();
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        super.appendProperties(stateBuilder);
        stateBuilder.add(FACING);

        stateBuilder.add(FRONT_SIDE_OPTION);
        stateBuilder.add(BACK_SIDE_OPTION);
        stateBuilder.add(RIGHT_SIDE_OPTION);
        stateBuilder.add(LEFT_SIDE_OPTION);
        stateBuilder.add(TOP_SIDE_OPTION);
        stateBuilder.add(BOTTOM_SIDE_OPTION);
    }

    @Override
    public boolean consumesOxygen() {
        return false;
    }

    @Override
    public boolean generatesOxygen() {
        return false;
    }

    @Override
    public boolean consumesPower() {
        return false;
    }

    @Override
    public boolean generatesPower() {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite())
                .with(FRONT_SIDE_OPTION, SideOption.DEFAULT)
                .with(BACK_SIDE_OPTION, SideOption.DEFAULT)
                .with(RIGHT_SIDE_OPTION, SideOption.DEFAULT)
                .with(LEFT_SIDE_OPTION, SideOption.DEFAULT)
                .with(TOP_SIDE_OPTION, SideOption.DEFAULT)
                .with(BOTTOM_SIDE_OPTION, SideOption.DEFAULT);
    }

    @Override
    public ConfigurableElectricMachineBlockEntity createBlockEntity(BlockView blockView) {
        return new BasicSolarPanelBlockEntity();
    }

    @Override
    public boolean consumesFluids() {
        return false;
    }

    @Override
    public boolean generatesFluids() {
        return false;
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) return ActionResult.SUCCESS;
        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftScreenHandlers.BASIC_SOLAR_PANEL_SCREEN_HANDLER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
        return ActionResult.SUCCESS;
    }

    @Override
    public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
        Direction dir = to.getSearchDirection();
        if (dir == null) return;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof BasicSolarPanelBlockEntity)) return;
        BasicSolarPanelBlockEntity generator = (BasicSolarPanelBlockEntity) be;
        to.offer(generator.getCapacitatorComponent());
        to.offer(generator.getExposedInventory());
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);
        dropInventory(world, blockPos);

        for (BlockPos otherPart : getOtherParts(blockState, blockPos)) {
            world.setBlockState(otherPart, Blocks.AIR.getDefaultState(), 3);
        }

        super.onBroken(world, blockPos, blockState);
    }

    private void dropInventory(World world, BlockPos blockPos) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof BasicSolarPanelBlockEntity) {
                BasicSolarPanelBlockEntity basicSolarPanelBlockEntity = (BasicSolarPanelBlockEntity) blockEntity;

                for (int i = 0; i < basicSolarPanelBlockEntity.getInventory().getSlotCount(); i++) {
                    ItemStack itemStack = basicSolarPanelBlockEntity.getInventory().getInvStack(i);

                    if (itemStack != null) {
                        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), itemStack));
                    }
                }
            }
        }
    }

    @Override
    public List<BlockPos> getOtherParts(BlockState state, BlockPos pos) {
        List<BlockPos> parts = new LinkedList<>();
        BlockPos rod = pos.up();
        BlockPos mid = rod.up();
        BlockPos front = mid.north();
        BlockPos back = mid.south();

        BlockPos right = mid.east();
        BlockPos left = mid.west();

        BlockPos frontLeft = front.east();
        BlockPos frontRight = front.west();
        BlockPos backLeft = back.east();
        BlockPos backRight = back.west();

        parts.add(rod);
        parts.add(mid);
        parts.add(front);
        parts.add(back);

        parts.add(right);
        parts.add(left);

        parts.add(frontLeft);
        parts.add(frontRight);
        parts.add(backLeft);
        parts.add(backRight);

        return parts;
    }

    @Override
    public List<Direction> getDisabledConfigFaces() {
        return Collections.singletonList(Direction.UP);
    }

    @Override
    public boolean canPlaceAt(BlockState blockState_1, WorldView WorldView_1, BlockPos blockPos_1) {
        for (BlockPos otherPart : getOtherParts(blockState_1, blockPos_1)) {
            if (!WorldView_1.getBlockState(otherPart).getMaterial().isReplaceable()) {
                return false;
            }
        }
        return super.canPlaceAt(blockState_1, WorldView_1, blockPos_1);
    }

    @Override
    public void onPlaced(World world_1, BlockPos basePos, BlockState blockState_1, LivingEntity livingEntity_1, ItemStack itemStack_1) {
        for (BlockPos otherPart : getOtherParts(blockState_1, basePos)) {
            BlockState defaultState = GalacticraftBlocks.BASIC_SOLAR_PANEL_PART.getDefaultState();
            world_1.setBlockState(otherPart, defaultState);

            BlockEntity partEntity = world_1.getBlockEntity(otherPart);
            assert partEntity != null; // This will never be null because world.setBlockState will put a blockentity there.
            ((BasicSolarPanelPartBlockEntity) partEntity).setBasePos(basePos);
            partEntity.markDirty();
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState_1) {
        return PistonBehavior.BLOCK;
    }

    void onPartDestroyed(World world, BlockState partState, BlockPos partPos, BlockState baseState, BlockPos basePos, boolean dropBase) {
        dropInventory(world, basePos);
        world.breakBlock(basePos, dropBase);

        for (BlockPos otherPart : getOtherParts(baseState, basePos)) {
            if (!world.getBlockState(otherPart).isAir()) {
                world.setBlockState(otherPart, Blocks.AIR.getDefaultState(), 3);
            }
        }
    }

    @Override
    public Text machineInfo(ItemStack itemStack_1, BlockView blockView_1, TooltipContext tooltipContext_1) {
        return new TranslatableText("tooltip.galacticraft-rewoven.basic_solar_panel");
    }

    @Override
    public List<Direction> disabledSides() {
        return Collections.singletonList(Direction.UP);
    }


}