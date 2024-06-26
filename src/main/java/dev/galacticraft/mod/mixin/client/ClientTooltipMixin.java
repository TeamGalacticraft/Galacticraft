package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.content.CannedFoodTooltip;
import dev.galacticraft.mod.content.ClientCannedFoodTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipMixin {
    /**
     * @author me
     * @reason to add exception for CannedFoodTooltip component
     */
    @Overwrite
    static ClientTooltipComponent create(TooltipComponent data) {
        if (data instanceof BundleTooltip) {
            return new ClientBundleTooltip((BundleTooltip)data);
        } else if (data instanceof CannedFoodTooltip){
            return new ClientCannedFoodTooltip((CannedFoodTooltip)data);
        } else {
            throw new IllegalArgumentException("Unknown TooltipComponent");
        }
    }
}
