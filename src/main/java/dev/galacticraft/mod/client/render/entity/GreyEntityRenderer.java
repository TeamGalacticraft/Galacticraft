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

package dev.galacticraft.mod.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.ArchGreyEntityModel;
import dev.galacticraft.mod.client.model.entity.GreyEntityModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.entity.ArchGreyEntity;
import dev.galacticraft.mod.content.entity.grey.GreyEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;

public class GreyEntityRenderer extends MobRenderer<GreyEntity, EntityModel<GreyEntity>> {

    private final ItemRenderer itemRenderer;
    public GreyEntityRenderer(EntityRendererProvider.Context context, EntityModel<GreyEntity> model) {
        super(context, model, 0.4f);
        this.itemRenderer = context.getItemRenderer();
    }

    public GreyEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GreyEntityModel<>(context.bakeLayer(GCEntityModelLayer.GREY)), 0.4f);
        this.itemRenderer = context.getItemRenderer();
        this.addLayer(new ItemInHandLayer(this, context.getItemInHandRenderer()));
    }

    public static GreyEntityRenderer arch(EntityRendererProvider.Context context) {
        return new GreyEntityRenderer(context, new ArchGreyEntityModel<>(context.bakeLayer(GCEntityModelLayer.ARCH_GREY)));
    }

    @Override
    public ResourceLocation getTextureLocation(GreyEntity entity) {
        return entity instanceof ArchGreyEntity ? Constant.id(Constant.EntityTexture.ARCH_GREY) : Constant.id(Constant.EntityTexture.GREY);
    }

    @Override
    public void render(GreyEntity mob, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        super.render(mob, f, g, poseStack, multiBufferSource, i);
    }
}
