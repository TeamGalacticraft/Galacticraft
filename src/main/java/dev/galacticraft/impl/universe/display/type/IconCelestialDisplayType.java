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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.Codec;
import dev.galacticraft.api.universe.display.CelestialDisplayType;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import dev.galacticraft.mod.client.gui.screen.ingame.CelestialSelectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL32C;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IconCelestialDisplayType extends CelestialDisplayType<IconCelestialDisplayConfig> {
    public static final IconCelestialDisplayType INSTANCE = new IconCelestialDisplayType(IconCelestialDisplayConfig.CODEC);

    protected IconCelestialDisplayType(Codec<IconCelestialDisplayConfig> codec) {
        super(codec);
    }

    @Override
    public Vector4f render(GuiGraphics graphics, BufferBuilder buffer, int size, double mouseX, double mouseY, float delta, Consumer<Supplier<ShaderInstance>> shaderSetter, IconCelestialDisplayConfig config) {
        shaderSetter.accept(GameRenderer::getPositionTexShader);
        Matrix4f positionMatrix = graphics.pose().last().pose();
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(config.texture());
        RenderSystem.setShaderTexture(0, texture.getId());
        texture.bind();
        float width = GlStateManager._getTexLevelParameter(GL32C.GL_TEXTURE_2D, 0, GL32C.GL_TEXTURE_WIDTH);
        float height = GlStateManager._getTexLevelParameter(GL32C.GL_TEXTURE_2D, 0, GL32C.GL_TEXTURE_HEIGHT);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(positionMatrix, (config.scale() * -size) / 2, (config.scale() * -size) / 2, 0).uv(config.u() / width, config.v() / height).endVertex();
        buffer.vertex(positionMatrix, (config.scale() * -size) / 2, (config.scale() * size) / 2, 0).uv(config.u() / width, (config.v() + config.height()) / height).endVertex();
        buffer.vertex(positionMatrix, (config.scale() * size) / 2, (config.scale() * size) / 2, 0).uv((config.u() + config.width()) / width, (config.v() + config.height()) / height).endVertex();
        buffer.vertex(positionMatrix, (config.scale() * size) / 2, (config.scale() * -size) / 2, 0).uv((config.u() + config.width()) / width, config.v() / height).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        config.decoration().ifPresent(decoration -> {
            RenderSystem.setShaderTexture(0, decoration.texture());
            float decoSize = size / 6.0F;
            CelestialSelectionScreen.blit(positionMatrix, decoration.xScale() * decoSize, decoration.yScale() * decoSize, decoration.widthScale() * decoSize, decoration.heightScale() * decoSize, decoration.u(), decoration.v(), decoration.width(), decoration.height(), 32, 32, 255, 255, 255, 255, false, false);
        });

        return new Vector4f(config.scale() * -size, config.scale() * -size, (config.scale() * size) * 2, (config.scale() * size) * 2);
    }
}
