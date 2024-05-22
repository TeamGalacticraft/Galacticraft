/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.client.resources;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.*;
import dev.galacticraft.mod.client.render.entity.BubbleEntityRenderer;
import dev.galacticraft.mod.content.GCFluids;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.Arrays;
import java.util.Collection;

@Environment(EnvType.CLIENT)
public class GCResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    public static final GCResourceReloadListener INSTANCE = new GCResourceReloadListener();
    public static final ResourceLocation ID = Constant.id("resource_reload_listener");

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public Collection<ResourceLocation> getFabricDependencies() {
        return Arrays.asList(ResourceReloadListenerKeys.MODELS, ResourceReloadListenerKeys.TEXTURES);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        var atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        WireBakedModel.invalidate();
        WalkwayBakedModel.invalidate();
        WireWalkwayBakedModel.invalidate();
        PipeBakedModel.invalidate();
        FluidPipeWalkwayBakedModel.invalidate();
        FluidRenderHandler oil = (view, pos, state) -> new TextureAtlasSprite[]{atlas.apply(Constant.id("block/crude_oil_still")), atlas.apply(Constant.id("block/crude_oil_flowing"))};
        FluidRenderHandler fuel = (view, pos, state) -> new TextureAtlasSprite[]{atlas.apply(Constant.id("block/fuel_still")), atlas.apply(Constant.id("block/fuel_flowing"))};
        FluidRenderHandler sulfuricAcid = (view, pos, state) -> new TextureAtlasSprite[]{
                atlas.apply(Constant.id("block/fluid/sulfuric_acid_still")),
                atlas.apply(Constant.id("block/fluid/sulfuric_acid_flowing"))
        };
        FluidRenderHandler oxygen = (view, pos, state) -> new TextureAtlasSprite[]{atlas.apply(Constant.id("block/oxygen")), atlas.apply(Constant.id("block/oxygen"))};

        FluidRenderHandlerRegistry.INSTANCE.register(GCFluids.CRUDE_OIL, oil);
        FluidRenderHandlerRegistry.INSTANCE.register(GCFluids.FLOWING_CRUDE_OIL, oil);
        FluidRenderHandlerRegistry.INSTANCE.register(GCFluids.FUEL, fuel);
        FluidRenderHandlerRegistry.INSTANCE.register(GCFluids.FLOWING_FUEL, fuel);
        FluidRenderHandlerRegistry.INSTANCE.register(GCFluids.SULFURIC_ACID, sulfuricAcid);
        FluidRenderHandlerRegistry.INSTANCE.register(GCFluids.FLOWING_SULFURIC_ACID, sulfuricAcid);
        FluidRenderHandlerRegistry.INSTANCE.register(GCFluids.LIQUID_OXYGEN, oxygen);
        BubbleEntityRenderer.bubbleModel = null;
    }
}