/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.machinelib.api.filter.ResourceFilter;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.OxygenGearItem;
import dev.galacticraft.mod.content.item.OxygenMaskItem;
import dev.galacticraft.mod.content.item.ThermalArmorItem;
import dev.galacticraft.mod.screen.slot.AccessorySlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GCPlayerInventoryMenu extends AbstractContainerMenu {
    private static final ResourceLocation[] EMPTY_ARMOR_SLOT_IDS = new ResourceLocation[]{
            Constant.id(Constant.SlotSprite.THERMAL_BOOTS),
            Constant.id(Constant.SlotSprite.THERMAL_PANTS),
            Constant.id(Constant.SlotSprite.THERMAL_CHEST),
            Constant.id(Constant.SlotSprite.THERMAL_HEAD)
    };
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final int OXYGEN_TANK_1_SLOT = 4;
    public static final int OXYGEN_TANK_2_SLOT = 5;

    public final Container inventory;

    public final Player player;

    public GCPlayerInventoryMenu(int syncId, Inventory playerInventory, Player player) {
        super(GCMenuTypes.PLAYER_INV_GC, syncId);

        this.player = player;
        this.inventory = player.galacticraft$getGearInv();

        for (int slotY = 0; slotY < 4; ++slotY) {
            EquipmentSlot slot = EQUIPMENT_SLOT_ORDER[slotY];
            int finalSlotY = slotY;
            this.addSlot(new Slot(inventory, finalSlotY, 8, 8 + slotY * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return slot == getPreferredEquipmentSlot(stack);
                }

                @Override
                public boolean mayPickup(Player player) {
                    return GCPlayerInventoryMenu.this.player == player;
                }

                @Override
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_ARMOR_SLOT_IDS[slot.getIndex()]);
                }
            });
        }

        this.addSlot(new OxygenTankSlot(inventory, OXYGEN_TANK_1_SLOT, 80, 8 + 2 * 18));
        this.addSlot(new OxygenTankSlot(inventory, OXYGEN_TANK_2_SLOT, 80, 8 + 3 * 18));

        this.addSlot(new AccessorySlot(inventory, 6, 80, 8, OxygenMaskItem.class, Constant.id(Constant.SlotSprite.OXYGEN_MASK)));
        this.addSlot(new AccessorySlot(inventory, 7, 80, 8 + 18, OxygenGearItem.class, Constant.id(Constant.SlotSprite.OXYGEN_GEAR)));

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

    public GCPlayerInventoryMenu(int syncId, Inventory inv) {
        this(syncId, inv, inv.player);
    }

    private EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
        Item item_1 = stack.getItem();
        if (item_1 instanceof ThermalArmorItem thermalArmorItem)
            return thermalArmorItem.getSlotGroup().getSlot();
        return LivingEntity.getEquipmentSlotForItem(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getUUID().equals(this.player.getUUID());
    }

    private static class OxygenTankSlot extends Slot {
        private static final ResourceFilter<Item> FILTER = ResourceFilters.canExtractFluid(Gases.OXYGEN);
        public OxygenTankSlot(Container gearInventory, int slotId, int x, int y) {
            super(gearInventory, slotId, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return FILTER.test(stack.getItem(), stack.getTag());
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Nullable
        @Override
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.id(Constant.SlotSprite.OXYGEN_TANK));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotFrom = this.slots.get(index);
        if (slotFrom.hasItem()) {
            ItemStack stackFrom = slotFrom.getItem();
            stack = stackFrom.copy();

            // Index of Indexes :)
            // 0-3 (4): GC, thermal armor slots;
            // 4-5 (2): GC, oxygen tank slots;
            // 6-11 (6): GC, accessory slots;
            // 12-38 (27): MC, non-hotbar inventory slots;
            // 39-48 (9): MC, hotbar slots.
            if (index < 12) {
                if (!this.moveItemStackTo(stackFrom, 12, 48, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 39) {
                if (!this.moveItemStackTo(stackFrom, 0, 8, true) &&
                    !this.moveItemStackTo(stackFrom, 39, 48, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 49) {
                if (!this.moveItemStackTo(stackFrom, 0, 8, true) &&
                    !this.moveItemStackTo(stackFrom, 12, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }

            slotFrom.onQuickCraft(stackFrom, stack);

            if (stackFrom.isEmpty()) {
                slotFrom.set(ItemStack.EMPTY);
            } else {
                slotFrom.setChanged();
            }

            if (stackFrom.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slotFrom.onTake(player, stackFrom);
            if (index == 0) {
                player.drop(stackFrom, false);
            }
        }

        return stack;
    }
}
