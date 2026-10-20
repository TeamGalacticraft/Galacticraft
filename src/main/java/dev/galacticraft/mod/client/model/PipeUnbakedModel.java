/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class PipeUnbakedModel implements UnbakedModel {
    private final ResourceLocation texture;
    private final float radius;
    /**
     * The model of the platform of a walkway attached to the pipe, or {@code null} if walkways can't be attached to it.
     */
    private final @Nullable ResourceLocation walkwayPlatform;
    /**
     * The texture of the end of the pipe under a walkway, or {@code null} to use its end cap.
     */
    private final @Nullable ResourceLocation walkwayEnd;

    public PipeUnbakedModel(ResourceLocation texture, float radius) {
        this(texture, radius, null, null);
    }

    public PipeUnbakedModel(ResourceLocation texture, float radius, @Nullable ResourceLocation walkwayPlatform, @Nullable ResourceLocation walkwayEnd) {
        this.texture = texture;
        this.radius = radius;
        this.walkwayPlatform = walkwayPlatform;
        this.walkwayEnd = walkwayEnd;
    }

    @Override
    public @NotNull Collection<ResourceLocation> getDependencies() {
        return this.walkwayPlatform == null ? Collections.emptyList() : List.of(this.walkwayPlatform);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> textureGetter, ModelState state) {
        PipeWalkwayModel walkway = this.walkwayPlatform == null ? null : new PipeWalkwayModel(baker, textureGetter, this.walkwayPlatform, this.texture, this.radius, this.walkwayEnd);
        return new PipeBakedModel(textureGetter, this.texture, this.radius, walkway);
    }
}