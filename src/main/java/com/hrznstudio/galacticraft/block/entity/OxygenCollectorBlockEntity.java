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
 *
 */

package com.hrznstudio.galacticraft.block.entity;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import io.github.cottonmc.component.api.ActionType;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCollectorBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final Fraction MAX_OXYGEN = Fraction.of(1, 100).multiply(Fraction.ofWhole(5000));
    public static final int BATTERY_SLOT = 0;

    public int collectionAmount = 0;
    public OxygenCollectorStatus status = OxygenCollectorStatus.INACTIVE;

    public OxygenCollectorBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_COLLECTOR_TYPE);
    }

    @Override
    public int getInventorySize() {
        return 1;
    }

    @Override
    public int getFluidTankSize() {
        return 1;
    }

    @Override
    public Fraction getFluidTankMaxCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return Lists.asList(SideOption.DEFAULT, SideOption.POWER_INPUT, new SideOption[]{SideOption.FLUID_OUTPUT});
    }

    @Override
    public boolean canExtractEnergy() {
        return false;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
    }

    private int collectOxygen(BlockPos center) {
        Optional<CelestialBodyType> celestialBodyType = CelestialBodyType.getByDimType(world.getRegistryKey());

        if (celestialBodyType.isPresent()) {
            if (!celestialBodyType.get().getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN)) {
                int minX = center.getX() - 5;
                int minY = center.getY() - 5;
                int minZ = center.getZ() - 5;
                int maxX = center.getX() + 5;
                int maxY = center.getY() + 5;
                int maxZ = center.getZ() + 5;

                float leafBlocks = 0;

                for (BlockPos pos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.isAir()) {
                        continue;
                    }
                    if (blockState.getBlock() instanceof LeavesBlock && !blockState.get(LeavesBlock.PERSISTENT)) {
                        leafBlocks++;
                    } else if (blockState.getBlock() instanceof CropBlock) {
                        leafBlocks += 0.75F;
                    }
                }

                if (leafBlocks < 2) return 0;

                double oxyCount = 20 * (leafBlocks / 14.0F);
                return (int) Math.ceil(oxyCount) / 20; //every tick
            } else {
                return 183 / 20;
            }
        } else {
            return 183 / 20;
        }
    }

    @Override
    public void tick() {
        if (world.isClient || disabled()) {
            if (disabled()) {
                idleEnergyDecrement(true);
            }
            return;
        }
        attemptChargeFromStack(BATTERY_SLOT);
        trySpreadFluids(0);

        if (this.getCapacitor().getCurrentEnergy() > 0) {
            this.status = OxygenCollectorStatus.COLLECTING;
        } else {
            this.status = OxygenCollectorStatus.INACTIVE;
        }

        if (this.status == OxygenCollectorStatus.INACTIVE) {
            idleEnergyDecrement(false);
        }

        if (status == OxygenCollectorStatus.COLLECTING) {
            collectionAmount = collectOxygen(this.pos);

            if (this.collectionAmount <= 0) {
                this.status = OxygenCollectorStatus.NOT_ENOUGH_LEAVES;
                return;
            }

            // If the oxygen capacity isn't full, add collected oxygen.
            if (this.getFluidTank().getMaxCapacity(0).compareTo(this.getFluidTank().getContents(0).getAmount()) > 0) {
                this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyUsagePerTick(), ActionType.PERFORM);

                this.getFluidTank().insertFluid(0, new FluidVolume(this.getFluidTank().getContents(0).isEmpty() ? GalacticraftFluids.OXYGEN : this.getFluidTank().getContents(0).getFluid(), Fraction.of(collectionAmount, 100)), ActionType.PERFORM);
            } else {
                status = OxygenCollectorStatus.FULL;
            }
        } else {
            collectionAmount = 0;
        }
    }

    @Override
    public int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().oxygenCollectorEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtractItems(int slot) {
        return true;
    }

    @Override
    public boolean canHopperInsertItems(int slot) {
        return true;
    }

    @Override
    public boolean canExtractFluid(int tank) {
        return true;
    }

    @Override
    public boolean canInsertFluid(int tank) {
        return false;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return volume.getFluid().isIn(GalacticraftTags.OXYGEN);
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    public enum OxygenCollectorStatus implements MachineStatus {
        INACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.inactive"), Formatting.GRAY),
        NOT_ENOUGH_LEAVES(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_leaves"), Formatting.RED),
        COLLECTING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.collecting"), Formatting.GREEN),
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full"), Formatting.GOLD);

        private final Text text;

        OxygenCollectorStatus(TranslatableText text, Formatting color) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        public static OxygenCollectorStatus get(int index) {
            if (index < 0) return OxygenCollectorStatus.values()[0];
            return OxygenCollectorStatus.values()[index % OxygenCollectorStatus.values().length];
        }

        @Override
        public Text getText() {
            return text;
        }
    }
}