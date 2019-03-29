package io.github.teamgalacticraft.galacticraft.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.teamgalacticraft.galacticraft.misc.Capes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ScoreboardEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
@Mixin(ScoreboardEntry.class)
public abstract class CapeMixin {

    @Shadow
    @Final
    private Map<MinecraftProfileTexture.Type, Identifier> field_3742;

    @Inject(at = @At("RETURN"), method = "method_2969")
    private void method_2969(CallbackInfo info) {
        for (Entity r : MinecraftClient.getInstance().world.getPlayers()) {
            if (r instanceof PlayerEntity) {
                if (Capes.getCapeMap().get(r.getUuidAsString().replace("-", "")) != null) {
                    field_3742.put(MinecraftProfileTexture.Type.CAPE, Capes.getCapeMap().get(r.getUuidAsString().replace("-", "")));
                    System.out.println(Capes.getCapeMap().get(r.getUuidAsString().replace("-", "")));
                }
            }
        }
    }
}
