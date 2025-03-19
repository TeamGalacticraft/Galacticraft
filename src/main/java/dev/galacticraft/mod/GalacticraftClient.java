/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.mod.client.GCKeyBinds;
import dev.galacticraft.mod.client.gui.screen.ingame.*;
import dev.galacticraft.mod.client.model.GCModelLoader;
import dev.galacticraft.mod.client.model.GCRenderTypes;
import dev.galacticraft.mod.client.model.types.UnbakedObjModel;
import dev.galacticraft.mod.client.network.GCClientPacketReceiver;
import dev.galacticraft.mod.client.particle.*;
import dev.galacticraft.mod.client.render.block.entity.GCBlockEntityRenderer;
import dev.galacticraft.mod.client.render.dimension.GCDimensionEffects;
import dev.galacticraft.mod.client.render.entity.*;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.client.render.entity.rocket.RocketEntityRenderer;
import dev.galacticraft.mod.client.render.item.RocketItemRenderer;
import dev.galacticraft.mod.client.render.rocket.GalacticraftRocketPartRenderers;
import dev.galacticraft.mod.client.resources.GCResourceReloadListener;
import dev.galacticraft.mod.client.resources.RocketTextureManager;
import dev.galacticraft.mod.client.util.ColorUtil;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.block.environment.FallenMeteorBlock;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.events.ClientEventHandler;
import dev.galacticraft.mod.misc.cape.CapesLoader;
import dev.galacticraft.mod.network.c2s.OpenGcInventoryPayload;
import dev.galacticraft.mod.network.c2s.OpenRocketPayload;
import dev.galacticraft.mod.particle.GCParticleTypes;
import dev.galacticraft.mod.screen.GCMenuTypes;
import dev.galacticraft.mod.screen.GCPlayerInventoryMenu;
import dev.galacticraft.mod.screen.RocketMenu;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.material.Fluids;

