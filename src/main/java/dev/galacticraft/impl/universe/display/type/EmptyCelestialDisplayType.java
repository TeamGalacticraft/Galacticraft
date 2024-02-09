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

package dev.galacticraft.impl.universe.display.type;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.api.universe.display.CelestialDisplayType;
import dev.galacticraft.impl.universe.display.config.EmptyCelestialDisplayConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Vector4f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EmptyCelestialDisplayType extends CelestialDisplayType<EmptyCelestialDisplayConfig> {
    public static final EmptyCelestialDisplayType INSTANCE = new EmptyCelestialDisplayType();

    private EmptyCelestialDisplayType() {
        super(EmptyCelestialDisplayConfig.CODEC);
    }

    @Override
    public Vector4f render(GuiGraphics graphics, BufferBuilder buffer, int size, double mouseX, double mouseY, float delta, Consumer<Supplier<ShaderInstance>> shaderSetter, EmptyCelestialDisplayConfig config) {
        return NULL_VECTOR;
    }
}
