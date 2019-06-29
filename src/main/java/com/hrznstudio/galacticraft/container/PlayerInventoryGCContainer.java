package com.hrznstudio.galacticraft.container;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.container.slot.ItemSpecificSlot;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import com.hrznstudio.galacticraft.items.ThermalArmorItem;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class PlayerInventoryGCContainer extends Container {
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
                public String getBackgroundSprite() {
                    return EMPTY_ARMOR_SLOT_IDS[finalSlotY];
                }
            });
        }
        this.addSlot(new ItemSpecificSlot(this.gearInventory, 4, 80, 8 + 0 * 18, GalacticraftItems.OXYGEN_MASK) {
            @Override
            public String getBackgroundSprite() {
                return Constants.MOD_ID + ":" + Constants.SlotSprites.OXYGEN_MASK;
            }
        });
        this.addSlot(new ItemSpecificSlot(this.gearInventory, 5, 80, 8 + 1 * 18, GalacticraftItems.OXYGEN_GEAR) {
            @Override
            public String getBackgroundSprite() {
                return Constants.MOD_ID + ":" + Constants.SlotSprites.OXYGEN_GEAR;
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
                return gearInventory.getInvStack(0);
            case CHEST:
                return gearInventory.getInvStack(1);
            case LEGS:
                return gearInventory.getInvStack(2);
            case FEET:
                return gearInventory.getInvStack(3);
            default:
                return ItemStack.EMPTY;
        }
    }

    public void setThermalPiece(EquipmentSlot slot, ItemStack thermalPiece) {
        switch (slot) {
            case HEAD:
                gearInventory.setInvStack(0, thermalPiece);
                return;
            case CHEST:
                gearInventory.setInvStack(1, thermalPiece);
                return;
            case LEGS:
                gearInventory.setInvStack(2, thermalPiece);
                return;
            case FEET:
                gearInventory.setInvStack(3, thermalPiece);
                return;
            default:
                throw new IllegalArgumentException("Invalid EquipmentSlot " + slot + "!");
        }
    }

    private class OxygenTankSlot extends Slot {
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

        @Override
        public String getBackgroundSprite() {
            return Constants.MOD_ID + ":" + Constants.SlotSprites.OXYGEN_TANK;
        }
    }
}