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

package dev.galacticraft.mod.screen;

import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.mod.block.entity.RocketAssemblerBlockEntity;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.util.EnergyUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketAssemblerScreenHandler extends ScreenHandler {

    protected Inventory inventory;
    public final RocketAssemblerBlockEntity assembler;

    public RocketAssemblerScreenHandler(int syncId, PlayerEntity playerEntity, RocketAssemblerBlockEntity assembler) {
        super(GalacticraftScreenHandlerType.ROCKET_ASSEMBLER_HANDLER, syncId);
        this.assembler = assembler;
        this.inventory = new InventoryFixedWrapper(assembler.getInventory()) {
            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return player == playerEntity;
            }
        };

        final int playerInvYOffset = 94;
        final int playerInvXOffset = 148;

        this.addSlot(new Slot(this.inventory, RocketAssemblerBlockEntity.SCHEMATIC_INPUT_SLOT, 235, 19) {
            @Override
            public boolean canInsert(ItemStack stack) {
                RocketData data = RocketData.fromItem(stack);
                return this.getStack().isEmpty() || (stack.getItem() == GalacticraftItem.ROCKET_SCHEMATIC
                        && data.getCone().isUnlocked(playerEntity)
                        && data.getBody().isUnlocked(playerEntity)
                        && data.getBooster().isUnlocked(playerEntity)
                        && data.getBottom().isUnlocked(playerEntity)
                        && data.getFin().isUnlocked(playerEntity)
                        && data.getUpgrade().isUnlocked(playerEntity)
                );
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return true;
            }
        });

        // Output slot
        this.addSlot(new Slot(this.inventory, RocketAssemblerBlockEntity.ROCKET_OUTPUT_SLOT, 299, 19) {
            @Override
            public boolean canInsert(ItemStack itemStack_1) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity_1) {
                return true;
            }
        });

        this.addSlot(new Slot(this.inventory, RocketAssemblerBlockEntity.ENERGY_INPUT_SLOT, 156, 72) {
            @Override
            public boolean canInsert(ItemStack itemStack_1) {
                return EnergyUtil.isEnergyExtractable(itemStack_1) && this.getStack().isEmpty();
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity_1) {
                return true;
            }
        });

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerEntity.inventory, i, 8 + i * 18 + playerInvXOffset, playerInvYOffset + 58));
        }

        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerEntity.inventory, j + i * 9 + 9, 8 + (j * 18) + playerInvXOffset, playerInvYOffset + i * 18));
            }
        }
    }

    public RocketAssemblerScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (RocketAssemblerBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if (actionType == SlotActionType.QUICK_MOVE) {
            if (slots.get(i).getStack().getItem() != GalacticraftItem.ROCKET_SCHEMATIC) {
                return ItemStack.EMPTY;
            } else {
                if(inventory.getStack(0).isEmpty()) {
                    inventory.setStack(0, slots.get(i).getStack().copy());
                    slots.get(i).setStack(ItemStack.EMPTY);
                    return inventory.getStack(0);
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }
        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
