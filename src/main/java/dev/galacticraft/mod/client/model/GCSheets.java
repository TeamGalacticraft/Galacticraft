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

package dev.galacticraft.mod.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.galacticraft.mod.Constant;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

public class GCSheets {
    public static final ResourceLocation OBJ_ATLAS = Constant.id("textures/atlas/obj.png");

    public static final BiFunction<ResourceLocation, Boolean, RenderType> OBJ = Util.memoize(
            (texture, composite) -> {
                return RenderType.create("rocket", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RenderType.TRANSIENT_BUFFER_SIZE, true, false, RenderType.CompositeState.builder()
                        .setShaderState(RenderType.RENDERTYPE_ENTITY_CUTOUT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderType.NO_TRANSPARENCY)
                        .setCullState(RenderType.NO_CULL)
                        .setLightmapState(RenderType.LIGHTMAP)
                        .setOverlayState(RenderType.OVERLAY)
                        .createCompositeState(true));
            }
    );

    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(
            ((texture, composite) -> {
                RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                        .setShaderState(RenderType.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(RenderType.NO_CULL)
                        .setWriteMaskState(RenderType.COLOR_WRITE)
                        .setOverlayState(RenderType.OVERLAY)
                        .createCompositeState(composite);
                return RenderType.create("entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RenderType.TRANSIENT_BUFFER_SIZE, true, true, compositeState);
            })
    );

    public static RenderType obj(ResourceLocation texture) {
        return obj(texture, true);
    }

    public static RenderType obj(ResourceLocation texture, boolean outline) {
        return OBJ.apply(texture, outline);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation texture, boolean outline) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, outline);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation texture) {
        return entityTranslucentEmissive(texture, true);
    }
}
