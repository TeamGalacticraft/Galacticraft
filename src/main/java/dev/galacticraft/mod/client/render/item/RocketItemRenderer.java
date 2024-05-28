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

package dev.galacticraft.mod.client.render.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.galacticraft.api.entity.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RocketItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final RocketEntity rocket = new RocketEntity(GCEntityTypes.ROCKET, null); // Fake rocket entity for rendering

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        RocketData data = RocketData.fromNbt(stack.getTag());
        rocket.setLevel(Minecraft.getInstance().level);
        rocket.setData(data);
        rocket.setOldPosAndRot();
        matrices.pushPose();
        ClientLevel level = Minecraft.getInstance().level;
        if (mode == ItemDisplayContext.GUI) {
            matrices.scale(0.25f, 0.25f, 0.25f);
            matrices.translate(1.5, 2, 2);
            matrices.mulPose(Axis.ZP.rotationDegrees(55));
            matrices.mulPose(Axis.XP.rotationDegrees(45));
            matrices.mulPose(Axis.YP.rotation((float) (level.getGameTime() * 66.666666666666 / 1000.0F)));
        } else if (mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || mode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            matrices.mulPose(Axis.YP.rotationDegrees(45));
            matrices.scale(2F, 2F, 2F);
            matrices.mulPose(Axis.XP.rotation(Mth.HALF_PI));
            matrices.mulPose(Axis.ZN.rotation(0.65F));
            matrices.translate(0.5F, -0.5F, -2.6);
        } else if (mode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || mode == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            matrices.mulPose(Axis.ZN.rotation(Mth.HALF_PI));
            matrices.mulPose(Axis.YP.rotation(Mth.HALF_PI));
            matrices.translate(0F, 0F, .2F);
            matrices.mulPose(Axis.XN.rotation(0.2F));
            matrices.mulPose(Axis.ZP.rotation(0.3F));
            matrices.mulPose(Axis.ZN.rotation(0.65F));
            matrices.translate(-.0, -.8F, -.8F);
        } else if (mode == ItemDisplayContext.GROUND) {
            matrices.scale(0.2f, 0.2f, 0.2f);
            matrices.translate(2, 3, 2);
        } else {
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(1, 0, 0);
        }
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        matrices.translate(0.0D, -1.75D, 0.0D);
        ResourceKey<? extends RocketPart<?, ?>> part = data.engine();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(level, matrices, rocket, vertexConsumers, 0, light, overlay);
            matrices.popPose();
        }

        matrices.translate(0.0D, 0.5, 0.0D);

        part = data.booster();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(level, matrices, rocket, vertexConsumers, 0, light, overlay);
            matrices.popPose();
        }

        part = data.fin();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(level, matrices, rocket, vertexConsumers, 0, light, overlay);
            matrices.popPose();
        }

        matrices.translate(0.0D, 1.0D, 0.0D);

        part = data.body();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(level, matrices, rocket, vertexConsumers, 0, light, overlay);
            matrices.popPose();
        }

        matrices.translate(0.0D, 1.75, 0.0D);

        part = data.cone();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(level, matrices, rocket, vertexConsumers, 0, light, overlay);
            matrices.popPose();
        }

        matrices.popPose();
    }
}
