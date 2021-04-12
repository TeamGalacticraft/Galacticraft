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

import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.screen.property.CapacitorProperty;
import com.hrznstudio.galacticraft.screen.property.FluidTankPropertyDelegate;
import com.hrznstudio.galacticraft.screen.property.StatusProperty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class MachineScreenHandler<T extends ConfigurableMachineBlockEntity> extends AbstractContainerMenu {
    public final Player player;
    public final T machine;

    protected MachineScreenHandler(int syncId, Player player, T machine, MenuType<? extends MachineScreenHandler<T>> handlerType) {
        super(handlerType, syncId);
        this.player = player;
        this.machine = machine;

        this.addDataSlot(new StatusProperty(machine));
        this.addDataSlot(new CapacitorProperty(machine.getCapacitor()));

        ContainerData tankDelegate = new FluidTankPropertyDelegate(machine.getFluidTank());
        for (int i = 0; i < machine.getFluidTankSize() * 2; i++) {
            this.addDataSlot(DataSlot.forContainer(tankDelegate, i));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotId) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if (slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();

            if (stack.isEmpty()) {
                return stack;
            }

            if (slotId < this.machine.getInventory().getSlotCount()) {
                if (!this.moveItemStackTo(stack1, this.machine.getInventory().getSlotCount(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack1, 0, this.machine.getInventory().getSlotCount(), false)) {
                return ItemStack.EMPTY;
            }
            if (stack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    protected void addPlayerInventorySlots(int x, int y) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(player.inventory, j + i * 9 + 9, x + 8 + j * 18, y + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(player.inventory, i, x + 8 + i * 18, y + 58));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return machine.getSecurity().getPublicity() == ConfigurableMachineBlockEntity.SecurityInfo.Publicity.PUBLIC
                || !machine.getSecurity().hasOwner()
                || machine.getSecurity().getOwner().equals(player.getUUID())
                || (machine.getSecurity().hasTeam() && machine.getSecurity().getPublicity() == ConfigurableMachineBlockEntity.SecurityInfo.Publicity.SPACE_RACE && false
//        && blockEntity.getSecurity().getTeam() == player
        );
    }

    @Override
    public DataSlot addDataSlot(DataSlot property) {
        return super.addDataSlot(property);
    }
}
