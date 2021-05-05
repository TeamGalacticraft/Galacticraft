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

import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.client.gui.screen.ingame.*;
import dev.galacticraft.mod.client.model.MachineBakedModel;
import dev.galacticraft.mod.client.model.MachineUnbakedModel;
import dev.galacticraft.mod.client.network.GalacticraftClientPacketReceiver;
import dev.galacticraft.mod.client.render.MoonSkyProperties;
import dev.galacticraft.mod.client.render.block.entity.GalacticraftBlockEntityRenderer;
import dev.galacticraft.mod.client.render.entity.*;
import dev.galacticraft.mod.client.render.entity.feature.gear.player.*;
import dev.galacticraft.mod.client.resource.GalacticraftResourceReloadListener;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.misc.cape.CapesLoader;
import dev.galacticraft.mod.mixin.SkyPropertiesAccessor;
import dev.galacticraft.mod.particle.GalacticraftParticle;
import dev.galacticraft.mod.particle.fluid.DrippingCrudeOilParticle;
import dev.galacticraft.mod.particle.fluid.DrippingFuelParticle;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

import java.util.Collections;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class GalacticraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        long startInitTime = System.currentTimeMillis();
        Galacticraft.LOGGER.info("Starting client initialization.");
        CapesLoader.load();

        // Render gear on player
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper) -> {
            if (entityRenderer instanceof PlayerEntityRenderer) {
                registrationHelper.register(new PlayerOxygenMaskFeatureRenderer<>((PlayerEntityRenderer) entityRenderer, 1.0F,
                        (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> stack.scale(0.825F, 0.825F, 0.825F)
                ));
                registrationHelper.register(new PlayerLeftOxygenTankFeatureRenderer<>((PlayerEntityRenderer) entityRenderer, 0.0F,
                        (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {}
                ));
                registrationHelper.register(new PlayerRightOxygenTankFeatureRenderer<>((PlayerEntityRenderer) entityRenderer, 0.0F,
                        (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {}
                ));
                registrationHelper.register(new PlayerSensorGlassesFeatureRenderer<>((PlayerEntityRenderer) entityRenderer, 1.0F,
                        (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> stack.scale(0.75F, 0.75F, 0.75F)
                ));
                registrationHelper.register(new PlayerFrequencyModuleFeatureRenderer<>((PlayerEntityRenderer) entityRenderer, 1.0F,
                        (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> stack.scale(0.6F, 0.6F, 0.6F)
                ));
            }
        });

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((spriteAtlasTexture, registry) -> {
            for (int i = 0; i <= 8; i++) {
                registry.register(new Identifier(Constant.MOD_ID, "block/energy_storage_module_" + i));
                registry.register(new Identifier(Constant.MOD_ID, "block/oxygen_storage_module_" + i));
            }

            registry.register(new Identifier(Constant.MOD_ID, "block/oxygen_sealer_top"));
            registry.register(new Identifier(Constant.MOD_ID, Constant.SlotSprite.THERMAL_HEAD));
            registry.register(new Identifier(Constant.MOD_ID, Constant.SlotSprite.THERMAL_CHEST));
            registry.register(new Identifier(Constant.MOD_ID, Constant.SlotSprite.THERMAL_PANTS));
            registry.register(new Identifier(Constant.MOD_ID, Constant.SlotSprite.THERMAL_BOOTS));

            registry.register(new Identifier(Constant.MOD_ID, Constant.SlotSprite.OXYGEN_MASK));
            registry.register(new Identifier(Constant.MOD_ID, Constant.SlotSprite.OXYGEN_GEAR));
            registry.register(new Identifier(Constant.MOD_ID, Constant.SlotSprite.OXYGEN_TANK));

            registry.register(Constant.Fluid.getIdentifier(Constant.Fluid.CRUDE_OIL_STILL));
            registry.register(Constant.Fluid.getIdentifier(Constant.Fluid.CRUDE_OIL_FLOWING));
            registry.register(Constant.Fluid.getIdentifier(Constant.Fluid.FUEL_STILL));
            registry.register(Constant.Fluid.getIdentifier(Constant.Fluid.FUEL_FLOWING));
        });

        ScreenRegistry.register(GalacticraftScreenHandlerType.BASIC_SOLAR_PANEL_HANDLER, BasicSolarPanelScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.ADVANCED_SOLAR_PANEL_HANDLER, AdvancedSolarPanelScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.COAL_GENERATOR_HANDLER, CoalGeneratorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.CIRCUIT_FABRICATOR_HANDLER, CircuitFabricatorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.REFINERY_HANDLER, RefineryScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.ELECTRIC_FURNACE_HANDLER, ElectricFurnaceScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.ELECTRIC_ARC_FURNACE_HANDLER, ElectricArcFurnaceScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.COMPRESSOR_HANDLER, CompressorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.ELECTRIC_COMPRESSOR_HANDLER, ElectricCompressorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.ENERGY_STORAGE_MODULE_HANDLER, EnergyStorageModuleScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.OXYGEN_COLLECTOR_HANDLER, OxygenCollectorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.OXYGEN_COMPRESSOR_HANDLER, OxygenCompressorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.OXYGEN_DECOMPRESSOR_HANDLER, OxygenDecompressorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.PLAYER_INV_GC_HANDLER, GalacticraftPlayerInventoryScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.BUBBLE_DISTRIBUTOR_HANDLER, BubbleDistributorScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.OXYGEN_STORAGE_MODULE_HANDLER, OxygenStorageModuleScreen::new);
        ScreenRegistry.register(GalacticraftScreenHandlerType.OXYGEN_SEALER_HANDLER, OxygenSealerScreen::new);

        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityType.MOON_VILLAGER, (dispatcher, context) -> new MoonVillagerEntityRenderer(dispatcher, context.getResourceManager()));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityType.EVOLVED_ZOMBIE, (dispatcher, context) -> new EvolvedZombieRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityType.EVOLVED_CREEPER, (dispatcher, context) -> new EvolvedCreeperEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityType.EVOLVED_SKELETON, (dispatcher, context) -> new EvolvedSkeletonEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityType.EVOLVED_SPIDER, (dispatcher, context) -> new EvolvedSpiderEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityType.EVOLVED_EVOKER, (dispatcher, context) -> new EvolvedEvokerEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityType.EVOLVED_PILLAGER, (dispatcher, context) -> new EvolvedPillagerEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityType.EVOLVED_VINDICATOR, (dispatcher, context) -> new EvolvedVindicatorEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(GalacticraftEntityType.BUBBLE, (dispatcher, context) -> new BubbleEntityRenderer(dispatcher));

        GalacticraftBlockEntityRenderer.register();
        GalacticraftClientPacketReceiver.register();
        MachineBakedModel.registerDefaults();

        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.TIN_LADDER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.GLASS_FLUID_PIPE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.WALKWAY, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.WIRE_WALKWAY, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.PIPE_WALKWAY, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.MOON_BERRY_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.GLOWSTONE_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.GLOWSTONE_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.UNLIT_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.UNLIT_WALL_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.GLOWSTONE_LANTERN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlock.UNLIT_LANTERN, RenderLayer.getCutout());

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new GalacticraftResourceReloadListener());

        ParticleFactoryRegistry.getInstance().register(GalacticraftParticle.DRIPPING_FUEL_PARTICLE, (type, world, x, y, z, velX, velY, velZ) -> new DrippingFuelParticle(world, x, y, z, velX, velY, velZ));
        ParticleFactoryRegistry.getInstance().register(GalacticraftParticle.DRIPPING_CRUDE_OIL_PARTICLE, (type, world, x, y, z, velX, velY, velZ) -> new DrippingCrudeOilParticle(world, x, y, z, velX, velY, velZ));

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> (resourceId, context) -> {
            if (MachineBakedModel.MACHINE_MARKER.equals(resourceId)) {
                return MachineUnbakedModel.INSTANCE;
            }
            return null;
        });

        ModelLoadingRegistry.INSTANCE.registerVariantProvider(resourceManager -> (modelId, context) -> {
            if (modelId.getVariant().equals("inventory") && MachineBakedModel.IDENTIFIERS.getOrDefault(modelId.getNamespace(), Collections.emptySet()).contains(modelId.getPath())) {
                return MachineUnbakedModel.INSTANCE;
            }
            return null;
        });

        SkyPropertiesAccessor.getBY_IDENTIFIER().put(new Identifier(Constant.MOD_ID, "moon"), new MoonSkyProperties());

        Galacticraft.LOGGER.info(String.format("Client initialization complete. (Took %d ms.)", System.currentTimeMillis() - startInitTime));
    }
}
