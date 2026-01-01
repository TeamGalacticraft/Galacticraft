package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.screen.slot.AccessorySlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends AbstractContainerMenu {

    protected InventoryMenuMixin(@Nullable MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addGcAccessorySlots(Inventory inv, boolean onServer, Player player, CallbackInfo ci) {
        var gearInv = player.galacticraft$getGearInv();
        if (gearInv == null) return;

        for (int i = 0; i < 12; i++) {
            this.addSlot(new AccessorySlot(
                    gearInv, player, i,
                    -2000, -2000,
                    GCAccessorySlots.SLOT_TAGS.get(i),
                    GCAccessorySlots.SLOT_SPRITES.get(i)
            ));
        }
    }
}
