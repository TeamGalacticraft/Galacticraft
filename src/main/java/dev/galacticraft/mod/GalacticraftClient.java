/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.api.client.tabs.InventoryTabRegistry;
import dev.galacticraft.machinelib.client.api.model.MachineModelRegistry;
import dev.galacticraft.mod.client.GCKeyBinds;
import dev.galacticraft.mod.client.gui.overlay.CountdownOverlay;
import dev.galacticraft.mod.client.gui.overlay.LanderOverlay;
import dev.galacticraft.mod.client.gui.overlay.OxygenOverlay;
import dev.galacticraft.mod.client.gui.overlay.RocketOverlay;
import dev.galacticraft.mod.client.gui.screen.ingame.*;
import dev.galacticraft.mod.client.model.*;
import dev.galacticraft.mod.client.network.GCClientPacketReceiver;
import dev.galacticraft.mod.client.particle.CryoFreezeParticle;
import dev.galacticraft.mod.client.particle.DrippingFuelProvider;
import dev.galacticraft.mod.client.particle.DrippingOilProvider;
import dev.galacticraft.mod.client.particle.LanderParticle;
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
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.block.environment.FallenMeteorBlock;
import dev.galacticraft.mod.content.entity.RocketEntity;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.events.ClientEventHandler;
import dev.galacticraft.mod.misc.cape.CapesLoader;
import dev.galacticraft.mod.particle.GCParticleTypes;
import dev.galacticraft.mod.screen.GCMenuTypes;
import dev.galacticraft.mod.screen.GCPlayerInventoryMenu;
import dev.galacticraft.mod.screen.RocketMenu;
import dev.galacticraft.mod.world.dimension.GCDimensions;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
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

        MenuScreens.register(GCMenuTypes.BASIC_SOLAR_PANEL, BasicSolarPanelScreen::new);
        MenuScreens.register(GCMenuTypes.ADVANCED_SOLAR_PANEL, AdvancedSolarPanelScreen::new);
        MenuScreens.register(GCMenuTypes.COAL_GENERATOR, CoalGeneratorScreen::new);
        MenuScreens.register(GCMenuTypes.CIRCUIT_FABRICATOR, CircuitFabricatorScreen::new);
        MenuScreens.register(GCMenuTypes.REFINERY, RefineryScreen::new);
        MenuScreens.register(GCMenuTypes.ELECTRIC_FURNACE, ElectricFurnaceScreen::new);
        MenuScreens.register(GCMenuTypes.ELECTRIC_ARC_FURNACE, ElectricArcFurnaceScreen::new);
        MenuScreens.register(GCMenuTypes.COMPRESSOR, CompressorScreen::new);
        MenuScreens.register(GCMenuTypes.ELECTRIC_COMPRESSOR, ElectricCompressorScreen::new);
        MenuScreens.register(GCMenuTypes.ENERGY_STORAGE_MODULE, EnergyStorageModuleScreen::new);
        MenuScreens.register(GCMenuTypes.OXYGEN_COLLECTOR, OxygenCollectorScreen::new);
        MenuScreens.register(GCMenuTypes.OXYGEN_COMPRESSOR, OxygenCompressorScreen::new);
        MenuScreens.register(GCMenuTypes.OXYGEN_DECOMPRESSOR, OxygenDecompressorScreen::new);
        MenuScreens.register(GCMenuTypes.PLAYER_INV_GC, GCPlayerInventoryScreen::new);
        MenuScreens.register(GCMenuTypes.OXYGEN_BUBBLE_DISTRIBUTOR, BubbleDistributorScreen::new);
        MenuScreens.register(GCMenuTypes.OXYGEN_STORAGE_MODULE, OxygenStorageModuleScreen::new);
        MenuScreens.register(GCMenuTypes.OXYGEN_SEALER, OxygenSealerScreen::new);
        MenuScreens.register(GCMenuTypes.FUEL_LOADER, FuelLoaderScreen::new);
        MenuScreens.register(GCMenuTypes.AIRLOCK_CONTROLLER_MENU, AirlockControllerScreen::new);
        MenuScreens.register(GCMenuTypes.ROCKET_WORKBENCH, RocketWorkbenchScreen::new);
        MenuScreens.register(GCMenuTypes.ROCKET, RocketInventoryScreen::new);

        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_ZOMBIE, EvolvedZombieRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_CREEPER, EvolvedCreeperEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_SKELETON, EvolvedSkeletonEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_SPIDER, EvolvedSpiderEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_EVOKER, EvolvedEvokerEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_PILLAGER, EvolvedPillagerEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_VINDICATOR, EvolvedVindicatorEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.GAZER, GazerEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.RUMBLER, RumblerEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.COMET_CUBE, CometCubeEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.OLI_GRUB, OliGrubEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.GREY, GreyEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.ARCH_GREY, GreyEntityRenderer::arch);
        EntityRendererRegistry.register(GCEntityTypes.BUBBLE, BubbleEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.ROCKET, RocketEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.LANDER, LanderEntityRenderer::new);

        GCBlockEntityRenderer.register();
        GCClientPacketReceiver.register();
        GCEntityModelLayer.register();
        GalacticraftRocketPartRenderers.register();
        GalacticraftRocketPartRenderers.registerModelLoader();
        GCKeyBinds.register();

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
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(), GCBlocks.CRYOGENIC_CHAMBER, GCBlocks.CRYOGENIC_CHAMBER_PART, GCBlocks.PLAYER_TRANSPORT_TUBE);

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new GCResourceReloadListener());

        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.DRIPPING_FUEL_PARTICLE, DrippingFuelProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.DRIPPING_CRUDE_OIL_PARTICLE, DrippingOilProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.CRYOGENIC_PARTICLE, CryoFreezeParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.LANDER_FLAME_PARTICLE, LanderParticle.Provider::new);

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

        DimensionRenderingRegistry.registerDimensionEffects(GCDimensions.MOON.location(), MoonDimensionEffects.INSTANCE);
        DimensionRenderingRegistry.registerCloudRenderer(GCDimensions.MOON, EmptyCloudRenderer.INSTANCE);
        DimensionRenderingRegistry.registerWeatherRenderer(GCDimensions.MOON, EmptyWeatherRenderer.INSTANCE);
        DimensionRenderingRegistry.registerSkyRenderer(GCDimensions.MOON, MoonSkyRenderer.INSTANCE);

        FluidRenderHandlerRegistry.INSTANCE.get(Fluids.WATER); // Workaround for classloading order bug

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> FallenMeteorBlock.colorMultiplier(state, world, pos), GCBlocks.FALLEN_METEOR);
        BuiltinItemRendererRegistry.INSTANCE.register(GCItems.ROCKET, new RocketItemRenderer());

        HudRenderCallback.EVENT.register(OxygenOverlay::onHudRender);
        HudRenderCallback.EVENT.register(RocketOverlay::onHudRender);
        HudRenderCallback.EVENT.register(LanderOverlay::onRenderHud);
        HudRenderCallback.EVENT.register(CountdownOverlay::renderCountdown);
        ClientTickEvents.END_CLIENT_TICK.register(LanderOverlay::clientTick);

        InventoryTabRegistry.INSTANCE.register(GCItems.OXYGEN_MASK.getDefaultInstance(), () -> {
            ClientPlayNetworking.send(Constant.Packet.OPEN_GC_INVENTORY, new FriendlyByteBuf(Unpooled.buffer(0)));
        }, GCPlayerInventoryMenu.class);
        InventoryTabRegistry.INSTANCE.register(GCItems.ROCKET.getDefaultInstance(), () -> {
            ClientPlayNetworking.send(Constant.Packet.OPEN_GC_ROCKET, new FriendlyByteBuf(Unpooled.buffer(0)));
        }, player -> player.getVehicle() instanceof RocketEntity, RocketMenu.class);

        Galacticraft.LOGGER.info("Client initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
