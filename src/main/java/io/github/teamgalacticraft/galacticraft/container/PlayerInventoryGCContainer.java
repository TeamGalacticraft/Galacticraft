package io.github.teamgalacticraft.galacticraft.container;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.teamgalacticraft.galacticraft.accessor.GCPlayerAccessor;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import io.github.teamgalacticraft.galacticraft.items.OxygenTankItem;
import io.github.teamgalacticraft.galacticraft.items.ThermalArmorItem;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class PlayerInventoryGCContainer extends Container {
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static final SimpleFixedItemInv inv = new SimpleFixedItemInv(11);
    private final boolean remote;
    private final PartialInventoryFixedWrapper gearInventory;
    private PlayerEntity player;

    public PlayerInventoryGCContainer(PlayerInventory playerInventory, boolean isServer, PlayerEntity playerEntity) {
        super(null, 0);

        this.gearInventory = new PartialInventoryFixedWrapper(((GCPlayerAccessor) (Object) playerEntity).getGearInventory()) {
            @Override
            public boolean canPlayerUseInv(PlayerEntity var1) {
                return true;
            }

            @Override
            public void markDirty() {
                //TODO
            }
        };

        this.player = playerEntity;
        this.remote = isServer;

        int slotX;
        int slotY;

        for (slotY = 0; slotY < 4; ++slotY) {
            EquipmentSlot slot = EQUIPMENT_SLOT_ORDER[slotY];
            this.addSlot(new Slot(gearInventory, slotY, 8, 8 + slotY * 18) {
                @Override
                public int getMaxStackAmount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack itemStack_1) {
                    return slot == getPreferredEquipmentSlot(itemStack_1);
                }
            });
        }
        this.addSlot(new ItemSpecificSlot(gearInventory, 4, 77, 8 + 0 * 18, GalacticraftItems.OXYGEN_MASK) {
            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        this.addSlot(new ItemSpecificSlot(gearInventory, 5, 77, 8 + 1 * 18, GalacticraftItems.OXYGEN_GEAR) {
            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        this.addSlot(new OxygenTankSlot(gearInventory, 6, 77, 8 + 2 * 18));
        this.addSlot(new OxygenTankSlot(gearInventory, 7, 77, 8 + 3 * 18));

        int accessorySlot = 0;
        for (int i = 8; i < 12; i++) {
            this.addSlot(new Slot(gearInventory, i, 77 + 18, 8 + accessorySlot * 18));
            accessorySlot++;
        }


        // Player main inv
        for (slotY = 0; slotY < 3; ++slotY) {
            for (slotX = 0; slotX < 9; ++slotX) {
                this.addSlot(new Slot(playerInventory, slotX + (slotY + 1) * 9, 8 + slotX * 18, 84 + slotY * 18));
            }
        }

        // Player hotbar
        for (slotY = 0; slotY < 9; ++slotY) {
            this.addSlot(new Slot(playerInventory, slotY, 8 + slotY * 18, 142));
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
                return inv.getInvStack(0);
            case CHEST:
                return inv.getInvStack(1);
            case LEGS:
                return inv.getInvStack(2);
            case FEET:
                return inv.getInvStack(3);
            default:
                return ItemStack.EMPTY;
        }
    }

    public void setThermalPiece(EquipmentSlot slot, ItemStack thermalPiece) {
        switch (slot) {
            case HEAD:
                inv.setInvStack(0, thermalPiece, Simulation.ACTION);
                return;
            case CHEST:
                inv.setInvStack(1, thermalPiece, Simulation.ACTION);
                return;
            case LEGS:
                inv.setInvStack(2, thermalPiece, Simulation.ACTION);
                return;
            case FEET:
                inv.setInvStack(3, thermalPiece, Simulation.ACTION);
                return;
            default:
                throw new IllegalArgumentException("Invalid EquipmentSlot " + slot + "!");
        }
    }

    private class OxygenTankSlot extends Slot {
        public OxygenTankSlot(PartialInventoryFixedWrapper gearInventory, int slotId, int x, int y) {
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
    }
}
