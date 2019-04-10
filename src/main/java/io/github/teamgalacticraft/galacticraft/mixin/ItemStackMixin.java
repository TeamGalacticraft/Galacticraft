package io.github.teamgalacticraft.galacticraft.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<TextComponent> returnable) {
        Identifier id = Registry.ITEM.getId(getItem());
        if (false && id.getNamespace().equals("galacticraft-rewoven")) {
            TextComponent returnVal = returnable.getReturnValue();
            if (returnVal.getStyle().getColor() == null) {
                returnable.setReturnValue(returnVal.setStyle(returnVal.getStyle().setColor(TextFormat.BLUE)));
            }
        }
    }
}