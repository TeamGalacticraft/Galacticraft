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
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.configuration.face.BlockFace;
import dev.galacticraft.machinelib.client.api.model.MachineModelRegistry;
import dev.galacticraft.machinelib.client.impl.model.MachineBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

public class SolarPanelSpriteProvider implements MachineModelRegistry.SpriteProvider {
    private TextureAtlasSprite front;
    private TextureAtlasSprite top;
    private TextureAtlasSprite machineSide;
    private TextureAtlasSprite machine;

    public SolarPanelSpriteProvider(JsonObject json, Function<Material, TextureAtlasSprite> function) {
        this.front = function.apply(new net.minecraft.client.resources.model.Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(GsonHelper.getAsString(json, "front"))));
        this.top = function.apply(new net.minecraft.client.resources.model.Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(GsonHelper.getAsString(json, "top"))));
        this.machine = function.apply(MachineBakedModel.MACHINE);
        this.machineSide = function.apply(MachineBakedModel.MACHINE_SIDE);
    }

    @Override
    public @NotNull TextureAtlasSprite getSpritesForState(@Nullable MachineBlockEntity machine, @Nullable ItemStack stack, @NotNull BlockFace face) {
        if (face == BlockFace.FRONT) return this.front;
        if (face == BlockFace.TOP) return this.top;
        if (face.side()) return this.machineSide;
        return this.machine;
    }
}
