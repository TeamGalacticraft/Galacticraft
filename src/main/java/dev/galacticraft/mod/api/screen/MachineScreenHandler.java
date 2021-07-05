/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.api.screen;

import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.screen.property.CapacitorProperty;
import dev.galacticraft.mod.screen.property.FluidTankPropertyDelegate;
import dev.galacticraft.mod.screen.property.StatusProperty;
import dev.galacticraft.mod.screen.tank.Tank;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public abstract class MachineScreenHandler<M extends MachineBlockEntity> extends ScreenHandler {
    public final PlayerEntity player;
    public final M machine;

    public final List<Tank> tanks = new ArrayList<>();

    protected MachineScreenHandler(int syncId, PlayerEntity player, M machine, ScreenHandlerType<? extends MachineScreenHandler<M>> handlerType) {
        super(handlerType, syncId);
        this.player = player;
        this.machine = machine;
        this.machine.itemInv().createSlots(this);
        this.machine.fluidInv().createTanks(this);
        this.addProperties(new FluidTankPropertyDelegate(machine.fluidInv()));

        this.addProperty(new StatusProperty(machine));
        this.addProperty(new CapacitorProperty(machine.capacitor()));
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

            if (slotId < this.machine.itemInv().getSlotCount()) {
                if (!this.insertItem(stack1, this.machine.itemInv().getSlotCount(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(stack1, 0, this.machine.itemInv().getSlotCount(), false)) {
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
                super.addSlot(new Slot(this.player.getInventory(), j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            super.addSlot(new Slot(this.player.getInventory(), i, x + i * 18, y + 58));
        }
    }

    @Override
    public Slot addSlot(Slot slot) {
        return super.addSlot(slot);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return machine.security().hasAccess(player);
    }

    public Tank addTank(Tank tank) {
        tank.id = this.tanks.size();
        this.tanks.add(tank);
        return tank;
    }
}
