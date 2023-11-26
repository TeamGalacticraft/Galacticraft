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

package dev.galacticraft.mod.client.model;

import com.google.gson.JsonObject;
import dev.galacticraft.machinelib.api.util.BlockFace;
import dev.galacticraft.machinelib.client.api.model.MachineModelRegistry;
import dev.galacticraft.machinelib.client.api.render.MachineRenderData;
import dev.galacticraft.machinelib.client.impl.model.MachineBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class OxygenSealerSpriteProvider implements MachineModelRegistry.SpriteProvider {
    private final TextureAtlasSprite left;
    private final TextureAtlasSprite right;
    private final TextureAtlasSprite top;
    private final TextureAtlasSprite machineSide;
    private final TextureAtlasSprite machine;

    public OxygenSealerSpriteProvider(JsonObject json, Function<net.minecraft.client.resources.model.Material, TextureAtlasSprite> function) {
        this.right = function.apply(new net.minecraft.client.resources.model.Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(GsonHelper.getAsString(json, "right"))));
        this.left = function.apply(new net.minecraft.client.resources.model.Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(GsonHelper.getAsString(json, "left"))));
        this.top = function.apply(new net.minecraft.client.resources.model.Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(GsonHelper.getAsString(json, "top"))));
        this.machine = function.apply(MachineBakedModel.MACHINE);
        this.machineSide = function.apply(MachineBakedModel.MACHINE_SIDE);
    }

    @Override
    public @NotNull TextureAtlasSprite getSpritesForState(@NotNull MachineRenderData renderData, @NotNull BlockFace face) {
        if (face == BlockFace.LEFT) return this.left;
        if (face == BlockFace.RIGHT) return this.right;
        if (face == BlockFace.TOP) return this.top;
        if (face.side()) return this.machineSide;
        return this.machine;
    }
}
