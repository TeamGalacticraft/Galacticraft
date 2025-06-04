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

package dev.galacticraft.api.universe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4f;

public abstract class CelestialDisplayType<C extends CelestialDisplayConfig> {
    public static final Vector4f NULL_VECTOR = new Vector4f(0, 0, 0, 0);
    private final MapCodec<CelestialDisplay<C, CelestialDisplayType<C>>> codec;

    public CelestialDisplayType(Codec<C> codec) {
        this.codec = codec.fieldOf("config").xmap((config) -> new CelestialDisplay<>(this, config), CelestialDisplay::config);
    }

    @Environment(EnvType.CLIENT)
    public abstract Vector4f render(GuiGraphics graphics, int size, double mouseX, double mouseY, float delta, C config);

    /**
     * Returns the texture used in the take-off gui on the side of the screen see {@link dev.galacticraft.mod.client.gui.overlay.RocketOverlay}
     *
     * @return the texture used in the take-off gui on the side of the screen see {@link dev.galacticraft.mod.client.gui.overlay.RocketOverlay}
     */
    public abstract ResourceLocation rocketOverlay(C config);

    public MapCodec<CelestialDisplay<C, CelestialDisplayType<C>>> codec() {
        return this.codec;
    }

    public CelestialDisplay<C, CelestialDisplayType<C>> configure(C config) {
        return new CelestialDisplay<>(this, config);
    }
}
