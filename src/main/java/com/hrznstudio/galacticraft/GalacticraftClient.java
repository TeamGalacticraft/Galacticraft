/*
 * Copyright (c) 2019-2021 HRZN LTD
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

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.client.gui.screen.ingame.*;
import com.hrznstudio.galacticraft.client.model.GCGeneratedMachineModels;
import com.hrznstudio.galacticraft.client.network.GalacticraftC2SPacketReceivers;
import com.hrznstudio.galacticraft.client.render.MoonSkyProperties;
import com.hrznstudio.galacticraft.client.render.block.entity.GalacticraftBlockEntityRenderers;
import com.hrznstudio.galacticraft.client.render.entity.*;
import com.hrznstudio.galacticraft.client.resource.GCResourceReloadListener;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.client.render.entity.rocket.RocketEntityRenderer;
import com.hrznstudio.galacticraft.misc.capes.CapeLoader;
import com.hrznstudio.galacticraft.misc.capes.JsonCapes;
import com.hrznstudio.galacticraft.mixin.SkyPropertiesAccessor;
import com.hrznstudio.galacticraft.particle.GalacticraftParticles;
import com.hrznstudio.galacticraft.particle.fluid.DrippingCrudeOilParticle;
import com.hrznstudio.galacticraft.particle.fluid.DrippingFuelParticle;
import com.hrznstudio.galacticraft.screen.GalacticraftScreenHandlerTypes;
import com.mojang.datafixers.util.Pair;
import dev.onyxstudios.foml.obj.OBJLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

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

        //Particles because yes
        GalacticraftParticles.register();

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((spriteAtlasTexture, registry) -> {
            for (int i = 0; i < 8; i++) {
                registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_" + i));
                registry.register(new Identifier(Constants.MOD_ID, "block/oxygen_storage_module_" + i));
            }
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.RESEARCH_PANELS));
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.ROCKET_ASSEMBLER_SCREEN));
            registry.register(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.PLAYER_INVENTORY_SCREEN));
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

        ScreenRegistry.register(GalacticraftScreenHandlerTypes.BASIC_SOLAR_PANEL_HANDLER, BasicSolarPanelScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.ADVANCED_SOLAR_PANEL_HANDLER, AdvancedSolarPanelScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.COAL_GENERATOR_HANDLER, CoalGeneratorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.CIRCUIT_FABRICATOR_HANDLER, CircuitFabricatorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.REFINERY_HANDLER, RefineryScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.ELECTRIC_FURNACE_HANDLER, ElectricFurnaceScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.ELECTRIC_ARC_FURNACE_HANDLER, ElectricArcFurnaceScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.COMPRESSOR_HANDLER, CompressorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.ELECTRIC_COMPRESSOR_HANDLER, ElectricCompressorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.ENERGY_STORAGE_MODULE_HANDLER, EnergyStorageModuleScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.OXYGEN_COLLECTOR_HANDLER, OxygenCollectorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.OXYGEN_COMPRESSOR_HANDLER, OxygenCompressorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.OXYGEN_DECOMPRESSOR_HANDLER, OxygenDecompressorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.PLAYER_INV_GC_HANDLER, PlayerInventoryGCScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.ROCKET_DESIGNER_HANDLER, RocketDesignerScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.ROCKET_ASSEMBLER_HANDLER, RocketAssemblerScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.FUEL_LOADER_HANDLER, FuelLoaderScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.BUBBLE_DISTRIBUTOR_HANDLER, BubbleDistributorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.OXYGEN_STORAGE_MODULE_HANDLER, OxygenStorageModuleScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerTypes.OXYGEN_SEALER_HANDLER, OxygenSealerScreen::new);

        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.MOON_VILLAGER, (entityRenderDispatcher, context) -> new MoonVillagerEntityRenderer(entityRenderDispatcher, context.getResourceManager()));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_ZOMBIE, (entityRenderDispatcher, context) -> new EvolvedZombieRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_CREEPER, (entityRenderDispatcher, context) -> new EvolvedCreeperEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_SKELETON, (entityRenderDispatcher, context) -> new EvolvedSkeletonEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_SPIDER, (entityRenderDispatcher, context) -> new EvolvedSpiderEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_EVOKER, (entityRenderDispatcher, context) -> new EvolvedEvokerEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_PILLAGER, (entityRenderDispatcher, context) -> new EvolvedPillagerEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.EVOLVED_VINDICATOR, (entityRenderDispatcher, context) -> new EvolvedVindicatorEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.BUBBLE, (entityRenderDispatcher, context) -> new BubbleEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityTypes.ROCKET, (entityRenderDispatcher, context) -> new RocketEntityRenderer(entityRenderDispatcher));

        GalacticraftBlockEntityRenderers.register();
        GalacticraftC2SPacketReceivers.register();
        GCGeneratedMachineModels.registerDefaults();

        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.TIN_LADDER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLASS_FLUID_PIPE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.WALKWAY, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.MOON_BERRY_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLOWSTONE_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLOWSTONE_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.UNLIT_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.UNLIT_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLOWSTONE_LANTERN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.UNLIT_LANTERN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.ROCKET_LAUNCH_PAD, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.ROCKET_BODY_RENDER_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.ROCKET_CONE_BASIC_RENDER_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.ROCKET_CONE_ADVANCED_RENDER_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.ROCKET_CONE_SLOPED_RENDER_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.ROCKET_BOTTOM_RENDER_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.ROCKET_FINS_RENDER_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.ROCKET_BOOSTER_TIER_1_RENDER_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.ROCKET_BOOSTER_TIER_2_RENDER_BLOCK, RenderLayer.getTranslucent());


        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new GCResourceReloadListener());

        ParticleFactoryRegistry.getInstance().register(GalacticraftParticles.DRIPPING_FUEL_PARTICLE, (effect1, world1, x1, y1, z1, velX1, velY1, velZ1) -> new DrippingFuelParticle(world1, x1, y1, z1, velX1, velY1, velZ1));
        ParticleFactoryRegistry.getInstance().register(GalacticraftParticles.DRIPPING_CRUDE_OIL_PARTICLE, (effect, world, x, y, z, velX, velY, velZ) -> new DrippingCrudeOilParticle(world, x, y, z, velX, velY, velZ));

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> (resourceId, context) -> {
            if (resourceId.equals(GCGeneratedMachineModels.MACHINE_MARKER)) {
                return new UnbakedModel() {
                    @Override
                    public Collection<Identifier> getModelDependencies() {
                        return Collections.emptyList();
                    }

                    @Override
                    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
                        ImmutableList.Builder<SpriteIdentifier> builder = ImmutableList.builder();
                        builder.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine")));
                        builder.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_side")));
                        builder.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_power_input")));
                        builder.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_power_output")));
                        builder.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_oxygen_input")));
                        builder.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_oxygen_output")));
                        builder.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_fluid_input")));
                        builder.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_fluid_output")));
                        builder.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_item_input")));
                        builder.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_item_output")));
                        return builder.build();
                    }

                    @Override
                    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
                        Galacticraft.logger.info("Generating model for: {}", modelId);
                        return GCGeneratedMachineModels.INSTANCE;
                    }
                };
            }
            return null;
        });

        OBJLoader.INSTANCE.registerDomain(Constants.MOD_ID);
        SkyPropertiesAccessor.getBY_IDENTIFIER().put(new Identifier(Constants.MOD_ID, "moon"), new MoonSkyProperties());

        Galacticraft.logger.info("[Galacticraft] Client initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
