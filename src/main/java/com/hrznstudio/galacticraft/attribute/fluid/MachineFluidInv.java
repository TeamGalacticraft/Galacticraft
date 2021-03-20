package com.hrznstudio.galacticraft.attribute.fluid;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import com.hrznstudio.galacticraft.attribute.Automatable;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import com.hrznstudio.galacticraft.screen.tank.Tank;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class MachineFluidInv extends SimpleFixedFluidInv implements Automatable {
    private final List<SlotType> slotTypes = new ArrayList<>();
    private final List<FluidFilter> filters = new ArrayList<>();
    private final List<Vec3i> positions = new ArrayList<>();

    public MachineFluidInv(FluidAmount tankCapacity) {
        super(0, tankCapacity);
    }

    @Override
    public FluidFilter getFilterForTank(int slot) {
        if (slot < 0 || slot >= this.getTankCount()) return ConstantFluidFilter.NOTHING;
        return this.filters.get(slot);
    }

    @Override
    public boolean isFluidValidForTank(int tank, FluidKey fluid) {
        return this.getFilterForTank(tank).matches(fluid);
    }

    public void addSlot(SlotType type, FluidFilter filter, int x, int y, int h) {
        this.positions.add(this.getTankCount(), new Vec3i(x, y, h));
        this.slotTypes.add(this.getTankCount(), type);
        this.filters.add(this.getTankCount(), filter);
    }

    public void addSlot(SlotType type, FluidFilter filter) {
        this.positions.add(this.getTankCount(), null);
        this.slotTypes.add(this.getTankCount(), type);
        this.filters.add(this.getTankCount(), filter);
    }

    public void createTanks(MachineScreenHandler<?> screenHandler) {
        Vec3i vec;
        for (int i = 0; i < getTankCount(); i++) {
            vec = positions.get(i);
            if (vec != null) {
                screenHandler.addTank(new Tank(i, screenHandler.machine.getFluidTank(), vec.getX(), vec.getY(), vec.getZ()));
            }
        }
    }

    @Override
    public List<SlotType> getTypes() {
        return this.slotTypes;
    }
}