package dev.galacticraft.mod.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AirlockControllerMenu extends AbstractContainerMenu {
    public AirlockControllerMenu(int syncId, Inventory inventory) {
        super(GCScreenHandlerType.AIRLOCK_CONTROLLER_MENU, syncId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
