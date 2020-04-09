package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.api.rocket.RocketData;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.hrznstudio.galacticraft.api.rocket.RocketParts;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.hrznstudio.galacticraft.items.RocketItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemDynamicRenderer.class)
public class ItemDynamicRendererMixin {
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void renderGC(ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof RocketItem) {
            RocketData data = RocketData.fromItem(stack);
            RocketEntity entity = GalacticraftEntityTypes.ROCKET.create(null);
            if (!data.isEmpty()) {
                entity.setColor(data.getRed(), data.getGreen(), data.getBlue(), data.getAlpha());
                entity.setParts(data.getParts());
            } else {
                entity.setColor(255, 255, 255, 255);
                for (RocketPartType type : RocketPartType.values()) {
                    entity.setPart(RocketParts.getPartForType(type));
                }
            }
            entity.pitch = 45;
            entity.yaw = 30;
            MinecraftClient.getInstance().getEntityRenderManager().render(entity, 0, 0, 0, entity.yaw, 1.0F, true);
            ci.cancel();
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }
}
