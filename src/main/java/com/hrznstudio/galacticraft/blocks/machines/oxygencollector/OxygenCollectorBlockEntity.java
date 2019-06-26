package com.hrznstudio.galacticraft.blocks.machines.oxygencollector;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.space.CelestialBody;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

public class OxygenCollectorBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {
    public static final int MAX_OXYGEN = 5000;
    public static final int BATTERY_SLOT = 0;

    public CollectorStatus status = CollectorStatus.INACTIVE;
    public int lastCollectAmount = 0;
    private SimpleEnergyAttribute oxygen = new SimpleEnergyAttribute(MAX_OXYGEN, GalacticraftEnergy.GALACTICRAFT_OXYGEN);

    public OxygenCollectorBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_COLLECTOR_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 1;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
    }

    private int collectOxygen(BlockPos center) {
        if (world.dimension instanceof CelestialBody) {
            if (!((CelestialBody) world.dimension).hasOxygen()) {
                int minX = center.getX() - 5;
                int minY = center.getY() - 5;
                int minZ = center.getZ() - 5;
                int maxX = center.getX() + 5;
                int maxY = center.getY() + 5;
                int maxZ = center.getZ() + 5;

                int leafBlocks = 0;

                for (BlockPos pos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.isAir()) {
                        continue;
                    }
                    if (blockState.getBlock() instanceof LeavesBlock && !blockState.get(LeavesBlock.PERSISTENT)) {
                        leafBlocks++;
                    } else if (blockState.getBlock() instanceof CropBlock) {
                        leafBlocks++;
                    }
                }

                if (leafBlocks < 2) return 0;

                double oxyCount = 20 * (leafBlocks / 14);
                return (int) Math.ceil(oxyCount);
            } else {
                return 183;
            }
        } else {
            return 183;
        }
    }

    @Override
    public void tick() {
        if (world.isClient || !isActive()) {
            return;
        }
        attemptChargeFromStack(BATTERY_SLOT);

        // Only collect every 20 ticks
        if (world.random.nextInt(10) != 0) {
            return;
        }

        if (this.getEnergy().getCurrentEnergy() > 0) {
            this.status = CollectorStatus.COLLECTING;
        } else {
            this.status = CollectorStatus.INACTIVE;
        }

        if (status == CollectorStatus.COLLECTING) {
            lastCollectAmount = collectOxygen(this.pos);

            if (this.lastCollectAmount <= 0) {
                this.status = CollectorStatus.NOT_ENOUGH_LEAVES;
                return;
            }

            // If the oxygen capacity isn't full, add collected oxygen.
            if (this.getOxygen().getMaxEnergy() != this.oxygen.getCurrentEnergy()) {
                this.getEnergy().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 5, Simulation.ACTION);

                this.oxygen.insertEnergy(GalacticraftEnergy.GALACTICRAFT_OXYGEN, lastCollectAmount, Simulation.ACTION);
            }
        } else {
            lastCollectAmount = 0;
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Oxygen", oxygen.getCurrentEnergy());

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        this.oxygen.setCurrentEnergy(tag.getInt("Oxygen"));
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    public EnergyAttribute getOxygen() {
        return this.oxygen;
    }
}