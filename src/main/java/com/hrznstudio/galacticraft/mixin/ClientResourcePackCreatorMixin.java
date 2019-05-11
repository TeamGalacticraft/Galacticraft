package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.misc.NewGalacticraftTexturePack;
import net.minecraft.client.resource.ClientResourcePackCreator;
import net.minecraft.resource.ResourcePackContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Map;

@Mixin(ClientResourcePackCreator.class)
public class ClientResourcePackCreatorMixin {

    @Inject(method = "registerContainer", at = @At("TAIL"), cancellable = true)
    private void registerContainer(Map<String, ResourcePackContainer> map, ResourcePackContainer.Factory<?> packContainerFactory, CallbackInfo ci) {
        File resourcePackDirectory = new File((Galacticraft.class.getResource("GalacticraftClient.class").getFile().replace("com/hrznstudio/galacticraft/GalacticraftClient.class", "") + "new/").replace("%20", " ").substring(1));
        if (resourcePackDirectory.isDirectory()) {
            ResourcePackContainer packContainer = ResourcePackContainer.of("galacticraft-rewoven:new_textures", false, () -> new NewGalacticraftTexturePack(resourcePackDirectory), packContainerFactory, ResourcePackContainer.SortingDirection.TOP);
            if (packContainer != null) {
                map.put("galacticraft-rewoven:new_textures", packContainer);
            }
        }

    }
}
