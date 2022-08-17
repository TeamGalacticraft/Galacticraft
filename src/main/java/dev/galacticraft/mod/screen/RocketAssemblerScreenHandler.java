/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.mod.block.entity.RocketAssemblerBlockEntity;
import dev.galacticraft.mod.item.GalacticraftItem;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketAssemblerScreenHandler extends AbstractContainerMenu {

    protected Container inventory;
    public final Player player;
    public final RocketAssemblerBlockEntity assembler;

    public RocketAssemblerScreenHandler(int syncId, Player player, RocketAssemblerBlockEntity assembler) {
        super(GalacticraftScreenHandlerType.ROCKET_ASSEMBLER_HANDLER, syncId);
        this.player = player;
        this.assembler = assembler;
        this.inventory = new InventoryFixedWrapper(assembler.getInventory()) {
            @Override
            public boolean canPlayerUse(Player player) {
                return player == RocketAssemblerScreenHandler.this.player;
            }
        };

        final int playerInvYOffset = 94;
        final int playerInvXOffset = 148;

        Registry<RocketPart> registry = RocketPart.getRegistry(player.getCommandSenderWorld().registryAccess());

        this.addSlot(new Slot(this.inventory, RocketAssemblerBlockEntity.SCHEMATIC_INPUT_SLOT, 235, 19) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                RocketData data = RocketData.fromNbt(stack.getTag());
                return this.getItem().isEmpty() || (stack.getItem() == GalacticraftItem.ROCKET_SCHEMATIC
                        && RocketPart.getById(registry, data.cone()).isUnlocked(player)
                        && RocketPart.getById(registry, data.body()).isUnlocked(player)
                        && RocketPart.getById(registry, data.booster()).isUnlocked(player)
                        && RocketPart.getById(registry, data.bottom()).isUnlocked(player)
                        && RocketPart.getById(registry, data.fin()).isUnlocked(player)
                        && RocketPart.getById(registry, data.upgrade()).isUnlocked(player)
                );
            }

            @Override
            public boolean canTakeItems(Player playerEntity) {
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
            public boolean canTakeItems(Player playerEntity_1) {
                return true;
            }
        });

        this.addSlot(new Slot(this.inventory, RocketAssemblerBlockEntity.ENERGY_INPUT_SLOT, 156, 72) {
            @Override
            public boolean canInsert(ItemStack itemStack_1) {
                return EnergyUtil.isEnergyExtractable(itemStack_1) && this.getItem().isEmpty();
            }

            @Override
            public boolean canTakeItems(Player playerEntity_1) {
                return true;
            }
        });

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(player.getInventory(), i, 8 + i * 18 + playerInvXOffset, playerInvYOffset + 58));
        }

        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(player.getInventory(), j + i * 9 + 9, 8 + (j * 18) + playerInvXOffset, playerInvYOffset + i * 18));
            }
        }
    }

    public RocketAssemblerScreenHandler(int syncId, Inventory inv, FriendlyByteBuf buf) {
        this(syncId, inv.player, (RocketAssemblerBlockEntity) inv.player.getLevel().getBlockEntity(buf.readBlockPos()));
    }

//    @Override
//    public void onSlotClick(int i, int j, SlotActionType actionType, Player playerEntity) {
//        if (actionType == SlotActionType.QUICK_MOVE) {
//            if (slots.get(i).getStack().getItem() != GalacticraftItem.ROCKET_SCHEMATIC) {
//                return ItemStack.EMPTY;
//            } else {
//                if(inventory.getStack(0).isEmpty()) {
//                    inventory.setStack(0, slots.get(i).getStack().copy());
//                    slots.get(i).setStack(ItemStack.EMPTY);
//                    return inventory.getStack(0);
//                } else {
//                    return ItemStack.EMPTY;
//                }
//            }
//        }
//        super.onSlotClick(i, j, actionType, playerEntity);
//    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
