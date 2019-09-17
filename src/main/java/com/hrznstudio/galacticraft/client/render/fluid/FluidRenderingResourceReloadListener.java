package com.hrznstudio.galacticraft.client.render.fluid;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

import java.util.Arrays;
import java.util.Collection;

public class FluidRenderingResourceReloadListener implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return new Identifier(Constants.MOD_ID, "fluid_reload_listener");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return Arrays.asList(ResourceReloadListenerKeys.MODELS, ResourceReloadListenerKeys.TEXTURES);
    }

    @Override
    public void apply(ResourceManager var1) {
        //noinspection Convert2Lambda
        FluidRenderHandler oilRenderHandler = new FluidRenderHandler() {
            @Override
            public Sprite[] getFluidSprites(ExtendedBlockView view, BlockPos pos, FluidState state) {
                return new Sprite[]{ MinecraftClient.getInstance().getSpriteAtlas().getSprite(new Identifier(Constants.MOD_ID, "block/crude_oil_still")), MinecraftClient.getInstance().getSpriteAtlas().getSprite(new Identifier(Constants.MOD_ID, "block/crude_oil_flowing")) };
            }
        };

        FluidRenderHandler fuelRenderHandler = new FluidRenderHandler() {
            @Override
            public Sprite[] getFluidSprites(ExtendedBlockView view, BlockPos pos, FluidState state) {
                return new Sprite[]{MinecraftClient.getInstance().getSpriteAtlas().getSprite(new Identifier(Constants.MOD_ID, "block/fuel_still")), MinecraftClient.getInstance().getSpriteAtlas().getSprite(new Identifier(Constants.MOD_ID, "block/fuel_flowing")) };
            }

            @Override
            public int getFluidColor(ExtendedBlockView view, BlockPos pos, FluidState state) {
                return 15117351;
            }
        };

        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluids.CRUDE_OIL, oilRenderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluids.FLOWING_CRUDE_OIL, oilRenderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluids.FUEL, fuelRenderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluids.FLOWING_FUEL, fuelRenderHandler);
    }
}
