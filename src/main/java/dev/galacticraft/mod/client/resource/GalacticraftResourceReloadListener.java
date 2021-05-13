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

package dev.galacticraft.mod.client.resource;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.MachineBakedModel;
import dev.galacticraft.mod.client.render.entity.BubbleEntityRenderer;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class GalacticraftResourceReloadListener implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return new Identifier(Constant.MOD_ID, "resource_reload_listener");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return Arrays.asList(ResourceReloadListenerKeys.MODELS, ResourceReloadListenerKeys.TEXTURES);
    }

    @Override
    public void apply(ResourceManager resourceManager) {
        Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        MachineBakedModel.setSpriteAtlas(atlas);
        FluidRenderHandler oil = (view, pos, state) -> new Sprite[]{atlas.apply(new Identifier(Constant.MOD_ID, "block/crude_oil_still")), atlas.apply(new Identifier(Constant.MOD_ID, "block/crude_oil_flowing"))};
        FluidRenderHandler fuel = (view, pos, state) -> new Sprite[]{atlas.apply(new Identifier(Constant.MOD_ID, "block/fuel_still")), atlas.apply(new Identifier(Constant.MOD_ID, "block/fuel_flowing"))};
        FluidRenderHandler bacterialSludge = (view, pos, state) -> new Sprite[]{atlas.apply(new Identifier(Constant.MOD_ID, "block/bacterial_sludge_still")), atlas.apply(new Identifier(Constant.MOD_ID, "block/bacterial_sludge_flowing"))};
        FluidRenderHandler oxygen = (view, pos, state) -> new Sprite[]{atlas.apply(new Identifier(Constant.MOD_ID, "block/oxygen")), atlas.apply(new Identifier(Constant.MOD_ID, "block/oxygen"))};

        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluid.CRUDE_OIL, oil);
        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluid.FLOWING_CRUDE_OIL, oil);
        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluid.FUEL, fuel);
        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluid.FLOWING_FUEL, fuel);
        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluid.LIQUID_OXYGEN, oxygen);
        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluid.BACTERIAL_SLUDGE, bacterialSludge);
        FluidRenderHandlerRegistry.INSTANCE.register(GalacticraftFluid.FLOWING_BACTERIAL_SLUDGE, bacterialSludge);
        BubbleEntityRenderer.bubbleModel = null;
    }
}
