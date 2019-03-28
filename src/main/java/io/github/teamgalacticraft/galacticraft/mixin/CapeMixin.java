package io.github.teamgalacticraft.galacticraft.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.teamgalacticraft.galacticraft.Constants;
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

    @Shadow @Final private Map<MinecraftProfileTexture.Type, Identifier> field_3742;

    @Inject(at = @At("RETURN"), method = "method_2969")
    private void method_2969(CallbackInfo info) {
        for (Entity r : MinecraftClient.getInstance().world.getPlayers()) {
            if (r instanceof PlayerEntity) {
                for (String uuid : Capes.getCapeUsers()) {
                    if (uuid.equals(r.getUuidAsString().replace("-", ""))) {
                        field_3742.put(MinecraftProfileTexture.Type.CAPE, new Identifier(Constants.MOD_ID, "textures/cape/cape_" + /*ConfigHandler.capeType*/ "earth" + ".png"));
                    }
                }
            }
                /*TypeConverters.UuidConverter converter = new TypeConverters.UuidConverter();
                try {
                    String username = Capes.getName(uuid);
                    if (username.equals(getDisplayName().getString())) {
                        this.getDisplayName();
                        field_3742.put(MinecraftProfileTexture.Type.CAPE, new Identifier(Constants.MOD_ID, "aijdksl"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
        }
    }
}
