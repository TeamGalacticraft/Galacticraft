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
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
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
    public final PlayerEntity playerEntity;
    public final T blockEntity;

    protected MachineScreenHandler(int syncId, PlayerEntity playerEntity, T blockEntity, ScreenHandlerType<? extends MachineScreenHandler<T>> handlerType) {
        super(handlerType, syncId);
        this.playerEntity = playerEntity;
        this.blockEntity = blockEntity;

        addProperty(new CapacitorProperty(blockEntity.getCapacitor()));
        addProperty(new StatusProperty(blockEntity));

        PropertyDelegate tankDelegate = new FluidTankPropertyDelegate(blockEntity.getFluidTank());
        for (int i = 0; i < blockEntity.getFluidTankSize() * 2; i++) {
            addProperty(Property.create(tankDelegate, i));
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int slotId) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if (slot != null && slot.hasStack()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();

            if (stack.isEmpty()) {
                return stack;
            }

            if (slotId < this.blockEntity.getInventory().getSize()) {
                if (!this.insertItem(stack1, this.blockEntity.getInventory().getSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(stack1, 0, this.blockEntity.getInventory().getSize(), false)) {
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

    @Override
    public boolean canUse(PlayerEntity player) {
        return blockEntity.getSecurity().getPublicity() == ConfigurableMachineBlockEntity.SecurityInfo.Publicity.PUBLIC
                || !blockEntity.getSecurity().hasOwner()
                || blockEntity.getSecurity().getOwner().equals(player.getUuid())
                || (blockEntity.getSecurity().hasTeam() && blockEntity.getSecurity().getPublicity() == ConfigurableMachineBlockEntity.SecurityInfo.Publicity.SPACE_RACE && false
//        && blockEntity.getSecurity().getTeam() == player
        );
    }

    @FunctionalInterface
    public interface MachineContainerConstructor<C, T> {
        C create(int syncId, PlayerEntity player, T blockEntity);
    }
}
