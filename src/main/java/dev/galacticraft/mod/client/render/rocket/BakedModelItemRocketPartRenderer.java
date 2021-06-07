/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.client.render.rocket;

import dev.galacticraft.api.client.rocket.render.RocketPartRenderer;
import dev.galacticraft.api.entity.Rocket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BakedModelItemRocketPartRenderer implements RocketPartRenderer {
    private final ItemStack stack;
    private final @Nullable BakedModel model;
    private final RenderLayer layer;

    public BakedModelItemRocketPartRenderer(ItemStack stack, @Nullable BakedModel model) {
        this.stack = stack;
        this.model = model;
        if (model != null) {
            this.layer = RenderLayer.getEntityTranslucent(model.getSprite().getId());
        } else {
            this.layer = null;
        }
    }

    @Override
    public void renderGUI(ClientWorld world, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(stack, (int)matrices.peek().getModel().a03, (int)matrices.peek().getModel().a13);
    }

    @Override
    public void render(ClientWorld world, MatrixStack matrices, Rocket rocket, VertexConsumerProvider vertices, float delta, int light) {
        if (this.model != null) {
            MatrixStack.Entry entry = matrices.peek();
            VertexConsumer consumer = vertices.getBuffer(layer);
            for (BakedQuad quad : model.getQuads(null, null, world.random)) {
                consumer.quad(entry, quad, (((rocket.getColor() << 16) & 0xFF) / 255f) * (((rocket.getColor() << 24) & 0xFF) / 255f),
                        (((rocket.getColor() << 8) & 0xFF) / 255f) * (((rocket.getColor() << 24) & 0xFF) / 255f),
                        ((rocket.getColor() & 0xFF) / 255f) * (((rocket.getColor() << 24) & 0xFF) / 255f),
                        light, OverlayTexture.DEFAULT_UV);
            }
        }
    }
}
