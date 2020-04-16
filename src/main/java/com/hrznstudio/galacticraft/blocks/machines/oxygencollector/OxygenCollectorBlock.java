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

package com.hrznstudio.galacticraft.blocks.machines.oxygencollector;

import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.MachineBlock;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.container.GalacticraftContainers;
import com.hrznstudio.galacticraft.util.Rotatable;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCollectorBlock extends ConfigurableElectricMachineBlock implements Rotatable, MachineBlock {
    private static final EnumProperty<SideOption> FRONT_SIDE_OPTION = EnumProperty.of("north", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT, SideOption.OXYGEN_OUTPUT);
    private static final EnumProperty<SideOption> BACK_SIDE_OPTION = EnumProperty.of("south", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT, SideOption.OXYGEN_OUTPUT);
    private static final EnumProperty<SideOption> RIGHT_SIDE_OPTION = EnumProperty.of("east", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT, SideOption.OXYGEN_OUTPUT);
    private static final EnumProperty<SideOption> LEFT_SIDE_OPTION = EnumProperty.of("west", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT, SideOption.OXYGEN_OUTPUT);
    private static final EnumProperty<SideOption> TOP_SIDE_OPTION = EnumProperty.of("up", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT, SideOption.OXYGEN_OUTPUT);
    private static final EnumProperty<SideOption> BOTTOM_SIDE_OPTION = EnumProperty.of("down", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT, SideOption.OXYGEN_OUTPUT);
    private static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public OxygenCollectorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftContainers.OXYGEN_COLLECTOR_CONTAINER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean isTranslucent(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return false;
    }

    @Override
    public Text machineInfo(ItemStack itemStack_1, BlockView blockView_1, TooltipContext tooltipContext_1) {
        return new TranslatableText("tooltip.galacticraft-rewoven.oxygen_collector");
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof OxygenCollectorBlockEntity) {
                OxygenCollectorBlockEntity be = (OxygenCollectorBlockEntity) blockEntity;

                for (int i = 0; i < be.getInventory().getSlotCount(); i++) {
                    ItemStack itemStack = be.getInventory().getInvStack(i);

                    if (itemStack != null) {
                        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), itemStack));
                    }
                }
            }
        }
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
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
        return true;
    }

    @Override
    public boolean consumesPower() {
        return true;
    }

    @Override
    public boolean generatesPower() {
        return false;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite())
                .with(FRONT_SIDE_OPTION, SideOption.BLANK)
                .with(BACK_SIDE_OPTION, SideOption.BLANK)
                .with(RIGHT_SIDE_OPTION, SideOption.BLANK)
                .with(LEFT_SIDE_OPTION, SideOption.BLANK)
                .with(TOP_SIDE_OPTION, SideOption.BLANK)
                .with(BOTTOM_SIDE_OPTION, SideOption.BLANK);
    }

    @Override
    public ConfigurableElectricMachineBlockEntity createBlockEntity(BlockView blockView) {
        return new OxygenCollectorBlockEntity();
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
    public void randomDisplayTick(BlockState blockState_1, World world, BlockPos pos, Random random_1) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof OxygenCollectorBlockEntity)) {
            return;
        }

        OxygenCollectorBlockEntity collector = (OxygenCollectorBlockEntity) blockEntity;
        if (collector.collectionAmount > 0) {
            for (int particleCount = 0; particleCount < 10; particleCount++) {
                Random random = world.random;

                for (int int_1 = 0; int_1 < 32; ++int_1) {
                    world.addParticle(
                            new DustParticleEffect(0.9f, 0.9f, 1.0f, 1.0F),
                            pos.getX() + 0.5D,
                            (random.nextFloat() - 0.5D) * 0.5D + /*random.nextDouble() * 2.0D*/ 0.5D,
                            pos.getZ() + 0.5D,
                            random.nextGaussian(),
                            0.0D,
                            random.nextGaussian());
                }
            }
        }
    }

    @Nonnull
    @Override
    public WireConnectionType canWireConnect(IWorld world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        return super.canWireConnect(world, opposite, connectionSourcePos, connectionTargetPos);
    }

    @Override
    public List<Direction> disabledSides() {
        return new ArrayList<>();
    }
}