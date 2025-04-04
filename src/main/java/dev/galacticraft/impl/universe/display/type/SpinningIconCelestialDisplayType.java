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

package dev.galacticraft.impl.universe.display.type;

import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector4f;

public class SpinningIconCelestialDisplayType extends IconCelestialDisplayType {
    public static final SpinningIconCelestialDisplayType INSTANCE = new SpinningIconCelestialDisplayType(IconCelestialDisplayConfig.CODEC);

    protected SpinningIconCelestialDisplayType(Codec<IconCelestialDisplayConfig> codec) {
        super(codec);
    }

    @Override
    public Vector4f render(GuiGraphics graphics, int size, double mouseX, double mouseY, float delta, IconCelestialDisplayConfig config) {
        float degrees = Minecraft.getInstance().level.getGameTime() * 66.666666666666F / 10.0F % 360.0f;
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(degrees));
        Vector4f render = super.render(graphics, size, mouseX, mouseY, delta, config);
        graphics.pose().mulPose(Axis.ZN.rotationDegrees(degrees)); // revert changes to the pose for position tracking
        return render;
    }
}
