/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import com.google.common.collect.ImmutableList;
import dev.galacticraft.mod.block.GalacticraftBlocks;
import dev.galacticraft.mod.client.gui.screen.ingame.*;
import dev.galacticraft.mod.client.model.GCGeneratedMachineModels;
import dev.galacticraft.mod.client.network.GalacticraftC2SPacketReceivers;
import dev.galacticraft.mod.client.render.MoonSkyProperties;
import dev.galacticraft.mod.client.render.block.entity.GalacticraftBlockEntityRenderers;
import dev.galacticraft.mod.client.render.entity.*;
import dev.galacticraft.mod.client.resource.GCResourceReloadListener;
import dev.galacticraft.mod.entity.GalacticraftEntityTypes;
import dev.galacticraft.mod.misc.capes.CapeLoader;
import dev.galacticraft.mod.misc.capes.JsonCapes;
import dev.galacticraft.mod.mixin.SkyPropertiesAccessor;
import dev.galacticraft.mod.particle.GalacticraftParticles;
import dev.galacticraft.mod.particle.fluid.DrippingCrudeOilParticle;
import dev.galacticraft.mod.particle.fluid.DrippingFuelParticle;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerTypes;
import com.mojang.datafixers.util.Pair;
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
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
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
            for (int i = 0; i < 8; i++) {
                registry.register(new Identifier(Constants.MOD_ID, "block/energy_storage_module_" + i));
                registry.register(new Identifier(Constants.MOD_ID, "block/oxygen_storage_module_" + i));
            }

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

        GalacticraftBlockEntityRenderers.register();
        GalacticraftC2SPacketReceivers.register();
        GCGeneratedMachineModels.registerDefaults();

        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.TIN_LADDER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLASS_FLUID_PIPE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.WALKWAY, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.WIRE_WALKWAY, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.PIPE_WALKWAY, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.MOON_BERRY_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLOWSTONE_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLOWSTONE_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.UNLIT_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.UNLIT_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.GLOWSTONE_LANTERN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.UNLIT_LANTERN, RenderLayer.getCutout());

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

        SkyPropertiesAccessor.getBY_IDENTIFIER().put(new Identifier(Constants.MOD_ID, "moon"), new MoonSkyProperties());

        Galacticraft.logger.info("[Galacticraft] Client initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
