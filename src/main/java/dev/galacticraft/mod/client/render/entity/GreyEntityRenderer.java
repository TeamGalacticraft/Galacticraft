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

package dev.galacticraft.mod.client.render.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.ArchGreyEntityModel;
import dev.galacticraft.mod.client.model.entity.GreyEntityModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.entity.ArchGreyEntity;
import dev.galacticraft.mod.content.entity.GreyEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GreyEntityRenderer extends MobRenderer<GreyEntity, EntityModel<GreyEntity>> {
    public GreyEntityRenderer(EntityRendererProvider.Context context, EntityModel<GreyEntity> model) {
        super(context, model, 0.4f);
    }

    public GreyEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GreyEntityModel<>(context.bakeLayer(GCEntityModelLayer.GREY)), 0.4f);
    }

    public static GreyEntityRenderer arch(EntityRendererProvider.Context context) {
        return new GreyEntityRenderer(context, new ArchGreyEntityModel<>(context.bakeLayer(GCEntityModelLayer.ARCH_GREY)));
    }

    @Override
    public ResourceLocation getTextureLocation(GreyEntity entity) {
        return entity instanceof ArchGreyEntity ? Constant.id(Constant.EntityTexture.ARCH_GREY) : Constant.id(Constant.EntityTexture.GREY);
    }
}
