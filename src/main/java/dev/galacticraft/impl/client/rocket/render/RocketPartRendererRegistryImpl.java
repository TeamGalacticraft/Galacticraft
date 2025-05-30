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

package dev.galacticraft.impl.client.rocket.render;

import dev.galacticraft.api.entity.rocket.render.RocketPartRenderer;
import dev.galacticraft.api.entity.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.part.RocketPart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RocketPartRendererRegistryImpl implements RocketPartRendererRegistry {
    private final Map<ResourceKey<? extends RocketPart<?, ?>>, RocketPartRenderer> renderers = new HashMap<>();

    public RocketPartRendererRegistryImpl() {
    }

    @Override
    public <T extends RocketPart<?, ?>> void register(@NotNull ResourceKey<T> id, @NotNull RocketPartRenderer renderer) {
        this.renderers.put(id, renderer);
    }

    @Override
    public <T extends RocketPart<?, ?>> @NotNull RocketPartRenderer getRenderer(ResourceKey<T> part) {
        return this.renderers.getOrDefault(part, EmptyRocketPartRenderer.INSTANCE);
    }
}
