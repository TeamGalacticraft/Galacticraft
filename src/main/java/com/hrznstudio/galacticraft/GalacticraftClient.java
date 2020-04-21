/*
 * Copyright (c) 2019 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelScreen;
import com.hrznstudio.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorScreen;
import com.hrznstudio.galacticraft.blocks.machines.coalgenerator.CoalGeneratorScreen;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorScreen;
import com.hrznstudio.galacticraft.blocks.machines.electriccompressor.ElectricCompressorScreen;
import com.hrznstudio.galacticraft.blocks.machines.energystoragemodule.EnergyStorageModuleScreen;
import com.hrznstudio.galacticraft.blocks.machines.oxygencollector.OxygenCollectorScreen;
import com.hrznstudio.galacticraft.blocks.machines.refinery.RefineryScreen;
import com.hrznstudio.galacticraft.client.render.block.entity.GalacticraftBlockEntityRenderers;
import com.hrznstudio.galacticraft.client.render.entity.EvolvedCreeperEntityRenderer;
import com.hrznstudio.galacticraft.client.render.entity.evolvedzombie.EvolvedZombieRenderer;
import com.hrznstudio.galacticraft.client.render.entity.moonvillager.MoonVillagerRenderer;
import com.hrznstudio.galacticraft.client.render.entity.t1rocket.RocketEntityRenderer;
import com.hrznstudio.galacticraft.client.render.fluid.FluidRenderingResourceReloadListener;
import com.hrznstudio.galacticraft.container.GalacticraftContainers;
import com.hrznstudio.galacticraft.container.screen.PlayerInventoryGCScreen;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.misc.capes.CapeLoader;
import com.hrznstudio.galacticraft.misc.capes.JsonCapes;
import com.hrznstudio.galacticraft.particle.GalacticraftParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftClient implements ClientModInitializer {

    public static JsonCapes jsonCapes;
    public static CapeLoader capeLoader;


    @Override
    public void onInitializeClient() {
        long startInitTime = System.currentTimeMillis();
        Galacticraft.logger.info("[Galacticraft] Starting client initialization.");
        capeLoader = new CapeLoader();
        jsonCapes = new JsonCapes();
        capeLoader.register(jsonCapes);
        capeLoader.load();

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((spriteAtlasTexture, registry) -> {
//        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> {
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.COAL_GENERATOR_SCREEN));
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.BASIC_SOLAR_PANEL_SCREEN));
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.MACHINE_CONFIG_TABS));
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.MACHINE_CONFIG_PANELS));
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.CIRCUIT_FABRICATOR_SCREEN));
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.PLAYER_INVENTORY_SCREEN));
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.PLAYER_INVENTORY_TABS));
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.MAP_SCREEN));

            registry.register(new Identifier(Constants.MOD_ID, Constants.SlotSprites.THERMAL_HEAD));
            registry.register(new Identifier(Constants.MOD_ID, Constants.SlotSprites.THERMAL_CHEST));
            registry.register(new Identifier(Constants.MOD_ID, Constants.SlotSprites.THERMAL_PANTS));
            registry.register(new Identifier(Constants.MOD_ID, Constants.SlotSprites.THERMAL_BOOTS));
            registry.register(new Identifier(Constants.MOD_ID, Constants.SlotSprites.OXYGEN_MASK));
            registry.register(new Identifier(Constants.MOD_ID, Constants.SlotSprites.OXYGEN_GEAR));
            registry.register(new Identifier(Constants.MOD_ID, Constants.SlotSprites.OXYGEN_TANK));

            registry.register(Constants.Fluids.getIdentifier(Constants.Fluids.CRUDE_OIL_STILL));
            registry.register(Constants.Fluids.getIdentifier(Constants.Fluids.CRUDE_OIL_FLOWING));
            registry.register(Constants.Fluids.getIdentifier(Constants.Fluids.FUEL_STILL));
            registry.register(Constants.Fluids.getIdentifier(Constants.Fluids.FUEL_FLOWING));
        });

        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.PLAYER_INVENTORY_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new PlayerInventoryGCScreen(playerEntity));

        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.COAL_GENERATOR_CONTAINER, CoalGeneratorScreen.FACTORY);
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.BASIC_SOLAR_PANEL_CONTAINER, BasicSolarPanelScreen.FACTORY);
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.CIRCUIT_FABRICATOR_CONTAINER, CircuitFabricatorScreen.FACTORY);
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.REFINERY_CONTAINER, RefineryScreen.FACTORY);
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.COMPRESSOR_CONTAINER, CompressorScreen.FACTORY);
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.ELECTRIC_COMPRESSOR_CONTAINER, ElectricCompressorScreen.ELECTRIC_FACTORY);
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.ENERGY_STORAGE_MODULE_CONTAINER, EnergyStorageModuleScreen.FACTORY);
        ScreenProviderRegistry.INSTANCE.registerFactory(GalacticraftContainers.OXYGEN_COLLECTOR_CONTAINER, OxygenCollectorScreen.FACTORY);

        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.MOON_VILLAGER, (entityRenderDispatcher, context) -> new MoonVillagerRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_ZOMBIE, (entityRenderDispatcher, context) -> new EvolvedZombieRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.ROCKET_T1, (manager, context) -> new RocketEntityRenderer(manager));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_CREEPER, (entityRenderDispatcher, context) -> new EvolvedCreeperEntityRenderer(entityRenderDispatcher));

        GalacticraftBlockEntityRenderers.register();
        GalacticraftParticles.registerClient();

        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.WALKWAY, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.MOON_BERRY_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLOWSTONE_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLOWSTONE_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.UNLIT_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.UNLIT_WALL_TORCH, RenderLayer.getCutout());

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new FluidRenderingResourceReloadListener());

        Galacticraft.logger.info("[Galacticraft] Client initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
