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

import java.io.*;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Mixin(ClientResourcePackCreator.class)
public class ClientResourcePackCreatorMixin {

    private boolean dl = false;
    private boolean iWantTheResourcePack = false;

    @Inject(method = "registerContainer", at = @At("TAIL"), cancellable = true)
    private void registerContainer(Map<String, ResourcePackContainer> map, ResourcePackContainer.Factory<?> packContainerFactory, CallbackInfo ci) {
        if (iWantTheResourcePack) {
            if (!dl) {
                dl = true;
                if (!new File(MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/b7/b7fee1c45fab62449d4b96c74f13bdbab05a30b1").exists()) {
                    if (!new File(MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/b7/").isDirectory()) {
                        new File(MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/b7/").mkdirs();
                    }
                    try {
                        ZipFile jarFile = new ZipFile(new File((Galacticraft.class.getResource("GalacticraftClient.class").getFile().replace("!/com/hrznstudio/galacticraft/GalacticraftClient.class", "")).replace("%20", " ").replace("file:\\", "")).toString().replace("%20", " ").replace("file:\\", ""));
                        ZipEntry rp = jarFile.getEntry("b7fee1c45fab62449d4b96c74f13bdbab05a30b1");
                        InputStream in = jarFile.getInputStream(rp);
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(
                                new File((MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/b7/b7fee1c45fab62449d4b96c74f13bdbab05a30b1")
                                        .replace("file:\\", ""))));

                        byte[] buffer = new byte[1024];
                        int lengthRead;
                        while ((lengthRead = in.read(buffer)) > 0) {
                            out.write(buffer, 0, lengthRead);
                            out.flush();
                        }
                    } catch (IOException | NullPointerException ignore) {}
                }
            }
            File resourcePackFile = new File(MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/b7/b7fee1c45fab62449d4b96c74f13bdbab05a30b1");
            if (resourcePackFile.isFile()) {
                ResourcePackContainer packContainer = ResourcePackContainer.of("galacticraft-rewoven:new_textures", false, () -> new NewGalacticraftTexturePack(resourcePackFile), packContainerFactory, ResourcePackContainer.InsertionPosition.TOP);
                if (packContainer != null) {
                    map.put("galacticraft-rewoven:new_textures", packContainer);
                }
            }
        }
    }
}
