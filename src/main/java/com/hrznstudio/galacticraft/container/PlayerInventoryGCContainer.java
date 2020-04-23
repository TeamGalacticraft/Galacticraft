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

package com.hrznstudio.galacticraft.container;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.container.slot.ItemSpecificSlot;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import com.hrznstudio.galacticraft.items.ThermalArmorItem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class PlayerInventoryGCContainer extends ScreenHandler {
    private static final String[] EMPTY_ARMOR_SLOT_IDS = new String[]{
            Constants.MOD_ID + ":" + Constants.SlotSprites.THERMAL_HEAD,
            Constants.MOD_ID + ":" + Constants.SlotSprites.THERMAL_CHEST,
            Constants.MOD_ID + ":" + Constants.SlotSprites.THERMAL_PANTS,
            Constants.MOD_ID + ":" + Constants.SlotSprites.THERMAL_BOOTS};
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static int OXYGEN_TANK_1_SLOT = 6, OXYGEN_TANK_2_SLOT = 7;

    public final BasicInventory gearInventory = new BasicInventory(12);
    private final boolean remote;
    private PlayerEntity player;

    public PlayerInventoryGCContainer(PlayerInventory playerInventory, boolean isServer, PlayerEntity playerEntity) {
        super(null, 1);

        this.player = playerEntity;
        this.remote = isServer;

        int slotX;
        int slotY;

        for (slotY = 0; slotY < 4; ++slotY) {
            EquipmentSlot slot = EQUIPMENT_SLOT_ORDER[slotY];
            int finalSlotY = slotY;
            this.addSlot(new Slot(this.gearInventory, finalSlotY, 8, 8 + slotY * 18) {
                @Override
                public int getMaxStackAmount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack itemStack_1) {
                    return slot == getPreferredEquipmentSlot(itemStack_1);
                }

                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, EMPTY_ARMOR_SLOT_IDS[slot.getEntitySlotId()]));
                }
            });
        }
        this.addSlot(new ItemSpecificSlot(this.gearInventory, 4, 80, 8, GalacticraftItems.OXYGEN_MASK) {
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, Constants.SlotSprites.OXYGEN_MASK));
            }
        });
        this.addSlot(new ItemSpecificSlot(this.gearInventory, 5, 80, 8 + 18, GalacticraftItems.OXYGEN_GEAR) {
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, Constants.SlotSprites.OXYGEN_GEAR));
            }
        });
        this.addSlot(new OxygenTankSlot(this.gearInventory, OXYGEN_TANK_1_SLOT, 80, 8 + 2 * 18));
        this.addSlot(new OxygenTankSlot(this.gearInventory, OXYGEN_TANK_2_SLOT, 80, 8 + 3 * 18));

        int accessorySlot = 0;
        for (int i = 8; i < 12; i++) {
            this.addSlot(new Slot(this.gearInventory, i, 80 + 18, 8 + accessorySlot * 18));
            accessorySlot++;
        }


        // Player main inv
        for (slotY = 0; slotY < 3; ++slotY) {
            for (slotX = 0; slotX < 9; ++slotX) {
                this.addSlot(new Slot(player.inventory, slotX + (slotY + 1) * 9, 8 + slotX * 18, 84 + slotY * 18));
            }
        }

        // Player hotbar
        for (slotY = 0; slotY < 9; ++slotY) {
            this.addSlot(new Slot(player.inventory, slotY, 8 + slotY * 18, 142));
        }
    }

    private EquipmentSlot getPreferredEquipmentSlot(ItemStack itemStack_1) {
        Item item_1 = itemStack_1.getItem();
        return ((ThermalArmorItem) item_1).getSlotType();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.getUuid().equals(this.player.getUuid());
    }

    public ItemStack getThermalPiece(EquipmentSlot slot) {
        switch (slot) {
            case HEAD:
                return gearInventory.getStack(0);
            case CHEST:
                return gearInventory.getStack(1);
            case LEGS:
                return gearInventory.getStack(2);
            case FEET:
                return gearInventory.getStack(3);
            default:
                return ItemStack.EMPTY;
        }
    }

    public void setThermalPiece(EquipmentSlot slot, ItemStack thermalPiece) {
        switch (slot) {
            case HEAD:
                gearInventory.setStack(0, thermalPiece);
                return;
            case CHEST:
                gearInventory.setStack(1, thermalPiece);
                return;
            case LEGS:
                gearInventory.setStack(2, thermalPiece);
                return;
            case FEET:
                gearInventory.setStack(3, thermalPiece);
                return;
            default:
                throw new IllegalArgumentException("Invalid EquipmentSlot " + slot + "!");
        }
    }

    private static class OxygenTankSlot extends Slot {
        public OxygenTankSlot(Inventory gearInventory, int slotId, int x, int y) {
            super(gearInventory, slotId, x, y);
        }

        @Override
        public boolean canInsert(ItemStack itemStack_1) {
            return itemStack_1.getItem() instanceof OxygenTankItem;
        }

        @Override
        public int getMaxStackAmount() {
            return 1;
        }

        @Nullable
        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
            return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, Constants.SlotSprites.OXYGEN_TANK));
        }
    }
}