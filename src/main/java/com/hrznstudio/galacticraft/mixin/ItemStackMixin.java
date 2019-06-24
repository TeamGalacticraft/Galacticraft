package com.hrznstudio.galacticraft.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void getName(CallbackInfoReturnable<Text> returnable) {
        Identifier id = Registry.ITEM.getId(getItem());
        if (false && id.getNamespace().equals("galacticraft-rewoven")) {
            Text returnVal = returnable.getReturnValue();
            if (returnVal.getStyle().getColor() == null) {
                returnable.setReturnValue(returnVal.setStyle(returnVal.getStyle().setColor(Formatting.BLUE)));
            }
        }
    }
}