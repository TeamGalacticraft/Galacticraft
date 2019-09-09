package com.hrznstudio.galacticraft.blocks.machines.refinery;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidProviderItem;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.misc.Ref;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluids.FuelFluid;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.tag.GalacticraftFluidTags;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RefineryBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {

    private static final Item[] mandatoryMaterials = new Item[]{Items.DIAMOND, GalacticraftItems.RAW_SILICON, GalacticraftItems.RAW_SILICON, Items.REDSTONE};
    private static final ItemFilter[] SLOT_FILTERS;

    static {
        SLOT_FILTERS = new ItemFilter[3];
        SLOT_FILTERS[0] = GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        SLOT_FILTERS[1] = new ExactItemFilter(Items.BUCKET);
        SLOT_FILTERS[2] = stack -> {
            if (stack.getItem() instanceof FluidProviderItem) {
                FluidVolume output = ((FluidProviderItem) stack.getItem()).drain(new Ref<>(stack));
                if (output.getFluidKey() == FluidKeys.EMPTY) {
                    return true;
                } else {
                    ((FluidProviderItem) stack.getItem()).fill(new Ref<>(stack), new Ref<>(output));
                    return false;
                }
            } else {
                return false;
            }
        };
    }

    private final SimpleFixedFluidInv fluidInv = new SimpleFixedFluidInv(2, FluidVolume.BUCKET * 10) {
        @Override
        public FluidFilter getFilterForTank(int tank) {
            if (tank == 0) {
                return fluidKey -> fluidKey.withAmount(FluidVolume.BUCKET).getRawFluid().matches(GalacticraftFluidTags.OIL);
            } else if (tank == 1) {
                return fluidKey -> fluidKey.withAmount(FluidVolume.BUCKET).getRawFluid().matches(GalacticraftFluidTags.FUEL);
            } else {
                System.out.println("2!?");
                return fluidKey -> false;
            }
        }

        @Override
        public boolean isFluidValidForTank(int tank, FluidKey fluid) {
            if (tank == 0) {
                return fluid.withAmount(FluidVolume.BUCKET).getRawFluid().matches(GalacticraftFluidTags.OIL);
            } else if (tank == 1) {
                return fluid.withAmount(FluidVolume.BUCKET).getRawFluid().matches(GalacticraftFluidTags.FUEL);
            } else {
                System.out.println("2!?");
                return false;
            }
        }
    };
    public RefineryStatus status = RefineryStatus.INACTIVE;

    public RefineryBlockEntity() {
        super(GalacticraftBlockEntities.REFINERY_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 3;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        return SLOT_FILTERS[slot];
    }

    @Override
    public void tick() {
        if (world.isClient || !enabled()) {
            return;
        }

        attemptChargeFromStack(0);

        if (getInventory().getInvStack(1).getItem() instanceof FluidProviderItem) {
            FluidVolume output = ((FluidProviderItem) getInventory().getInvStack(1).getItem()).drain(new Ref<>(getInventory().getInvStack(1)));
            if (output.getRawFluid().matches(GalacticraftFluidTags.OIL)) {
                this.fluidInv.getTank(1).insert(output);
            } else {
                ((FluidProviderItem) getInventory().getInvStack(1).getItem()).fill(new Ref<>(getInventory().getInvStack(1)), new Ref<>(output));
            }
        }

        if (getEnergyAttribute().getCurrentEnergy() <= 0) {
            status = RefineryStatus.INACTIVE;
            return;
        }

        if (!fluidInv.getInvFluid(1).isEmpty() && !fluidInv.getInvFluid(2).isEmpty()) {
            this.status = RefineryStatus.ACTIVE;
        } else {
            this.status = RefineryStatus.IDLE;
        }

        if (status == RefineryStatus.ACTIVE) {
            this.getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, Simulation.ACTION);
            FluidVolume extracted = this.fluidInv.getTank(1).extract(10);
            this.fluidInv.insert(FluidVolume.create(new FuelFluid(), extracted.getAmount()));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("FluidInventory", fluidInv.toTag());
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        fluidInv.fromTag(tag.getCompound("FluidInventory"));
    }
}