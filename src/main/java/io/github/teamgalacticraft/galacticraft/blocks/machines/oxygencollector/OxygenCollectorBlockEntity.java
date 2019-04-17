package io.github.teamgalacticraft.galacticraft.blocks.machines.oxygencollector;

import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.blocks.machines.MachineBlockEntity;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

public class OxygenCollectorBlockEntity extends MachineBlockEntity implements Tickable, BlockEntityClientSerializable {
    public static int BATTERY_SLOT = 0;
    public CollectorStatus status = CollectorStatus.INACTIVE;
    public int lastCollectAmount = 0;
    private SimpleEnergyAttribute oxygen = new SimpleEnergyAttribute(5000, GalacticraftEnergy.GALACTICRAFT_OXYGEN);

    public OxygenCollectorBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_COLLECTOR_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 1;
    }

    private int collectOxygen(BlockPos center) {
        int minX = center.getX() - 5;
        int minY = center.getY() - 5;
        int minZ = center.getZ() - 5;
        int maxX = center.getX() + 5;
        int maxY = center.getY() + 5;
        int maxZ = center.getZ() + 5;

        double leafBlocks = 0;

        for (BlockPos pos : BlockPos.iterateBoxPositions(minX, minY, minZ, maxX, maxY, maxZ)) {
            BlockState blockState = world.getBlockState(pos);
            if (blockState.isAir()) {
                continue;
            }
            if (blockState.getBlock() instanceof LeavesBlock || blockState.getBlock() instanceof CropBlock) {
                leafBlocks++;
            }
        }

        if (leafBlocks < 2) return 0;

        double oxyCount = 20 * (leafBlocks / 14);
        return (int) Math.ceil(oxyCount);
    }

    @Override
    public void tick() {
        attemptChargeFromStack(getInventory().getInvStack(BATTERY_SLOT));

        // Only collect every 20 seconds
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
                int i = this.getEnergy().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 5, ActionType.PERFORM);

                this.oxygen.insertEnergy(GalacticraftEnergy.GALACTICRAFT_OXYGEN, lastCollectAmount, ActionType.PERFORM);
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