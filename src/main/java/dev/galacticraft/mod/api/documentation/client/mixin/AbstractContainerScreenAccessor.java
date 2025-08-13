package dev.galacticraft.mod.api.documentation.client.mixin;

import dev.galacticraft.mod.api.documentation.client.HoveredSlotAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenAccessor implements HoveredSlotAccessor {
    @Shadow protected Slot hoveredSlot;
    @Override public Slot gc$getHoveredSlot() { return hoveredSlot; }
}