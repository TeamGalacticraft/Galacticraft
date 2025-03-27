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

package dev.galacticraft.mod.client.render.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.entity.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.RocketPrefabs;
import dev.galacticraft.api.rocket.part.RocketBooster;
import dev.galacticraft.api.rocket.part.RocketEngine;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class RocketItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final RocketEntity rocket = new RocketEntity(GCEntityTypes.ROCKET, null); // Fake rocket entity for rendering

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        RocketData data = stack.has(GCDataComponents.ROCKET_DATA) ? stack.get(GCDataComponents.ROCKET_DATA) : RocketPrefabs.MISSING;
        ClientLevel level = Minecraft.getInstance().level;
        rocket.setLevel(level);
        rocket.setData(data);
        rocket.setOldPosAndRot();
        matrices.pushPose();

        switch (mode) {
            case ItemDisplayContext.THIRD_PERSON_LEFT_HAND:
                matrices.mulPose(Axis.XP.rotation(Mth.HALF_PI - 0.2F));
                matrices.mulPose(Axis.ZN.rotation(Mth.HALF_PI - 0.4F));
                matrices.mulPose(Axis.YP.rotation(Mth.PI));
                matrices.translate(-0.4F, 0.0F, 0.5F);
                break;
            case ItemDisplayContext.THIRD_PERSON_RIGHT_HAND:
                matrices.mulPose(Axis.XP.rotation(Mth.HALF_PI - 0.2F));
                matrices.mulPose(Axis.ZN.rotation(Mth.HALF_PI + 0.2F));
                matrices.mulPose(Axis.YP.rotation(Mth.PI));
                matrices.translate(0.1F, -1.6F, 0.5F);
                break;
            case ItemDisplayContext.FIRST_PERSON_LEFT_HAND:
            case ItemDisplayContext.FIRST_PERSON_RIGHT_HAND:
                matrices.scale(2.0F, 2.0F, 2.0F);
                matrices.mulPose(Axis.YP.rotationDegrees(45));
                matrices.mulPose(Axis.XP.rotation(Mth.HALF_PI + 0.2F));
                matrices.mulPose(Axis.ZN.rotation(0.65F));
                matrices.translate(0.5F, -2.0F, -2.0F);
                break;
            case ItemDisplayContext.GUI:
                matrices.translate(0.7F, 0.35F, 0.0F);
                matrices.scale(0.25F, 0.25F, 0.25F);
                matrices.mulPose(Axis.ZP.rotationDegrees(55));
                matrices.mulPose(Axis.XP.rotationDegrees(45));
                matrices.mulPose(Axis.YP.rotation((float) (level.getGameTime() * 66.666666666666 / 1000.0F)));
                break;
            case ItemDisplayContext.FIXED:
                matrices.translate(0.5F, 0.0F, 0.4F);
                matrices.scale(0.25F, 0.25F, 0.25F);
                break;
            case ItemDisplayContext.GROUND:
            default:
                matrices.translate(0.5F, 0.5F, 0.5F);
                matrices.scale(0.2F, 0.2F, 0.2F);
        }
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        data.engine().ifPresent(part -> {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part.key()).render(level, matrices, rocket, vertexConsumers, 0, light, overlay);
            matrices.popPose();
        });

        matrices.translate(0.0F, 0.5F, 0.0F);

        data.booster().ifPresent(part -> {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part.key()).render(level, matrices, rocket, vertexConsumers, 0, light, overlay);
            matrices.popPose();
        });

        data.fin().ifPresent(part -> {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part.key()).render(level, matrices, rocket, vertexConsumers, 0, light, overlay);
            matrices.popPose();
        });

        matrices.translate(0.0F, 1.0F, 0.0F);

        data.body().ifPresent(part -> {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part.key()).render(level, matrices, rocket, vertexConsumers, 0, light, overlay);
            matrices.popPose();
        });

        matrices.translate(0.0F, 1.75F, 0.0F);

        data.cone().ifPresent(part -> {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part.key()).render(level, matrices, rocket, vertexConsumers, 0, light, overlay);
            matrices.popPose();
        });

        matrices.popPose();
    }
}
