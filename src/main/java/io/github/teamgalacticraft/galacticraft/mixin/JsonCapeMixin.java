package io.github.teamgalacticraft.galacticraft.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.teamgalacticraft.galacticraft.Constants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
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
@Mixin(PlayerListEntry.class)
public abstract class JsonCapeMixin {

    @Shadow
    @Final
    private Map<MinecraftProfileTexture.Type, Identifier> textures;

    @Inject(at = @At("RETURN"), method = "loadTextures")
    private void loadTextures(CallbackInfo callbackInfo) {
        for (Entity entity : MinecraftClient.getInstance().world.getPlayers()) {
            if (entity instanceof PlayerEntity) {
                textures.put(MinecraftProfileTexture.Type.CAPE, new Identifier(Constants.MOD_ID, "textures/cape/developer_cape.png"));
            }
        }
    }
}
