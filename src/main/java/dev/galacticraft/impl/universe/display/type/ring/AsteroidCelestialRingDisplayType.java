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

package dev.galacticraft.impl.universe.display.type.ring;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.Codec;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplayType;
import dev.galacticraft.impl.universe.display.config.ring.DefaultCelestialRingDisplayConfig;
import dev.galacticraft.mod.client.gui.screen.ingame.CelestialSelectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsteroidCelestialRingDisplayType extends CelestialRingDisplayType<DefaultCelestialRingDisplayConfig> {
    public static final AsteroidCelestialRingDisplayType INSTANCE = new AsteroidCelestialRingDisplayType(DefaultCelestialRingDisplayConfig.CODEC);

    public AsteroidCelestialRingDisplayType(Codec<DefaultCelestialRingDisplayConfig> codec) {
        super(codec);
    }

    @Override
    public boolean render(CelestialBody<?, ?> body, GuiGraphics graphics, int count, Vector3f systemOffset, float lineScale, float alpha, double mouseX, double mouseY, float delta, Consumer<Supplier<ShaderInstance>> shaderSetter, DefaultCelestialRingDisplayConfig config) {
        shaderSetter.accept(GameRenderer::getRendertypeLinesShader);
        PoseStack matrices = graphics.pose();
        matrices.pushPose();
        Screen screen = Minecraft.getInstance().screen;
        float[] color = screen instanceof CelestialSelectionScreen ? new float[]{0.7F, 0.0F, 0.0F, alpha / 2.0F} : new float[]{0.3F, 0.1F, 0.1F, 1.0F};
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        final float theta = Mth.TWO_PI / 90;
        final float cos = Mth.cos(theta);
        final float sin = Mth.sin(theta);
        final float theta2 = Mth.TWO_PI / -90f;
        final float cos2 = Mth.cos(theta2);
        final float sin2 = Mth.sin(theta2);

        float min = 72.0F;
        float max = 78.0F;

        float x = max * body.position().lineScale();
        float y = 0;

        float temp;
        float x1 = x;
        float y1 = y;
        Matrix4f model = matrices.last().pose();
        for (int i = 0; i < 180; i++) {
            buffer.vertex(model, x, y, 0).color(color[0], color[1], color[2], color[3]);
            if (i < 90) {
                buffer.normal(1, 1, 1);
            } else {
                buffer.normal(1, -1, -1);
            }

            buffer.endVertex();

            temp = x;
            x = cos * x - sin * y;
            y = sin * temp + cos * y;
        }

        buffer.vertex(model, x1, y1, 0).color(color[0], color[1], color[2], color[3]).normal(1, 1, 1).endVertex(); //LINE_LOOP is gone
        x = x1;
        y = y1;

        for (int i = 0; i < 180; i++) {
            buffer.vertex(model, x, y, 0).color(color[0], color[1], color[2], color[3]);
            if (i < 90) {
                buffer.normal(1, 1, 1);
            } else {
                buffer.normal(1, -1, -1);
            }

            buffer.endVertex();

            temp = x;
            x = cos2 * x - sin2 * y;
            y = sin2 * temp + cos2 * y;
        }
        buffer.vertex(model, x1, y1, 0).color(color[0], color[1], color[2], color[3]).normal(1, 1, 1).endVertex(); //LINE_LOOP is gone

        tesselator.end();
        buffer.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        x = min * body.position().lineScale();
        y = 0;

        x1 = x;
        y1 = y;

        for (int i = 0; i < 180; i++) {
            buffer.vertex(model, x, y, 0).color(color[0], color[1], color[2], color[3]);
            if (i < 90) {
                buffer.normal(1, 1, 1);
            } else {
                buffer.normal(1, -1, -1);
            }

            buffer.endVertex();

            temp = x;
            x = cos * x - sin * y;
            y = sin * temp + cos * y;
        }

        buffer.vertex(model, x1, y1, 0).color(color[0], color[1], color[2], color[3]).normal(1, 1, 1).endVertex(); //LINE_LOOP is gone
        x = x1;
        y = y1;

        for (int i = 0; i < 180; i++) {
            buffer.vertex(model, x, y, 0).color(color[0], color[1], color[2], color[3]);
            if (i < 90) {
                buffer.normal(1, 1, 1);
            } else {
                buffer.normal(1, -1, -1);
            }

            buffer.endVertex();

            temp = x;
            x = cos2 * x - sin2 * y;
            y = sin2 * temp + cos2 * y;
        }
        buffer.vertex(model, x1, y1, 0).color(color[0], color[1], color[2], color[3]).normal(1, 1, 1).endVertex(); //LINE_LOOP is gone

        tesselator.end();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        x = min * body.position().lineScale();
        y = 0;
        float x2 = max * body.position().lineScale();
        float y2 = 0;

        for (int i = 0; i < 90; i++) {
            buffer.vertex(model, x2, y2, 0).color(0.7F, 0.0F, 0.0F, alpha / 10.0F).endVertex();
            buffer.vertex(model, x, y, 0).color(0.7F, 0.0F, 0.0F, alpha / 10.0F).endVertex();

            temp = x;
            x = cos * x - sin * y;
            y = sin * temp + cos * y;
            temp = x2;
            x2 = cos * x2 - sin * y2;
            y2 = sin * temp + cos * y2;

            buffer.vertex(model, x, y, 0).color(0.7F, 0.0F, 0.0F, alpha / 10.0F).endVertex();
            buffer.vertex(model, x2, y2, 0).color(0.7F, 0.0F, 0.0F, alpha / 10.0F).endVertex();
        }

        tesselator.end();
        matrices.popPose();
        return false;
    }
}
