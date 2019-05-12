package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.misc.NewGalacticraftTexturePack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ClientResourcePackCreator;
import net.minecraft.resource.ResourcePackContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Mixin(ClientResourcePackCreator.class)
public class ClientResourcePackCreatorMixin {

    private boolean dl = false;

    @Inject(method = "registerContainer", at = @At("TAIL"), cancellable = true)
    private void registerContainer(Map<String, ResourcePackContainer> map, ResourcePackContainer.Factory<?> packContainerFactory, CallbackInfo ci) {
        if (!dl) {
            dl = true;
            if (!new File (MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/B7/B7FEE1C45FAB62449D4B96C74F13BDBAB05A30B1".toLowerCase()).exists()) {
                if (!new File (MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/B7/".toLowerCase()).isDirectory()) {
                    new File(MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/B7".toLowerCase()).mkdirs();
                }
                try {
                    Files.copy(new File((Galacticraft.class.getResource("GalacticraftClient.class").getFile().replace("com/hrznstudio/galacticraft/GalacticraftClient.class", "") + "B7FEE1C45FAB62449D4B96C74F13BDBAB05A30B1").replace("%20", " ").substring(1)).toPath(), new File (MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/B7/B7FEE1C45FAB62449D4B96C74F13BDBAB05A30B1".toLowerCase()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    Galacticraft.logger.error("Unable to copy the new GC Textures!");
                    e.printStackTrace();
                }
            }
        }
        File resourcePackFile = new File (MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/B7/B7FEE1C45FAB62449D4B96C74F13BDBAB05A30B1".toLowerCase());
        if (resourcePackFile.isFile()) {
            ResourcePackContainer packContainer = ResourcePackContainer.of("galacticraft-rewoven:new_textures", false, () -> new NewGalacticraftTexturePack(resourcePackFile), packContainerFactory, ResourcePackContainer.SortingDirection.TOP);
            if (packContainer != null) {
                map.put("galacticraft-rewoven:new_textures", packContainer);
            }
        }

    }
}
