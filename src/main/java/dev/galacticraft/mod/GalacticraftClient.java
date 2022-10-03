/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod;

import dev.galacticraft.api.client.model.MachineModelRegistry;
import dev.galacticraft.mod.block.GCBlocks;
import dev.galacticraft.mod.client.gui.overlay.CountdownOverylay;
import dev.galacticraft.mod.client.gui.overlay.RocketOverlay;
import dev.galacticraft.mod.client.gui.screen.ingame.*;
import dev.galacticraft.mod.client.model.*;
import dev.galacticraft.mod.client.network.GCClientPacketReceiver;
import dev.galacticraft.mod.client.particle.DrippingFuelFactory;
import dev.galacticraft.mod.client.particle.DrippingOilFactory;
import dev.galacticraft.mod.client.render.block.entity.GCBlockEntityRenderer;
import dev.galacticraft.mod.client.render.dimension.EmptyCloudRenderer;
import dev.galacticraft.mod.client.render.dimension.EmptyWeatherRenderer;
import dev.galacticraft.mod.client.render.dimension.MoonDimensionEffects;
import dev.galacticraft.mod.client.render.dimension.MoonSkyRenderer;
import dev.galacticraft.mod.client.render.entity.*;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.client.render.entity.rocket.RocketEntityRenderer;
import dev.galacticraft.mod.client.render.item.RocketItemRenderer;
import dev.galacticraft.mod.client.render.rocket.GalacticraftRocketPartRenderers;
import dev.galacticraft.mod.client.resource.GCResourceReloadListener;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.events.ClientEventHandler;
import dev.galacticraft.mod.item.GCItem;
import dev.galacticraft.mod.misc.cape.CapesLoader;
import dev.galacticraft.mod.particle.GCParticleType;
import dev.galacticraft.mod.screen.GCScreenHandlerType;
import dev.galacticraft.mod.world.dimension.GCDimensionType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class GalacticraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        long startInitTime = System.currentTimeMillis();
        Galacticraft.LOGGER.info("Starting client initialization.");
        ClientEventHandler.init();
        CapesLoader.load();

        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS).register((spriteAtlasTexture, registry) -> {
            for (int i = 0; i <= 8; i++) {
                registry.register(new ResourceLocation(Constant.MOD_ID, "block/energy_storage_module_" + i));
                registry.register(new ResourceLocation(Constant.MOD_ID, "block/oxygen_storage_module_" + i));
            }

            registry.register(new ResourceLocation(Constant.MOD_ID, "block/oxygen_sealer_top"));
            registry.register(new ResourceLocation(Constant.MOD_ID, Constant.SlotSprite.THERMAL_HEAD));
            registry.register(new ResourceLocation(Constant.MOD_ID, Constant.SlotSprite.THERMAL_CHEST));
            registry.register(new ResourceLocation(Constant.MOD_ID, Constant.SlotSprite.THERMAL_PANTS));
            registry.register(new ResourceLocation(Constant.MOD_ID, Constant.SlotSprite.THERMAL_BOOTS));

            registry.register(new ResourceLocation(Constant.MOD_ID, Constant.SlotSprite.OXYGEN_MASK));
            registry.register(new ResourceLocation(Constant.MOD_ID, Constant.SlotSprite.OXYGEN_GEAR));
            registry.register(new ResourceLocation(Constant.MOD_ID, Constant.SlotSprite.OXYGEN_TANK));

            registry.register(Constant.Fluid.getId(Constant.Fluid.CRUDE_OIL_STILL));
            registry.register(Constant.Fluid.getId(Constant.Fluid.CRUDE_OIL_FLOWING));
            registry.register(Constant.Fluid.getId(Constant.Fluid.FUEL_STILL));
            registry.register(Constant.Fluid.getId(Constant.Fluid.FUEL_FLOWING));

            registry.register(new ResourceLocation("galacticraft", "model/rocket"));
        });

        MenuScreens.register(GCScreenHandlerType.BASIC_SOLAR_PANEL_HANDLER, BasicSolarPanelScreen::new);
        MenuScreens.register(GCScreenHandlerType.ADVANCED_SOLAR_PANEL_HANDLER, AdvancedSolarPanelScreen::new);
        MenuScreens.register(GCScreenHandlerType.COAL_GENERATOR_HANDLER, CoalGeneratorScreen::new);
        MenuScreens.register(GCScreenHandlerType.CIRCUIT_FABRICATOR_HANDLER, CircuitFabricatorScreen::new);
        MenuScreens.register(GCScreenHandlerType.REFINERY_HANDLER, RefineryScreen::new);
        MenuScreens.register(GCScreenHandlerType.ELECTRIC_FURNACE_HANDLER, ElectricFurnaceScreen::new);
        MenuScreens.register(GCScreenHandlerType.ELECTRIC_ARC_FURNACE_HANDLER, ElectricArcFurnaceScreen::new);
        MenuScreens.register(GCScreenHandlerType.COMPRESSOR_HANDLER, CompressorScreen::new);
        MenuScreens.register(GCScreenHandlerType.ELECTRIC_COMPRESSOR_HANDLER, ElectricCompressorScreen::new);
        MenuScreens.register(GCScreenHandlerType.ENERGY_STORAGE_MODULE_HANDLER, EnergyStorageModuleScreen::new);
        MenuScreens.register(GCScreenHandlerType.OXYGEN_COLLECTOR_HANDLER, OxygenCollectorScreen::new);
        MenuScreens.register(GCScreenHandlerType.OXYGEN_COMPRESSOR_HANDLER, OxygenCompressorScreen::new);
        MenuScreens.register(GCScreenHandlerType.OXYGEN_DECOMPRESSOR_HANDLER, OxygenDecompressorScreen::new);
        MenuScreens.register(GCScreenHandlerType.PLAYER_INV_GC_HANDLER, GCPlayerInventoryScreen::new);
        MenuScreens.register(GCScreenHandlerType.BUBBLE_DISTRIBUTOR_HANDLER, BubbleDistributorScreen::new);
        MenuScreens.register(GCScreenHandlerType.OXYGEN_STORAGE_MODULE_HANDLER, OxygenStorageModuleScreen::new);
        MenuScreens.register(GCScreenHandlerType.OXYGEN_SEALER_HANDLER, OxygenSealerScreen::new);
        MenuScreens.register(GCScreenHandlerType.AIRLOCK_CONTROLLER_MENU, AirlockControllerScreen::new);

        EntityRendererRegistry.register(GalacticraftEntityType.EVOLVED_ZOMBIE, EvolvedZombieRenderer::new);
        EntityRendererRegistry.register(GalacticraftEntityType.EVOLVED_CREEPER, EvolvedCreeperEntityRenderer::new);
        EntityRendererRegistry.register(GalacticraftEntityType.EVOLVED_SKELETON, EvolvedSkeletonEntityRenderer::new);
        EntityRendererRegistry.register(GalacticraftEntityType.EVOLVED_SPIDER, EvolvedSpiderEntityRenderer::new);
        EntityRendererRegistry.register(GalacticraftEntityType.EVOLVED_EVOKER, EvolvedEvokerEntityRenderer::new);
        EntityRendererRegistry.register(GalacticraftEntityType.EVOLVED_PILLAGER, EvolvedPillagerEntityRenderer::new);
        EntityRendererRegistry.register(GalacticraftEntityType.EVOLVED_VINDICATOR, EvolvedVindicatorEntityRenderer::new);
        EntityRendererRegistry.register(GalacticraftEntityType.GAZER, GazerEntityRenderer::new);
        EntityRendererRegistry.register(GalacticraftEntityType.ARCH_GREY, ArchGreyEntityRenderer::new);
        EntityRendererRegistry.register(GalacticraftEntityType.BUBBLE, BubbleEntityRenderer::new);
        EntityRendererRegistry.register(GalacticraftEntityType.ROCKET, RocketEntityRenderer::new);

        GCBlockEntityRenderer.register();
        GCClientPacketReceiver.register();
        GCEntityModelLayer.register();
        GalacticraftRocketPartRenderers.register();
        GalacticraftRocketPartRenderers.registerModelLoader();

        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.TIN_LADDER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.GLASS_FLUID_PIPE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.WALKWAY, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.WIRE_WALKWAY, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.PIPE_WALKWAY, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.MOON_BERRY_BUSH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.GLOWSTONE_TORCH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.GLOWSTONE_WALL_TORCH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.UNLIT_TORCH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.UNLIT_WALL_TORCH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.GLOWSTONE_LANTERN, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.UNLIT_LANTERN, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.POISONOUS_CAVERNOUS_VINE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.CAVERNOUS_VINE, RenderType.cutout());

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new GCResourceReloadListener());

        ParticleFactoryRegistry.getInstance().register(GCParticleType.DRIPPING_FUEL_PARTICLE, DrippingFuelFactory::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleType.DRIPPING_CRUDE_OIL_PARTICLE, DrippingOilFactory::new);

        MachineModelRegistry.register(new ResourceLocation(Constant.MOD_ID, "solar_panel"), SolarPanelSpriteProvider::new);
        MachineModelRegistry.register(new ResourceLocation(Constant.MOD_ID, "oxygen_sealer"), OxygenSealerSpriteProvider::new);

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> (resourceId, context) -> {
            if (WireBakedModel.WIRE_MARKER.equals(resourceId)) {
                return WireUnbakedModel.INSTANCE;
            } else if (WalkwayBakedModel.WALKWAY_MARKER.equals(resourceId)) {
                return WalkwayUnbakedModel.INSTANCE;
            } else if (WireWalkwayBakedModel.WIRE_WALKWAY_MARKER.equals(resourceId)) {
                return WireWalkwayUnbakedModel.INSTANCE;
            } else if (PipeWalkwayBakedModel.PIPE_WALKWAY_MARKER.equals(resourceId)) {
                return PipeWalkwayUnbakedModel.INSTANCE;
            } else if (PipeBakedModel.GLASS_FLUID_PIPE_MARKER.equals(resourceId)) {
                return PipeUnbakedModel.INSTANCE;
            }
            return null;
        });

        DimensionRenderingRegistry.registerDimensionEffects(GCDimensionType.MOON_KEY.location(), MoonDimensionEffects.INSTANCE);
        DimensionRenderingRegistry.registerCloudRenderer(GCDimensionType.MOON_KEY, EmptyCloudRenderer.INSTANCE);
        DimensionRenderingRegistry.registerWeatherRenderer(GCDimensionType.MOON_KEY, EmptyWeatherRenderer.INSTANCE);
        DimensionRenderingRegistry.registerSkyRenderer(GCDimensionType.MOON_KEY, MoonSkyRenderer.INSTANCE);

        FluidRenderHandlerRegistry.INSTANCE.get(Fluids.WATER); // Workaround for classloading order bug

        BuiltinItemRendererRegistry.INSTANCE.register(GCItem.ROCKET, new RocketItemRenderer());

        HudRenderCallback.EVENT.register(RocketOverlay::onHudRender);
        HudRenderCallback.EVENT.register(CountdownOverylay::renderCountdown);

        Galacticraft.LOGGER.info("Client initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
