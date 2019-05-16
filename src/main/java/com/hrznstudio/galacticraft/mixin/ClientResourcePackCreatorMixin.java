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

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Mixin(ClientResourcePackCreator.class)
public class ClientResourcePackCreatorMixin {

    private boolean dl = false;

    @Inject(method = "registerContainer", at = @At("TAIL"), cancellable = true)
    private void registerContainer(Map<String, ResourcePackContainer> map, ResourcePackContainer.Factory<?> packContainerFactory, CallbackInfo ci) {
        if (!dl) {
            dl = true;
            if (!new File(MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/b7/b7fee1c45fab62449d4b96c74f13bdbab05a30b1").exists()) {
                if (!new File(MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/b7/").isDirectory()) {
                    new File(MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/b7").mkdirs();
                }
                try {
                    InputStream in = new BufferedInputStream(new FileInputStream(
                            new File((Galacticraft.class.getResource("GalacticraftClient.class").getFile().replace("com/hrznstudio/galacticraft/GalacticraftClient.class", "") + "b7fee1c45fab62449d4b96c74f13bdbab05a30b1").replace("%20", " "))));
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(
                            new File((MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/b7/b7fee1c45fab62449d4b96c74f13bdbab05a30b1").replace("file:\\", ""))));

                    byte[] buffer = new byte[1024];
                    int lengthRead;
                    while ((lengthRead = in.read(buffer)) > 0) {
                        out.write(buffer, 0, lengthRead);
                        out.flush();
                    }
                } catch (IOException e) {
                    Galacticraft.logger.error("Unable to copy the new GC Textures!");
                    e.printStackTrace();
                }
            } else {
                try {
                    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                    try (InputStream input = new FileInputStream(new File(MinecraftClient.getInstance().runDirectory.toString() + "/assets/objects/b7/b7fee1c45fab62449d4b96c74f13bdbab05a30b1"))) {
                        byte[] buffer = new byte[8192];
                        int len = input.read(buffer);
                        while (len != -1) {
                            sha1.update(buffer, 0, len);
                            len = input.read(buffer);
                        }

                        new HexBinaryAdapter().marshal(sha1.digest());
                    }
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
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
