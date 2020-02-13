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

package com.hrznstudio.galacticraft.blocks.machines.coalgenerator;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.MachineBlock;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.api.wire.WireNetwork;
import com.hrznstudio.galacticraft.container.GalacticraftContainers;
import com.hrznstudio.galacticraft.util.Rotatable;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CoalGeneratorBlock extends ConfigurableElectricMachineBlock implements AttributeProvider, Rotatable, WireConnectable, MachineBlock {

    private static final EnumProperty<SideOption> FRONT_SIDE_OPTION = EnumProperty.of("north", SideOption.class, SideOption.BLANK, SideOption.POWER_OUTPUT);
    private static final EnumProperty<SideOption> BACK_SIDE_OPTION = EnumProperty.of("south", SideOption.class, SideOption.BLANK, SideOption.POWER_OUTPUT);
    private static final EnumProperty<SideOption> RIGHT_SIDE_OPTION = EnumProperty.of("east", SideOption.class, SideOption.BLANK, SideOption.POWER_OUTPUT);
    private static final EnumProperty<SideOption> LEFT_SIDE_OPTION = EnumProperty.of("west", SideOption.class, SideOption.BLANK, SideOption.POWER_OUTPUT);
    private static final EnumProperty<SideOption> TOP_SIDE_OPTION = EnumProperty.of("up", SideOption.class, SideOption.BLANK, SideOption.POWER_OUTPUT);
    private static final EnumProperty<SideOption> BOTTOM_SIDE_OPTION = EnumProperty.of("down", SideOption.class, SideOption.BLANK, SideOption.POWER_OUTPUT);
    private final static DirectionProperty FACING = DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

    public CoalGeneratorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder) {
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
                .with(FRONT_SIDE_OPTION, SideOption.BLANK)
                .with(BACK_SIDE_OPTION, SideOption.BLANK)
                .with(RIGHT_SIDE_OPTION, SideOption.BLANK)
                .with(LEFT_SIDE_OPTION, SideOption.BLANK)
                .with(TOP_SIDE_OPTION, SideOption.BLANK)
                .with(BOTTOM_SIDE_OPTION, SideOption.BLANK);
    }

    @Override
    public ConfigurableElectricMachineBlockEntity createBlockEntity(BlockView blockView) {
        return new CoalGeneratorBlockEntity();
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) return true;
        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftContainers.COAL_GENERATOR_CONTAINER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
        return true;
    }

    @Override
    public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
        Direction dir = to.getSearchDirection();
        if (dir != null) return;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CoalGeneratorBlockEntity)) return;
        CoalGeneratorBlockEntity generator = (CoalGeneratorBlockEntity) be;
        to.offer(generator.getEnergyAttribute());
        generator.getExposedInventory().offerSelfAsAttribute(to, null, null);
    }

    @Override
    public Text machineInfo(ItemStack itemStack_1, BlockView blockView_1, TooltipContext tooltipContext_1) {
        return new TranslatableText("tooltip.galacticraft-rewoven.coal_generator");
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);

        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof CoalGeneratorBlockEntity) {
                CoalGeneratorBlockEntity coalGeneratorBlockEntity = (CoalGeneratorBlockEntity) blockEntity;

                for (int i = 0; i < coalGeneratorBlockEntity.getInventory().getSlotCount(); i++) {
                    ItemStack itemStack = coalGeneratorBlockEntity.getInventory().getInvStack(i);

                    if (itemStack != null) {
                        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), itemStack));
                    }
                }
            }
        }
    }

    @Override
    public WireNetwork.WireConnectionType canWireConnect(IWorld world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        return super.canWireConnect(world, opposite, connectionSourcePos, connectionTargetPos);
    }

    @Override
    public List<Direction> disabledSides() {
        return new ArrayList<>();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(BlockState blockState_1, World world_1, BlockPos blockPos_1, Random random_1) {
        if (world_1.getBlockEntity(blockPos_1) instanceof CoalGeneratorBlockEntity && ((CoalGeneratorBlockEntity) world_1.getBlockEntity(blockPos_1)).status == CoalGeneratorStatus.ACTIVE || ((CoalGeneratorBlockEntity) world_1.getBlockEntity(blockPos_1)).status == CoalGeneratorStatus.WARMING) {
            double double_1 = (double)blockPos_1.getX() + 0.5D;
            double double_2 = blockPos_1.getY();
            double double_3 = (double)blockPos_1.getZ() + 0.5D;
            if (random_1.nextDouble() < 0.1D) {
                world_1.playSound(double_1, double_2, double_3, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction_1 = blockState_1.get(FACING);
            Direction.Axis direction$Axis_1 = direction_1.getAxis();
            double double_4 = 0.52D;
            double double_5 = random_1.nextDouble() * 0.6D - 0.3D;
            double double_6 = direction$Axis_1 == Direction.Axis.X ? (double)direction_1.getOffsetX() * 0.52D : double_5;
            double double_7 = random_1.nextDouble() * 6.0D / 16.0D;
            double double_8 = direction$Axis_1 == Direction.Axis.Z ? (double)direction_1.getOffsetZ() * 0.52D : double_5;
            world_1.addParticle(ParticleTypes.SMOKE, double_1 + double_6, double_2 + double_7, double_3 + double_8, 0.0D, 0.0D, 0.0D);
            world_1.addParticle(ParticleTypes.FLAME, double_1 + double_6, double_2 + double_7, double_3 + double_8, 0.0D, 0.0D, 0.0D);

        }
    }
}
