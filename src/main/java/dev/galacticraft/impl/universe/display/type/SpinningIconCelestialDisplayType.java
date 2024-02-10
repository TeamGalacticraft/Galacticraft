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
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Vector4f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpinningIconCelestialDisplayType extends IconCelestialDisplayType {
    public static final SpinningIconCelestialDisplayType INSTANCE = new SpinningIconCelestialDisplayType(IconCelestialDisplayConfig.CODEC);
    protected SpinningIconCelestialDisplayType(Codec<IconCelestialDisplayConfig> codec) {
        super(codec);
    }

    @Override
    public Vector4f render(GuiGraphics graphics, BufferBuilder buffer, int size, double mouseX, double mouseY, float delta, Consumer<Supplier<ShaderInstance>> shaderSetter, IconCelestialDisplayConfig config) {
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(Minecraft.getInstance().level.getGameTime() * 66.666666666666F / 10.0F % 360));
        return super.render(graphics, buffer, size, mouseX, mouseY, delta, shaderSetter, config);
    }
}
