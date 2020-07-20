/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.client.gui.screen.ingame.*;
import com.hrznstudio.galacticraft.client.network.GalacticraftClientPackets;
import com.hrznstudio.galacticraft.client.render.MoonSkyProperties;
import com.hrznstudio.galacticraft.client.render.block.entity.GalacticraftBlockEntityRenderers;
import com.hrznstudio.galacticraft.client.render.entity.EvolvedCreeperEntityRenderer;
import com.hrznstudio.galacticraft.client.render.entity.EvolvedZombieRenderer;
import com.hrznstudio.galacticraft.client.render.entity.MoonVillagerRenderer;
import com.hrznstudio.galacticraft.client.resource.FluidRenderingResourceReloadListener;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.misc.capes.CapeLoader;
import com.hrznstudio.galacticraft.misc.capes.JsonCapes;
import com.hrznstudio.galacticraft.particle.GalacticraftParticles;
import com.hrznstudio.galacticraft.screen.GalacticraftScreenHandlerTypes;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
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

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((spriteAtlasTexture, registry) -> {
//        ClientSpriteRegistryCallback.EVENT.register((spriteAtlasTexture, registry) -> {
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.COAL_GENERATOR_SCREEN));
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.SOLAR_PANEL_SCREEN));
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

            //FOR CONFIG. SIDES W/O JSON

            registry.register(new Identifier(Constants.MOD_ID, "block/machine"));
            registry.register(new Identifier(Constants.MOD_ID, "block/machine_side"));
            registry.register(new Identifier(Constants.MOD_ID, "block/machine_power_input"));
            registry.register(new Identifier(Constants.MOD_ID, "block/machine_power_output"));
            registry.register(new Identifier(Constants.MOD_ID, "block/machine_oxygen_input"));
            registry.register(new Identifier(Constants.MOD_ID, "block/machine_oxygen_output"));
            registry.register(new Identifier(Constants.MOD_ID, "block/machine_fluid_input"));
            registry.register(new Identifier(Constants.MOD_ID, "block/machine_fluid_output"));

            registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_0"));
            registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_1"));
            registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_2"));
            registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_3"));
            registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_4"));
            registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_5"));
            registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_6"));
            registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_7"));
            registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_8"));

            registry.register(new Identifier(Constants.MOD_ID, "block/electric_compressor"));
            registry.register(new Identifier(Constants.MOD_ID, "block/electric_compressor_off"));
            registry.register(new Identifier(Constants.MOD_ID, "block/electric_compressor_on"));

            registry.register(new Identifier(Constants.MOD_ID, "block/oxygen_collector"));

            registry.register(new Identifier(Constants.MOD_ID, "block/refinery_front"));
            registry.register(new Identifier(Constants.MOD_ID, "block/refinery_side"));
            registry.register(new Identifier(Constants.MOD_ID, "block/refinery_top"));

            registry.register(new Identifier(Constants.MOD_ID, "block/basic_solar_panel"));
            registry.register(new Identifier(Constants.MOD_ID, "block/advanced_solar_panel"));
            registry.register(new Identifier(Constants.MOD_ID, "block/solar_panel"));

            registry.register(new Identifier(Constants.MOD_ID, "block/coal_generator"));
            registry.register(new Identifier(Constants.MOD_ID, "block/compressor"));

            registry.register(new Identifier(Constants.MOD_ID, "block/empty"));
        });

        ScreenRegistry.register(GalacticraftScreenHandlerTypes.BASIC_SOLAR_PANEL_HANDLER, BasicSolarPanelScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.ADVANCED_SOLAR_PANEL_HANDLER, AdvancedSolarPanelScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.COAL_GENERATOR_HANDLER, CoalGeneratorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.CIRCUIT_FABRICATOR_HANDLER, CircuitFabricatorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.REFINERY_HANDLER, RefineryScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.COMPRESSOR_HANDLER, CompressorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.ELECTRIC_COMPRESSOR_HANDLER, ElectricCompressorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.ENERGY_STORAGE_MODULE_HANDLER, EnergyStorageModuleScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.OXYGEN_COLLECTOR_HANDLER, OxygenCollectorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.PLAYER_INV_GC_HANDLER, PlayerInventoryGCScreen::new);

        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.MOON_VILLAGER, (entityRenderDispatcher, context) -> new MoonVillagerRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_ZOMBIE, (entityRenderDispatcher, context) -> new EvolvedZombieRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_CREEPER, (entityRenderDispatcher, context) -> new EvolvedCreeperEntityRenderer(entityRenderDispatcher));

        GalacticraftBlockEntityRenderers.register();
        GalacticraftParticles.registerClient();
        GalacticraftClientPackets.register();

        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.FLUID_PIPE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.WALKWAY, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.MOON_BERRY_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLOWSTONE_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLOWSTONE_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.UNLIT_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.UNLIT_WALL_TORCH, RenderLayer.getCutout());

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new FluidRenderingResourceReloadListener());

        SkyProperties.BY_DIMENSION_TYPE.put(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier(Constants.MOD_ID, "moon")), new MoonSkyProperties());

        Galacticraft.logger.info("[Galacticraft] Client initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
