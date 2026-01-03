package dev.galacticraft.mod.accessor;

import dev.galacticraft.mod.screen.slot.AccessorySlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public interface GCCreativeGuiSlots {
    boolean gc$isGCInventoryEnabled();
    List<AccessorySlot> gc$getRenderSlots();
    void gc$renderGcSlots(GuiGraphics g, int mouseX, int mouseY);
}
