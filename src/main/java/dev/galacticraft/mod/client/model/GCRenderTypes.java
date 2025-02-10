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

package dev.galacticraft.mod.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class GCRenderTypes {
    public static final ResourceLocation OBJ_ATLAS = Constant.id("textures/atlas/obj.png");

    @Nullable
    private static ShaderInstance rendertypeBubbleShader;

    public static final RenderStateShard.ShaderStateShard BUBBLE_SHADER = new RenderStateShard.ShaderStateShard(
            GCRenderTypes::getRendertypeBubbleShader
    );

    @Nullable
    public static ShaderInstance getRendertypeBubbleShader() {
        return rendertypeBubbleShader;
    }

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

    private static final BiFunction<ResourceLocation, Boolean, RenderType> BUBBLE = Util.memoize(
            ((texture, composite) -> {
                RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                        .setShaderState(BUBBLE_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(RenderType.NO_CULL)
                        .setWriteMaskState(RenderType.COLOR_WRITE)
                        .setOverlayState(RenderType.OVERLAY)
                        .createCompositeState(composite);
                return RenderType.create("entity_bubble", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RenderType.TRANSIENT_BUFFER_SIZE, true, true, compositeState);
            })
    );

    public static RenderType obj(ResourceLocation texture) {
        return obj(texture, true);
    }

    public static RenderType obj(ResourceLocation texture, boolean outline) {
        return OBJ.apply(texture, outline);
    }

    public static RenderType bubble(ResourceLocation texture, boolean outline) {
        return BUBBLE.apply(texture, outline);
    }

    public static RenderType bubble(ResourceLocation texture) {
        return bubble(texture, true);
    }

    public static void init() {
        CoreShaderRegistrationCallback.EVENT.register(context -> {
            context.register(Constant.id("rendertype_bubble"), DefaultVertexFormat.NEW_ENTITY, shader -> rendertypeBubbleShader = shader);
        });
    }
}
