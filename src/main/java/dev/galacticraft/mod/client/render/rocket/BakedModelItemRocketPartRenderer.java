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

package dev.galacticraft.mod.client.render.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.api.entity.rocket.render.RocketPartRenderer;
import dev.galacticraft.api.rocket.entity.Rocket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BakedModelItemRocketPartRenderer implements RocketPartRenderer {
    private final ItemStack stack;
    private final @Nullable BakedModel model;
    private final RenderType layer;

    public BakedModelItemRocketPartRenderer(ItemStack stack, @Nullable BakedModel model) {
        this.stack = stack;
        this.model = model;
        if (model != null) {
            this.layer = RenderType.entityTranslucent(model.getParticleIcon().atlasLocation());
        } else {
            this.layer = null;
        }
    }

    @Override
    public void renderGUI(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float delta) {
        graphics.renderFakeItem(this.stack, x, y);
    }

    @Override
    public void render(ClientLevel world, PoseStack matrices, Rocket rocket, MultiBufferSource vertices, float partialTick, int light, int overlay) {
        if (this.model != null) {
            PoseStack.Pose entry = matrices.last();
            VertexConsumer consumer = vertices.getBuffer(layer);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(entry, consumer, null, model, (rocket.red() / 255f) * (rocket.alpha() / 255f),
                    (rocket.green() / 255f) * (rocket.alpha() / 255f),
                    (rocket.blue() / 255f) * (rocket.alpha() / 255f),
                    light, overlay);
        }
    }
}