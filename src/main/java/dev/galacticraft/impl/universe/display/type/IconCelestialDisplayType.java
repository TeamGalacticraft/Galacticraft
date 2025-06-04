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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.serialization.Codec;
import dev.galacticraft.api.universe.display.CelestialDisplayType;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.util.Graphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL32C;

public class IconCelestialDisplayType extends CelestialDisplayType<IconCelestialDisplayConfig> {
    public static final IconCelestialDisplayType INSTANCE = new IconCelestialDisplayType(IconCelestialDisplayConfig.CODEC);

    protected IconCelestialDisplayType(Codec<IconCelestialDisplayConfig> codec) {
        super(codec);
    }

    @Override
    public Vector4f render(GuiGraphics graphics, int size, double mouseX, double mouseY, float delta, IconCelestialDisplayConfig config) {
        float realSize = config.scale() * size;
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(config.texture());
        texture.bind();
        int width = GlStateManager._getTexLevelParameter(GL32C.GL_TEXTURE_2D, 0, GL32C.GL_TEXTURE_WIDTH);
        int height = GlStateManager._getTexLevelParameter(GL32C.GL_TEXTURE_2D, 0, GL32C.GL_TEXTURE_HEIGHT);
        Graphics.blitCentered(graphics.pose().last().pose(), 0.0f, 0.0f, realSize, realSize, 0.0f, config.u(), config.v(), config.width(), config.height(), width, height, config.texture());

        config.decoration().ifPresent(decoration -> {
            float decoSize = realSize / 3.0F;
            float decoW = decoration.xScale() * decoSize;
            float decoH = decoration.yScale() * decoSize;
            Graphics.blitCentered(graphics.pose().last().pose(), 0.0f, 0.0f, decoW, decoH, 0.0f, decoration.u(), decoration.v(), decoration.width(), decoration.height(), 32, 32, decoration.texture());
        });

        return new Vector4f(config.scale() * -size, config.scale() * -size, (config.scale() * size) * 2, (config.scale() * size) * 2);
    }

    @Override
    public ResourceLocation rocketOverlay(IconCelestialDisplayConfig config) {
        return config.rocketOverlay().orElse(Constant.CelestialOverlay.EARTH);
    }
}
