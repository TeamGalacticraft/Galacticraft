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

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.CheeseSlimeEntityModel;
import dev.galacticraft.mod.client.render.entity.feature.CheeseSlimeOuterLayer;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.entity.CheeseSlimeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;

public class CheeseSlimeEntityRenderer  extends MobRenderer<CheeseSlimeEntity, CheeseSlimeEntityModel<CheeseSlimeEntity>>{
    private static final ResourceLocation TEXTURE = Constant.id(Constant.EntityTexture.CHEESE_SLIME);

    public CheeseSlimeEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new CheeseSlimeEntityModel<>(context.bakeLayer(GCEntityModelLayer.CHEESE_SLIME)), 0.25f);
        this.addLayer(new CheeseSlimeOuterLayer<>(this, context.getModelSet()));
    }

    public void render(CheeseSlimeEntity cheeseSlime, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        this.shadowRadius = 0.25F * cheeseSlime.getSize();
        super.render(cheeseSlime, f, g, poseStack, multiBufferSource, i);
    }

    protected void scale(Slime slime, PoseStack poseStack, float f) {
        float g = 0.999F;
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(0.0F, 0.001F, 0.0F);
        float h = slime.getSize();
        float i = Mth.lerp(f, slime.oSquish, slime.squish) / (h * 0.5F + 1.0F);
        float j = 1.0F / (i + 1.0F);
        poseStack.scale(j * h, 1.0F / j * h, j * h);
    }

    @Override
    public ResourceLocation getTextureLocation(CheeseSlimeEntity entity) {
        return TEXTURE;
    }
}