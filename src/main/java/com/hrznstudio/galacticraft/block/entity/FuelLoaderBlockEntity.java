/*
 * Copyright (c) 2020 HRZN LTD
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

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import com.hrznstudio.galacticraft.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.api.ComponentHelper;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FuelLoaderBlockEntity extends ConfigurableMachineBlockEntity {
    private static final int CHARGE_SLOT = 0;
    private static final int FUEL_INPUT_SLOT = 1;
    private BlockPos connectionPos = null;
    private Direction check = null;

    public FuelLoaderBlockEntity() {
        super(GalacticraftBlockEntities.FUEL_LOADER_TYPE);
    }

    @Nullable
    public BlockPos getConnectionPos() {
        return connectionPos;
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public int getFluidTankSize() {
        return 1;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.FLUID_INPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public boolean canPipeInsertFluid(int tank) {
        return true;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return tank == 0 && volume.getFluid().isIn(GalacticraftTags.FUEL);
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == 0) {
            return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        } else if (slot == 1) {
            return stack -> ComponentHelper.TANK.hasComponent(stack) || stack.getItem() instanceof BucketItem;
        }
        return Constants.Misc.alwaysFalse();
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.getFluidTank().takeFluid(0, Fraction.ONE, ActionType.TEST).isEmpty()) return Status.NOT_ENOUGH_FUEL;
        if (this.connectionPos == null) return Status.NO_ROCKET;
        BlockEntity be = this.world.getBlockEntity(connectionPos);
        if (be instanceof RocketLaunchPadBlockEntity) {
            if (!((RocketLaunchPadBlockEntity) be).hasRocket()) return Status.NO_ROCKET;
            Entity e = world.getEntityById(((RocketLaunchPadBlockEntity) be).getRocketEntityId());
            if (e == null) return Status.NO_ROCKET;
            TankComponent component = UniversalComponents.TANK_COMPONENT.getNullable(e);
            if (component == null) return Status.NO_ROCKET;
            if (component.getContents(0).getAmount().compareTo(component.getMaxCapacity(0)) >= 0) return Status.ROCKET_IS_FULL;
        } else {
            return Status.NO_ROCKET;
        }

        return Status.LOADING;
    }

    @Override
    public void tickWork() {
        if (check != null) {
            BlockPos launchPad = this.pos.offset(check);
            if (world.getBlockState(launchPad).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD) {
                launchPad = launchPad.add(RocketLaunchPadBlock.partToCenterPos(world.getBlockState(launchPad).get(RocketLaunchPadBlock.PART)));
                if (world.getBlockState(launchPad).getBlock() instanceof RocketLaunchPadBlock
                        && world.getBlockState(launchPad).get(RocketLaunchPadBlock.PART) == RocketLaunchPadBlock.Part.CENTER
                        && world.getBlockEntity(launchPad) instanceof RocketLaunchPadBlockEntity) {
                    connectionPos = launchPad;
                }
            }
            check = null;
        }

        if (!this.isTankFull(0)) {
            ItemStack bucket = this.getInventory().getStack(1);
            if (bucket.getItem() instanceof BucketItem) {
                if (((BucketItem) bucket.getItem()).fluid.isIn(GalacticraftTags.FUEL)) {
                    this.getInventory().setStack(1, new ItemStack(Items.BUCKET));
                    this.getFluidTank().insertFluid(0, new FluidVolume(((BucketItem) bucket.getItem()).fluid, Fraction.ONE), ActionType.PERFORM);
                }
            }
        }
        if (!this.isTankFull(0)) {
            TankComponent component = ComponentHelper.TANK.getComponent(this.getInventory().getStack(FUEL_INPUT_SLOT));
            if (component != null) {
                if (component.getContents(0).getFluid().isIn(GalacticraftTags.FUEL)) {
                    this.getFluidTank().insertFluid(0, component.takeFluid(0, Fraction.of(1, 20), ActionType.PERFORM), ActionType.PERFORM);
                }
            }
        }

        if (this.getStatus().getType().isActive()) {
            TankComponent component = UniversalComponents.TANK_COMPONENT.get(this.world.getEntityById(((RocketLaunchPadBlockEntity) world.getBlockEntity(connectionPos)).getRocketEntityId()));
            this.getFluidTank().insertFluid(0, component.insertFluid(0, this.getFluidTank().takeFluid(0, Fraction.of(1, 50), ActionType.PERFORM), ActionType.PERFORM), ActionType.PERFORM);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (connectionPos != null) {
            tag.putBoolean("has_connection" , true);
            tag.putLong("connection_pos", connectionPos.asLong());
        }
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (tag.getBoolean("has_connection")) {
            connectionPos = BlockPos.fromLong(tag.getLong("connection_pos"));
        }
    }

    public void updateConnections(Direction direction) {
        this.check = direction;
    }

    @Environment(EnvType.CLIENT)
    public void setConnectionPos(BlockPos connectionPos) {
        this.connectionPos = connectionPos;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        /**
         * The fuel loader is loading fuel into the rocket.
         */
        LOADING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.loading"), Formatting.GREEN, StatusType.WORKING),

        /**
         * The fuel loader has enough fuel to load but not enough energy.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),

        /**
         * The fuel loader doesn't have any fuel.
         */
        NOT_ENOUGH_FUEL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_fuel"), Formatting.GOLD, StatusType.MISSING_FLUIDS),

        /**
         * The fuel loader doesn't have a rocket
         */
        NO_ROCKET(new TranslatableText("ui.galacticraft-rewoven.machinestatus.no_rocket"), Formatting.RED, StatusType.MISSING_RESOURCE),

        /**
         * The sun is not visible.
         */
        ROCKET_IS_FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.rocket_is_full"), Formatting.GOLD, StatusType.OUTPUT_FULL);

        private final Text name;
        private final StatusType type;

        Status(TranslatableText name, Formatting color, StatusType type) {
            this.type = type;
            this.name = name.setStyle(Style.EMPTY.withColor(color));
        }

        public static Status get(int index) {
            if (index < 0) index = 0;
            return Status.values()[index % Status.values().length];
        }

        @Override
        public @NotNull Text getName() {
            return name;
        }

        @Override
        public @NotNull StatusType getType() {
            return type;
        }

        @Override
        public int getIndex() {
            return ordinal();
        }
    }
}