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

import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.item.ThermalArmorItem;
import dev.galacticraft.mod.screen.slot.AccessorySlot;
import dev.galacticraft.mod.util.OxygenTankUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftPlayerInventoryScreenHandler extends ScreenHandler {
    private static final Identifier[] EMPTY_ARMOR_SLOT_IDS = new Identifier[]{
            new Identifier(Constant.MOD_ID, Constant.SlotSprite.THERMAL_BOOTS),
            new Identifier(Constant.MOD_ID, Constant.SlotSprite.THERMAL_PANTS),
            new Identifier(Constant.MOD_ID, Constant.SlotSprite.THERMAL_CHEST),
            new Identifier(Constant.MOD_ID, Constant.SlotSprite.THERMAL_HEAD)
    };
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final int OXYGEN_TANK_1_SLOT = 4;
    public static final int OXYGEN_TANK_2_SLOT = 5;

    public final FixedItemInv inventory;

    public final PlayerEntity player;

    public GalacticraftPlayerInventoryScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(GalacticraftScreenHandlerType.PLAYER_INV_GC_HANDLER, syncId);

        this.player = player;
        this.inventory = ((GearInventoryProvider)player).getGearInv();
        Inventory inventory = new InventoryFixedWrapper(this.inventory) {
            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return GalacticraftPlayerInventoryScreenHandler.this.player == player;
            }
        };

        for (int slotY = 0; slotY < 4; ++slotY) {
            EquipmentSlot slot = EQUIPMENT_SLOT_ORDER[slotY];
            int finalSlotY = slotY;
            this.addSlot(new Slot(inventory, finalSlotY, 8, 8 + slotY * 18) {
                @Override
                public int getMaxItemCount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return slot == getPreferredEquipmentSlot(stack);
                }

                @Override
                public boolean canTakeItems(PlayerEntity player) {
                    return GalacticraftPlayerInventoryScreenHandler.this.player == player;
                }

                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_IDS[slot.getEntitySlotId()]);
                }
            });
        }

        this.addSlot(new OxygenTankSlot(inventory, OXYGEN_TANK_1_SLOT, 80, 8 + 2 * 18));
        this.addSlot(new OxygenTankSlot(inventory, OXYGEN_TANK_2_SLOT, 80, 8 + 3 * 18));

        this.addSlot(new AccessorySlot(inventory, 6, 80, 8));
        this.addSlot(new AccessorySlot(inventory, 7, 80, 8 + 18));

        int accessorySlot = 0;
        for (int i = 8; i < 12; i++) {
            this.addSlot(new AccessorySlot(inventory, i, 80 + 18, 8 + accessorySlot * 18));
            accessorySlot++;
        }

        // Player main inv
        for (int slotY = 0; slotY < 3; ++slotY) {
            for (int slotX = 0; slotX < 9; ++slotX) {
                this.addSlot(new Slot(playerInventory, slotX + (slotY + 1) * 9, 8 + slotX * 18, 84 + slotY * 18));
            }
        }

        // Player hotbar
        for (int slotY = 0; slotY < 9; ++slotY) {
            this.addSlot(new Slot(playerInventory, slotY, 8 + slotY * 18, 142));
        }
    }

    public GalacticraftPlayerInventoryScreenHandler(int syncId, PlayerInventory inv) {
        this(syncId, inv, inv.player);
    }

    private EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
        Item item_1 = stack.getItem();
        return ((ThermalArmorItem) item_1).getSlotType();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.getUuid().equals(this.player.getUuid());
    }

    private static class OxygenTankSlot extends Slot {
        public OxygenTankSlot(Inventory gearInventory, int slotId, int x, int y) {
            super(gearInventory, slotId, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return OxygenTankUtil.isOxygenTank(stack);
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }

        @Nullable
        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
            return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constant.MOD_ID, Constant.SlotSprite.OXYGEN_TANK));
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotFrom = this.slots.get(index);
        if (slotFrom.hasStack()) {
            ItemStack stackFrom = slotFrom.getStack();
            stack = stackFrom.copy();

            // Index of Indexes :)
            // 0-3 (4): GC, thermal armor slots;
            // 4-5 (2): GC, oxygen tank slots;
            // 6-11 (6): GC, accessory slots;
            // 12-38 (27): MC, non-hotbar inventory slots;
            // 39-48 (9): MC, hotbar slots.
            if (index < 12) {
                if (!this.insertItem(stackFrom, 12, 48, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 39) {
                if (!this.insertItem(stackFrom, 0, 8, true) &&
                    !this.insertItem(stackFrom, 39, 48, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 49) {
                if (!this.insertItem(stackFrom, 0, 8, true) &&
                    !this.insertItem(stackFrom, 12, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }

            slotFrom.onQuickTransfer(stackFrom, stack);

            if (stackFrom.isEmpty()) {
                slotFrom.setStack(ItemStack.EMPTY);
            } else {
                slotFrom.markDirty();
            }

            if (stackFrom.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slotFrom.onTakeItem(player, stackFrom);
            if (index == 0) {
                player.dropItem(stackFrom, false);
            }
        }

        return stack;
    }
}
