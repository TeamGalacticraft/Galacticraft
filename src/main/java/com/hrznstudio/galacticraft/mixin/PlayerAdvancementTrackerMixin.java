package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.api.research.PlayerResearchTracker;
import net.minecraft.advancement.PlayerAdvancementTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Inject(at = @At("HEAD"), method = "load", cancellable = true)
    private void cancelLoadForResearch(CallbackInfo ci) {
        //noinspection ConstantConditions
        if (((Object) this) instanceof PlayerResearchTracker) {
            ci.cancel();
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }
}
