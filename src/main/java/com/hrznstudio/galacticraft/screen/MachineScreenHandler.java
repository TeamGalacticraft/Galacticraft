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

package com.hrznstudio.galacticraft.screen;

import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.screen.property.CapacitorProperty;
import com.hrznstudio.galacticraft.screen.property.FluidTankPropertyDelegate;
import com.hrznstudio.galacticraft.screen.property.StatusProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class MachineScreenHandler<T extends ConfigurableMachineBlockEntity> extends ScreenHandler {
    public final PlayerEntity player;
    public final T machine;

    protected MachineScreenHandler(int syncId, PlayerEntity player, T machine, ScreenHandlerType<? extends MachineScreenHandler<T>> handlerType) {
        super(handlerType, syncId);
        this.player = player;
        this.machine = machine;

        this.addProperty(new CapacitorProperty(machine.getCapacitor()));
        this.addProperty(new StatusProperty(machine));

        PropertyDelegate tankDelegate = new FluidTankPropertyDelegate(machine.getFluidTank());
        for (int i = 0; i < machine.getFluidTankSize() * 2; i++) {
            this.addProperty(Property.create(tankDelegate, i));
        }
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

            if (slotId < this.machine.getInventory().getSize()) {
                if (!this.insertItem(stack1, this.machine.getInventory().getSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(stack1, 0, this.machine.getInventory().getSize(), false)) {
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
                this.addSlot(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, y + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(player.inventory, i, 8 + i * 18, y + 58));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return machine.getSecurity().getPublicity() == ConfigurableMachineBlockEntity.SecurityInfo.Publicity.PUBLIC
                || !machine.getSecurity().hasOwner()
                || machine.getSecurity().getOwner().equals(player.getUuid())
                || (machine.getSecurity().hasTeam() && machine.getSecurity().getPublicity() == ConfigurableMachineBlockEntity.SecurityInfo.Publicity.SPACE_RACE && false
//        && blockEntity.getSecurity().getTeam() == player
        );
    }
}
