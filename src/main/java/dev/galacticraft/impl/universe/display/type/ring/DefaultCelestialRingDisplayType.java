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

import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.Codec;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplayType;
import dev.galacticraft.impl.universe.display.config.ring.DefaultCelestialRingDisplayConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultCelestialRingDisplayType extends CelestialRingDisplayType<DefaultCelestialRingDisplayConfig> {
    public static final DefaultCelestialRingDisplayType INSTANCE = new DefaultCelestialRingDisplayType(DefaultCelestialRingDisplayConfig.CODEC);

    public DefaultCelestialRingDisplayType(Codec<DefaultCelestialRingDisplayConfig> codec) {
        super(codec);
    }

    @Override
    public boolean render(CelestialBody<?, ?> body, GuiGraphics graphics, int count, Vector3f systemOffset, float lineScale, float alpha, double mouseX, double mouseY, float delta, Consumer<Supplier<ShaderInstance>> shaderSetter, DefaultCelestialRingDisplayConfig config) {
        shaderSetter.accept(GameRenderer::getRendertypeLinesShader);

        PoseStack matrices = graphics.pose();

        final float theta = (float) (2f * Math.PI / 90f);
        final float cos = Mth.cos(theta);
        final float sin = Mth.sin(theta);
        final float theta2 = (float) (2f * Math.PI / -90f);
        final float cos2 = Mth.cos(theta2);
        final float sin2 = Mth.sin(theta2);


        float x = lineScale;
        if (Float.isNaN(x)) return false;
        float y = 0;

        if (alpha > 0.0F) {
            matrices.pushPose();
            matrices.translate(systemOffset.x(), systemOffset.y(), systemOffset.z());
//                matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(55));
            float[] color = switch (count % 2) {
                case 0 -> new float[]{0.0F / 1.4F, 0.6F / 1.4F, 1.0F / 1.4F, alpha / 1.4F};
                case 1 -> new float[]{0.3F / 1.4F, 0.8F / 1.4F, 1.0F / 1.4F, alpha / 1.4F};
                default -> throw new IllegalStateException("Unexpected value: " + count % 2);
            };

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR_NORMAL);

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
            matrices.popPose();

            return true;
        }

        return false;
    }
}
