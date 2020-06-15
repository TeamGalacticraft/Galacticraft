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

package com.hrznstudio.galacticraft.block.entity;

import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import com.hrznstudio.galacticraft.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.Component;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FuelLoaderBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable, BlockEntityClientSerializable {

    public FuelLoaderStatus status = FuelLoaderStatus.NO_ROCKET;
    private final SimpleTankComponent tank = new SimpleTankComponent(1, Fraction.ofWhole(10));
    private BlockPos connectionPos = null;

    public FuelLoaderBlockEntity() {
        super(GalacticraftBlockEntities.FUEL_LOADER_TYPE);
        tank.listen(() -> {
            this.markDirty();
            if (!world.isClient)sync();
        });
    }

    @Override
    protected int getInventorySize() {
        return 3;
    }

    @Override
    public int getEnergyUsagePerTick() {
        return GalacticraftEnergy.Values.T1_MACHINE_ENERGY_USAGE;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == 0) {
            return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        } else if (slot == 1) {
            return stack -> ComponentProvider.fromItemStack(stack).hasComponent(UniversalComponents.TANK_COMPONENT) || stack.getItem() instanceof BucketItem;
        }
        return stack -> false;
    }

    @Override
    protected boolean canExtractEnergy() {
        return true;
    }

    @Override
    protected boolean canInsertEnergy() {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public FuelLoaderStatus getStatusForTooltip() {
        return status;
    }

    @Override
    public void tick() {
        trySpreadEnergy();
        attemptChargeFromStack(0);

        if (world.isClient || disabled()) {
            return;
        }

        if (this.getTank().getContents(0).getAmount().doubleValue() + 1.0D < tank.getMaxCapacity(0).doubleValue()) {
            if (getInventory().getStack(1).getItem() instanceof BucketItem) {
                if (((BucketItem) getInventory().getStack(1).getItem()).fluid == GalacticraftFluids.FUEL) {
                    getInventory().setStack(1, new ItemStack(Items.BUCKET));
                    this.tank.insertFluid(0, new FluidVolume(GalacticraftFluids.FUEL, Fraction.ONE), ActionType.PERFORM);
                }
            }
        }
        if (this.getTank().getContents(0).getAmount().compareTo(tank.getMaxCapacity(0)) < 0) {
            if (ComponentProvider.fromItemStack(getInventory().getStack(1)).hasComponent(UniversalComponents.TANK_COMPONENT)) {
                TankComponent component = ComponentProvider.fromItemStack(getInventory().getStack(1)).getComponent(UniversalComponents.TANK_COMPONENT);
                if (component.getContents(0).getFluid() == GalacticraftFluids.FUEL) {
                    tank.insertFluid(0, component.takeFluid(0, this.getTank().getMaxCapacity(0).subtract(tank.getContents(0).getAmount()), ActionType.PERFORM), ActionType.PERFORM);
                }
            }
        }

        if (this.connectionPos != null) {
            BlockEntity be = world.getBlockEntity(connectionPos);
            if (be instanceof RocketLaunchPadBlockEntity) {
                if (((RocketLaunchPadBlockEntity) be).hasRocket()) {
                    if (!tank.getContents(0).isEmpty()) {
                        if (this.getCapacitatorComponent().getCurrentEnergy() > 0) {
                            RocketEntity rocketEntity = (RocketEntity) world.getEntityById(((RocketLaunchPadBlockEntity) be).getRocketEntityId());
                            TankComponent tank = rocketEntity.getComponent(UniversalComponents.TANK_COMPONENT);
                            if (tank.getContents(0).getFluid().equals(this.tank.getContents(0).getFluid())
                                    && tank.getContents(0).getAmount().compareTo(tank.getMaxCapacity(0)) < 0
                                    && tank.getContents(0).getFluid().equals(GalacticraftFluids.FUEL)) {
                                tank.insertFluid(0, this.tank.takeFluid(0, tank.getMaxCapacity(0).subtract(tank.getContents(0).getAmount()), ActionType.PERFORM), ActionType.PERFORM);
                                this.getCapacitatorComponent().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyUsagePerTick(), ActionType.PERFORM);
                                status = FuelLoaderStatus.LOADING;
                            }

                        } else {
                            status = FuelLoaderStatus.NOT_ENOUGH_ENERGY;
                        }
                    } else {
                        status = FuelLoaderStatus.NOT_ENOUGH_FUEL;
                    }
                } else {
                    status = FuelLoaderStatus.NO_ROCKET;
                }
            } else {
                status = FuelLoaderStatus.NOT_ENOUGH_FUEL;
                // 4294967298
            }
        } else {
            status = FuelLoaderStatus.NO_ROCKET;
        }
    }

    public void updateConnections(Direction direction) {
        BlockPos launchPad = this.pos.offset(direction);
        if (world.getBlockState(launchPad).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD) {
            launchPad = RocketLaunchPadBlock.partToCenterPos(world.getBlockState(launchPad).get(RocketLaunchPadBlock.PART));
            if (launchPad != BlockPos.ORIGIN) {
                connectionPos = launchPad;
            }
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tank.toTag(tag);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        tank.fromTag(tag);
    }

    @Override
    protected int getBatteryTransferRate() {
        return 10;
    }

    @Override
    public Set<ComponentType<?>> getComponentTypes(BlockView blockView, BlockPos pos, @Nullable Direction side) {
        Set<ComponentType<?>> set = super.getComponentTypes(blockView, pos, side);

        BlockState state = blockView.getBlockState(pos);
        SideOption option = ((ConfigurableElectricMachineBlock) state.getBlock()).getOption(state, ConfigurableElectricMachineBlock.BlockFace.toFace(state.get(ConfigurableElectricMachineBlock.FACING), side));
        if (option == SideOption.FLUID_INPUT) {
            set.add(UniversalComponents.TANK_COMPONENT);
        }
        return set;
    }

    @Override
    public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, @Nullable Direction side) {
        if (type == UniversalComponents.TANK_COMPONENT) {
            BlockState state = blockView.getBlockState(pos);
            SideOption option = ((ConfigurableElectricMachineBlock) state.getBlock()).getOption(state, ConfigurableElectricMachineBlock.BlockFace.toFace(state.get(ConfigurableElectricMachineBlock.FACING), side));
            return option == SideOption.FLUID_INPUT;
        }
        return super.hasComponent(blockView, pos, type, side);
    }

    @Nullable
    @Override
    public <T extends Component> T getComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, @Nullable Direction side) {
        if (type == UniversalComponents.TANK_COMPONENT) {
            BlockState state = blockView.getBlockState(pos);
            SideOption option = ((ConfigurableElectricMachineBlock) state.getBlock()).getOption(state, ConfigurableElectricMachineBlock.BlockFace.toFace(state.get(ConfigurableElectricMachineBlock.FACING), side));
            //noinspection unchecked
            return option == SideOption.FLUID_INPUT ? (T) tank : null;
        }
        return super.getComponent(blockView, pos, type, side);
    }

    public SimpleTankComponent getTank() {
        return tank;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    public enum FuelLoaderStatus implements MachineStatus {
        /**
         * The fuel loader is loading fuel into the rocket.
         */
        LOADING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.loading"), Formatting.GREEN),

        /**
         * The fuel loader has enough fuel to load but not enough energy.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED),

        /**
         * The fuel loader doesn't have any fuel.
         */
        NOT_ENOUGH_FUEL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_fuel"), Formatting.GOLD),

        /**
         * The fuel loader doesn't have a rocket
         */
        NO_ROCKET(new TranslatableText("ui.galacticraft-rewoven.machinestatus.no_rocket"), Formatting.RED),

        /**
         * The sun is not visible.
         */
        BLOCKED(new TranslatableText("ui.galacticraft-rewoven.machinestatus.blocked"), Formatting.DARK_GRAY);

        private final Text text;

        FuelLoaderStatus(TranslatableText text, Formatting color) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        public static FuelLoaderStatus get(int index) {
            if (index < 0) index = 0;
            return FuelLoaderStatus.values()[index % FuelLoaderStatus.values().length];
        }

        @Override
        public Text getText() {
            return text;
        }
    }
}