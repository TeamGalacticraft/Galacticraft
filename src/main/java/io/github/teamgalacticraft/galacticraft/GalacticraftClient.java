package io.github.teamgalacticraft.galacticraft;

import io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelScreen;
import io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorScreen;
import io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator.CoalGeneratorScreen;
import io.github.teamgalacticraft.galacticraft.blocks.machines.compressor.CompressorScreen;
import io.github.teamgalacticraft.galacticraft.blocks.machines.electriccompressor.ElectricCompressorScreen;
import io.github.teamgalacticraft.galacticraft.blocks.machines.energystoragemodule.EnergyStorageModuleScreen;
import io.github.teamgalacticraft.galacticraft.blocks.machines.oxygencollector.OxygenCollectorScreen;
import io.github.teamgalacticraft.galacticraft.client.render.block.entity.GalacticraftBlockEntityRenderers;
import io.github.teamgalacticraft.galacticraft.container.GalacticraftContainers;
import io.github.teamgalacticraft.galacticraft.container.screen.PlayerInventoryGCScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.COAL_GENERATOR_SCREEN)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.BASIC_SOLAR_PANEL_SCREEN)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.MACHINE_CONFIG_TABS)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.CIRCUIT_FABRICATOR_SCREEN)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.PLAYER_INVENTORY_SCREEN)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.PLAYER_INVENTORY_TABS)));

        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.ScreenTextures.THERMAL_HEAD)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.ScreenTextures.THERMAL_CHEST)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.ScreenTextures.THERMAL_PANTS)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.ScreenTextures.THERMAL_BOOTS)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.ScreenTextures.OXYGEN_MASK)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.ScreenTextures.OXYGEN_GEAR)));
        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> registry.register(new Identifier(Constants.ScreenTextures.OXYGEN_TANK)));

        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.PLAYER_INVENTORY_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new PlayerInventoryGCScreen(playerEntity));

        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.COAL_GENERATOR_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new CoalGeneratorScreen(syncId, packetByteBuf.readBlockPos(), playerEntity));
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.BASIC_SOLAR_PANEL_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new BasicSolarPanelScreen(syncId, packetByteBuf.readBlockPos(), playerEntity));
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.CIRCUIT_FABRICATOR_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new CircuitFabricatorScreen(syncId, packetByteBuf.readBlockPos(), playerEntity));
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.COMPRESSOR_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new CompressorScreen(syncId, packetByteBuf.readBlockPos(), playerEntity));
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.ELECTRIC_COMPRESSOR_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new ElectricCompressorScreen(syncId, packetByteBuf.readBlockPos(), playerEntity));
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.ENERGY_STORAGE_MODULE_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new EnergyStorageModuleScreen(syncId, packetByteBuf.readBlockPos(), playerEntity));
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.OXYGEN_COLLECTOR_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new OxygenCollectorScreen(syncId, packetByteBuf.readBlockPos(), playerEntity));

        GalacticraftBlockEntityRenderers.register();

        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            try {
                Class<?> clazz = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
                Method method = clazz.getMethod("addConfigOverride", String.class, Runnable.class);
                method.invoke(null, Constants.MOD_ID, (Runnable) () -> Galacticraft.configHandler.openConfigScreen());
            } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                Galacticraft.logger.error("[Galacticraft] Failed to add modmenu config override. {1}", e);
            }
        }

    }
}
