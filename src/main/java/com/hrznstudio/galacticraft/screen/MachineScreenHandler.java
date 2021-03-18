/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.screen;

import alexiil.mc.lib.attributes.fluid.SingleFluidTank;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.energy.api.Capacitor;
import com.hrznstudio.galacticraft.screen.property.CapacitorProperty;
import com.hrznstudio.galacticraft.screen.property.FluidTankPropertyDelegate;
import com.hrznstudio.galacticraft.screen.property.StatusProperty;
import com.hrznstudio.galacticraft.screen.slot.AutoFilteredSlot;
import com.hrznstudio.galacticraft.screen.slot.MachineComponent;
import com.hrznstudio.galacticraft.screen.slot.MachineSlotComponent;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import java.util.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class MachineScreenHandler<T extends MachineBlockEntity> extends ScreenHandler {
    public final PlayerEntity player;
    public final T machine;

    private final Map<SlotType, List<MachineComponent<Slot>>> machineSlots = new HashMap<>();
    private final Map<SlotType, List<MachineComponent<SingleFluidTank>>> machineTanks = new HashMap<>();
    private final Map<SlotType, List<MachineComponent<Capacitor>>> machineCapacitors = new HashMap<>();
    private final PropertyDelegate tankProperty;

    protected MachineScreenHandler(int syncId, PlayerEntity player, T machine, ScreenHandlerType<? extends MachineScreenHandler<T>> handlerType) {
        super(handlerType, syncId);
        this.player = player;
        this.machine = machine;
        this.tankProperty = new FluidTankPropertyDelegate(machine.getFluidTank());

        this.addProperty(new StatusProperty(machine));
        this.addProperty(new CapacitorProperty(machine.getCapacitor()));
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int slotId) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if (slot != null && slot.hasStack()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();

            if (stack.isEmpty()) {
                return stack;
            }

            if (slotId < this.machine.getInventory().getSlotCount()) {
                if (!this.insertItem(stack1, this.machine.getInventory().getSlotCount(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(stack1, 0, this.machine.getInventory().getSlotCount(), false)) {
                return ItemStack.EMPTY;
            }
            if (stack1.getCount() == 0) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return stack;
    }

    protected void addPlayerInventorySlots(int x, int y) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                super.addSlot(new Slot(player.inventory, j + i * 9 + 9, x + 8 + j * 18, y + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            super.addSlot(new Slot(player.inventory, i, x + 8 + i * 18, y + 58));
        }
    }

    @Override
    @Deprecated
    protected Slot addSlot(Slot slot) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment() && slot.inventory == this.machine.getWrappedInventory()) {
            Galacticraft.LOGGER.warn("You shouldn't add normal slots to this ScreenHandler!");
            assert false;
        }
        return super.addSlot(slot);
    }

    protected void addSlot(MachineComponent<Slot> slot, SlotType type) {
        assert type.getType().isItem();
        super.addSlot(slot.getComponent());
        this.machineSlots.putIfAbsent(type, new LinkedList<>());
        this.machineSlots.get(type).add(slot);
    }

    protected void addSlot(int index, int x, int y, SlotType type) {
        this.addSlot(new MachineSlotComponent(new AutoFilteredSlot(this.machine, index, x, y)), type);
    }

    protected void addTank(SlotType type, MachineComponent<SingleFluidTank> tank) {
        assert type.getType().isFluid();
        this.machineTanks.putIfAbsent(type, new LinkedList<>());
        this.machineTanks.get(type).add(tank);
        this.addProperty(Property.create(this.tankProperty, (tank.getComponent().getIndex() * 2)));
        this.addProperty(Property.create(this.tankProperty, (tank.getComponent().getIndex() * 2) + 1));
    }

    protected void addTank(int index, int x, int y, SlotType type) {
        this.addTank(type, new MachineComponent<>(this.machine.getFluidTank().getTank(index), x, y));
    }

    protected void addCapacitor(SlotType type, int x, int y) {
        this.addCapacitor(type, new MachineComponent<>(this.machine.getCapacitor(), x, y));
    }

    protected void addCapacitor(SlotType type, MachineComponent<Capacitor> capacitor) {
        assert type.getType().isEnergy();
        this.machineCapacitors.putIfAbsent(type, new LinkedList<>());
        this.machineCapacitors.get(type).add(capacitor);
        this.addProperty(new CapacitorProperty(capacitor.getComponent()));
    }

    public Set<SlotType> getItemSlotTypes() {
        return this.machineSlots.keySet();
    }

    public Set<SlotType> getTankTypes() {
        return this.machineTanks.keySet();
    }

    public Set<SlotType> getCapacitorTypes() {
        return this.machineCapacitors.keySet();
    }

    public Map<SlotType, List<MachineComponent<Slot>>> getMachineSlots() {
        return machineSlots;
    }

    public Map<SlotType, List<MachineComponent<SingleFluidTank>>> getMachineTanks() {
        return machineTanks;
    }

    public Map<SlotType, List<MachineComponent<Capacitor>>> getMachineCapacitors() {
        return machineCapacitors;
    }

    public Set<SlotType> getSlotTypes() {
        return this.machineSlots.keySet();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return machine.getSecurity().hasAccess(player);
    }
}
