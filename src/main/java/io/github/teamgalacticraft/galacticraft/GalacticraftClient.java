package io.github.teamgalacticraft.galacticraft;

import io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator.CoalGeneratorScreen;
import io.github.teamgalacticraft.galacticraft.container.GalacticraftContainers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        ClientSpriteRegistryCallback.EVENT.register(((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.COAL_GENERATOR_SCREEN))));
        ClientSpriteRegistryCallback.EVENT.register(((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.MACHINE_CONFIG_TABS))));

        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.COAL_GENERATOR_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new CoalGeneratorScreen(syncId, packetByteBuf.readBlockPos(), playerEntity));
    }
}
