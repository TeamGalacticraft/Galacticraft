/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.client.render.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.galacticraft.api.entity.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.RocketEntity;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

public class RocketItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final RocketEntity rocket = new RocketEntity(GCEntityTypes.ROCKET, Minecraft.getInstance().level); // Fake rocket entity for rendering
    @Override
    public void render(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        RocketData data = RocketData.fromNbt(stack.getTag());
        rocket.setParts(data.parts());
        rocket.setColor(data.color());
        rocket.setOldPosAndRot();
        matrices.pushPose();
        if (mode == ItemTransforms.TransformType.GUI) {
            matrices.scale(0.25f, 0.25f, 0.25f);
            matrices.translate(1.5, 2, 2);
            matrices.mulPose(Axis.ZP.rotationDegrees(55));
            matrices.mulPose(Axis.XP.rotationDegrees(45));
            matrices.translate(0.5D, 0, 0.5D);
            matrices.mulPose(Axis.YP.rotation((float) (((Minecraft.getInstance().level.getGameTime() * 66.666666666666)) / 1000.0F)));
            matrices.translate(-0.5D, 0, -0.5D);
        } else if (mode == ItemTransforms.TransformType.GROUND) {
            matrices.scale(0.2f, 0.2f, 0.2f);
            matrices.translate(2, 3, 2);
        } else {
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(1, 0, 0);
        }
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        matrices.translate(0.0D, -1.75D, 0.0D);
        ResourceLocation part = data.bottom();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(Minecraft.getInstance().level, matrices, rocket, vertexConsumers, 0, light);
            matrices.popPose();
        }

        matrices.translate(0.0D, 0.5, 0.0D);

        part = data.booster();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(Minecraft.getInstance().level, matrices, rocket, vertexConsumers, 0, light);
            matrices.popPose();
        }

        part = data.fin();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(Minecraft.getInstance().level, matrices, rocket, vertexConsumers, 0, light);
            matrices.popPose();
        }

        matrices.translate(0.0D, 1.0D, 0.0D);

        part = data.body();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(Minecraft.getInstance().level, matrices, rocket, vertexConsumers, 0, light);
            matrices.popPose();
        }

        matrices.translate(0.0D, 1.75, 0.0D);

        part = data.cone();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(Minecraft.getInstance().level, matrices, rocket, vertexConsumers, 0, light);
            matrices.popPose();
        }

        matrices.popPose();
    }
}
