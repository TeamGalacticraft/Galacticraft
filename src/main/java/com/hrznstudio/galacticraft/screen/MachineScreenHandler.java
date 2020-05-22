/*
 * Copyright (c) 2019 HRZN LTD
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

import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class MachineScreenHandler<T extends ConfigurableElectricMachineBlockEntity> extends ScreenHandler {

    public final PlayerEntity playerEntity;
    public final T blockEntity;
    public final Property energy = Property.create();

    protected MachineScreenHandler(int syncId, PlayerEntity playerEntity, T blockEntity) {
        super(null, syncId);
        this.playerEntity = playerEntity;
        this.blockEntity = blockEntity;
        addProperty(energy);
    }

    public static <T extends ConfigurableElectricMachineBlockEntity> ContainerFactory<ScreenHandler> createFactory(
            Class<T> machineClass, MachineContainerConstructor<? extends ScreenHandler, T> constructor) {
        return (syncId, id, player, buffer) -> {
            BlockPos pos = buffer.readBlockPos();
            BlockEntity be = player.world.getBlockEntity(pos);
            if (machineClass.isInstance(be)) {
                return constructor.create(syncId, player, machineClass.cast(be));
            } else {
                return null;
            }
        };
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int slotId) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if (slot != null && slot.hasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (itemStack.isEmpty()) {
                return itemStack;
            }

            if (slotId < this.blockEntity.getInventory().getSlotCount()) {
                if (!this.insertItem(itemStack1, this.blockEntity.getInventory().getSlotCount(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack1, 0, this.blockEntity.getInventory().getSlotCount(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack1.getCount() == 0) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void sendContentUpdates() {
        energy.set(blockEntity.getEnergyAttribute().getCurrentEnergy());
        super.sendContentUpdates();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return blockEntity.getSecurity().getPublicity() == ConfigurableElectricMachineBlockEntity.SecurityInfo.Publicity.PUBLIC
                || !blockEntity.getSecurity().hasOwner()
                || blockEntity.getSecurity().getOwner().equals(player.getUuid())
                || (blockEntity.getSecurity().hasTeam() && blockEntity.getSecurity().getPublicity() == ConfigurableElectricMachineBlockEntity.SecurityInfo.Publicity.SPACE_RACE && false
//        && blockEntity.getSecurity().getTeam() == player
        );
    }

    public int getMaxEnergy() {
        return blockEntity.getMaxEnergy();
    }

    @FunctionalInterface
    public interface MachineContainerConstructor<C, T> {
        C create(int syncId, PlayerEntity player, T blockEntity);
    }
}
