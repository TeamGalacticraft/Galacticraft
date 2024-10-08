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

package dev.galacticraft.mod.client.model.sprite;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.machinelib.api.machine.MachineRenderData;
import dev.galacticraft.machinelib.api.util.BlockFace;
import dev.galacticraft.machinelib.client.api.model.sprite.TextureProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record SolarPanelTextureProvider(Material front, Material top, Material base, Material side) implements TextureProvider<SolarPanelTextureProvider.Bound> {
    public static final Codec<SolarPanelTextureProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MATERIAL_CODEC.fieldOf("front").forGetter(SolarPanelTextureProvider::front),
            MATERIAL_CODEC.fieldOf("top").forGetter(SolarPanelTextureProvider::top),
            MATERIAL_CODEC.fieldOf("base").forGetter(SolarPanelTextureProvider::base),
            MATERIAL_CODEC.fieldOf("side").forGetter(SolarPanelTextureProvider::side)
    ).apply(instance, SolarPanelTextureProvider::new));

    @Override
    public SolarPanelTextureProvider.Bound bind(Function<Material, TextureAtlasSprite> atlas) {
        return new SolarPanelTextureProvider.Bound(atlas.apply(this.front), atlas.apply(this.top), atlas.apply(this.base), atlas.apply(this.side));
    }

    public record Bound(TextureAtlasSprite front, TextureAtlasSprite top, TextureAtlasSprite base, TextureAtlasSprite side) implements BoundTextureProvider {
        @Override
        public TextureAtlasSprite getSprite(@Nullable MachineRenderData renderData, @NotNull BlockFace face) {
            if (face == BlockFace.FRONT) return this.front;
            if (face == BlockFace.TOP) return this.top;
            if (face.side()) return this.side;
            return this.base;
        }

        @Override
        public TextureAtlasSprite getParticle() {
            return this.base;
        }
    }
}