@Environment(EnvType.CLIENT)
public class GalacticraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        long startInitTime = System.currentTimeMillis();
        Constant.LOGGER.info("Starting client initialization.");
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
        MenuScreens.register(GCMenuTypes.OXYGEN_BUBBLE_DISTRIBUTOR, OxygenBubbleDistributorScreen::new);
        MenuScreens.register(GCMenuTypes.OXYGEN_STORAGE_MODULE, OxygenStorageModuleScreen::new);
        MenuScreens.register(GCMenuTypes.OXYGEN_SEALER, OxygenSealerScreen::new);
        MenuScreens.register(GCMenuTypes.FUEL_LOADER, FuelLoaderScreen::new);
        MenuScreens.register(GCMenuTypes.AIRLOCK_CONTROLLER_MENU, AirlockControllerScreen::new);
        MenuScreens.register(GCMenuTypes.ROCKET_WORKBENCH, RocketWorkbenchScreen::new);
        MenuScreens.register(GCMenuTypes.ROCKET, RocketInventoryScreen::new);
        MenuScreens.register(GCMenuTypes.PARACHEST, ParachestScreen::new);

        EntityRendererRegistry.register(GCEntityTypes.MOON_VILLAGER, MoonVillagerRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_ZOMBIE, EvolvedZombieEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_CREEPER, EvolvedCreeperEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_SKELETON, EvolvedSkeletonEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_SPIDER, EvolvedSpiderEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_ENDERMAN, EvolvedEndermanEntityRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.EVOLVED_WITCH, EvolvedWitchEntityRenderer::new);
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
        EntityRendererRegistry.register(GCEntityTypes.BUGGY, BuggyRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.PARACHEST, ParachestRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.THROWABLE_METEOR_CHUNK, ThrownItemRenderer::new);
        EntityRendererRegistry.register(GCEntityTypes.SKELETON_BOSS, EvolvedSkeletonBossRenderer::new);

        GCBlockEntityRenderer.register();
        GCClientPacketReceiver.register();
        GCEntityModelLayer.register();
        GalacticraftRocketPartRenderers.register();
        GCKeyBinds.register();

        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.TIN_LADDER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.GLASS_FLUID_PIPE, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.WALKWAY, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.WIRE_WALKWAY, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.FLUID_PIPE_WALKWAY, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.IRON_GRATING, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.GLOWSTONE_TORCH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.GLOWSTONE_WALL_TORCH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.UNLIT_TORCH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.UNLIT_WALL_TORCH, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.GLOWSTONE_LANTERN, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.UNLIT_LANTERN, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.CAVERNOUS_VINES, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.CAVERNOUS_VINES_PLANT, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.OLIVINE_CLUSTER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(GCBlocks.MOON_CHEESE_LEAVES, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(), GCBlocks.VACUUM_GLASS, GCBlocks.CLEAR_VACUUM_GLASS, GCBlocks.STRONG_VACUUM_GLASS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(), GCBlocks.CRYOGENIC_CHAMBER, GCBlocks.CRYOGENIC_CHAMBER_PART, GCBlocks.PLAYER_TRANSPORT_TUBE);

        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.DRIPPING_CRUDE_OIL, DrippingCrudeOilProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.FALLING_CRUDE_OIL, FallingCrudeOilProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.DRIPPING_FUEL, DrippingFuelProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.FALLING_FUEL, FallingFuelProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.DRIPPING_SULFURIC_ACID, DrippingSulfuricAcidProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.FALLING_SULFURIC_ACID, FallingSulfuricAcidProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.CRYOGENIC_PARTICLE, CryoFreezeParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.LANDER_FLAME_PARTICLE, LanderParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.SPARK_PARTICLE, SparksParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.LAUNCH_SMOKE_PARTICLE, LaunchSmokeParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.LAUNCH_FLAME, LaunchFlameParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.LAUNCH_FLAME_LAUNCHED, LaunchFlameParticle.LaunchedProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.ACID_VAPOR_PARTICLE, AcidVaporParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticleTypes.SPLASH_VENUS, SplashParticle.Provider::new);

        FluidRenderHandlerRegistry.INSTANCE.get(Fluids.WATER); // Workaround for classloading order bug

        FluidRenderHandler oil = new SimpleFluidRenderHandler(
                Constant.Fluid.fluidId(Constant.Fluid.CRUDE_OIL_STILL),
                Constant.Fluid.fluidId(Constant.Fluid.CRUDE_OIL_FLOWING)
        );
        FluidRenderHandler fuel = new SimpleFluidRenderHandler(
                Constant.Fluid.fluidId(Constant.Fluid.FUEL_STILL),
                Constant.Fluid.fluidId(Constant.Fluid.FUEL_FLOWING)
        );
        FluidRenderHandler sulfuricAcid = new SimpleFluidRenderHandler(
                Constant.Fluid.fluidId(Constant.Fluid.SULFURIC_ACID_STILL),
                Constant.Fluid.fluidId(Constant.Fluid.SULFURIC_ACID_FLOWING)
        );

        FluidRenderHandlerRegistry.INSTANCE.register(GCFluids.CRUDE_OIL, GCFluids.FLOWING_CRUDE_OIL, oil);
        FluidRenderHandlerRegistry.INSTANCE.register(GCFluids.FUEL, GCFluids.FLOWING_FUEL, fuel);
        FluidRenderHandlerRegistry.INSTANCE.register(GCFluids.SULFURIC_ACID, GCFluids.FLOWING_SULFURIC_ACID, sulfuricAcid);

        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), GCFluids.FUEL, GCFluids.FLOWING_FUEL);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), GCFluids.SULFURIC_ACID, GCFluids.FLOWING_SULFURIC_ACID);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> FallenMeteorBlock.colorMultiplier(state, world, pos), GCBlocks.FALLEN_METEOR);
        ColorProviderRegistry.ITEM.register((stack, layer) -> layer != 1 ? -1 : ColorUtil.getRainbowOpaque(), GCItems.INFINITE_BATTERY, GCItems.INFINITE_OXYGEN_TANK);

        BuiltinItemRendererRegistry.INSTANCE.register(GCItems.ROCKET, new RocketItemRenderer());

        InventoryTabRegistry.INSTANCE.register(GCItems.OXYGEN_MASK.getDefaultInstance(), () -> ClientPlayNetworking.send(new OpenGcInventoryPayload()), GCPlayerInventoryMenu.class);
        InventoryTabRegistry.INSTANCE.register(GCItems.ROCKET.getDefaultInstance(), () -> ClientPlayNetworking.send(new OpenRocketPayload()), player -> player.getVehicle() instanceof RocketEntity, RocketMenu.class);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {

        });

        ModelLoadingPlugin.register(GCModelLoader.INSTANCE);

        GCRenderTypes.init();

        Constant.LOGGER.info("Client initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }

    /**
     * Called after GL render context has been created
     * should be safe to do general initialization here.
     */
    public static void init() {
        var helper = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
        helper.registerReloadListener(RocketTextureManager.INSTANCE);
        helper.registerReloadListener(GCModelLoader.INSTANCE);
        helper.registerReloadListener(GCResourceReloadListener.INSTANCE);

        GCModelLoader.registerModelType(UnbakedObjModel.TYPE);

        GCDimensionEffects.register();
    }
}
