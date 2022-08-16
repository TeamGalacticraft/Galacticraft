/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import dev.galacticraft.api.block.entity.MachineBlockEntity;
import dev.galacticraft.api.block.util.BlockFace;
import dev.galacticraft.api.client.model.MachineModelRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

public class OxygenSealerSpriteProvider implements MachineModelRegistry.SpriteProvider {
    private ResourceLocation left;
    private ResourceLocation right;
    private ResourceLocation top;

    @Override
    public @NotNull TextureAtlasSprite getSpritesForState(@Nullable MachineBlockEntity machine, @Nullable ItemStack stack, @NotNull BlockFace face, @NotNull Function<ResourceLocation, TextureAtlasSprite> atlas) {
        if (face == BlockFace.LEFT) return atlas.apply(this.left);
        if (face == BlockFace.RIGHT) return atlas.apply(this.right);
        if (face == BlockFace.TOP) return atlas.apply(this.top);
        if (face.horizontal()) return atlas.apply(MachineModelRegistry.MACHINE_SIDE);
        return atlas.apply(MachineModelRegistry.MACHINE);
    }

    @Override
    public void fromJson(JsonObject jsonObject, Set<ResourceLocation> textureDependencies) {
        this.right = new ResourceLocation(GsonHelper.getAsString(jsonObject, "right"));
        this.left = new ResourceLocation(GsonHelper.getAsString(jsonObject, "left"));
        this.top = new ResourceLocation(GsonHelper.getAsString(jsonObject, "top"));
        textureDependencies.add(this.right);
        textureDependencies.add(this.left);
        textureDependencies.add(this.top);
    }
}
