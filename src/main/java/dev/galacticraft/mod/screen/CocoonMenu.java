package dev.galacticraft.mod.screen;

import dev.galacticraft.mod.content.block.entity.CocoonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class CocoonMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerLevelAccess access;

    public CocoonMenu(int syncId, Inventory playerInv, Container cocoon, BlockPos pos) {
        super(GCMenuTypes.COCOON_MENU, syncId);
        this.container = cocoon;
        this.access = ContainerLevelAccess.create(playerInv.player.level(), pos);

        // Cocoon slots: 5 x 3
        int startX = 44; // adjust to taste
        int startY = 18;
        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                addSlot(new Slot(container, index++, startX + col * 18, startY + row * 18));
            }
        }

        // Player inv (3 rows)
        int invY = startY + 18 * 3 + 12;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                addSlot(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, invY + r * 18));
            }
        }
        // Hotbar
        int hotbarY = invY + 58;
        for (int c = 0; c < 9; c++) {
            addSlot(new Slot(playerInv, c, 8 + c * 18, hotbarY));
        }
    }

    // Convenience ctor if you use MenuScreens.register with a factory taking only (id, inv)
    public CocoonMenu(int syncId, Inventory playerInv) {
        this(syncId, playerInv, new net.minecraft.world.SimpleContainer(CocoonBlockEntity.SLOT_COUNT), BlockPos.ZERO);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack cur = slot.getItem();
            ret = cur.copy();

            int cocoonSlots = CocoonBlockEntity.SLOT_COUNT;
            // shift-click from cocoon -> player
            if (index < cocoonSlots) {
                if (!this.moveItemStackTo(cur, cocoonSlots, this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                // player -> cocoon
                if (!this.moveItemStackTo(cur, 0, cocoonSlots, false)) return ItemStack.EMPTY;
            }
            if (cur.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return ret;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}