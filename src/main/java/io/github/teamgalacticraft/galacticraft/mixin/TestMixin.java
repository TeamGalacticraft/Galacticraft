package io.github.teamgalacticraft.galacticraft.mixin;

import io.github.teamgalacticraft.galacticraft.Galacticraft;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
@Mixin(MinecraftClient.class)
public class TestMixin {

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        Galacticraft.logger.info("Loaded Mixin");
    }
}
