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

package dev.galacticraft.mod.client.render.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.EvolvedCreeperEntityModel;
import dev.galacticraft.mod.client.render.entity.feature.EvolvedCreeperChargeFeatureRenderer;
import dev.galacticraft.mod.client.render.entity.model.GalacticraftEntityModelLayer;
import dev.galacticraft.mod.entity.EvolvedCreeperEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class EvolvedCreeperEntityRenderer extends MobEntityRenderer<EvolvedCreeperEntity, EvolvedCreeperEntityModel> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, "textures/entity/evolved/creeper.png");

    public EvolvedCreeperEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new EvolvedCreeperEntityModel(context.getPart(GalacticraftEntityModelLayer.EVOLVED_CREEPER), true), 0.5F);
        this.addFeature(new EvolvedCreeperChargeFeatureRenderer(this, context.getModelLoader()));
    }

    @Override
    protected void scale(EvolvedCreeperEntity entity, MatrixStack matrices, float tickDelta) {
        float g = entity.getClientFuseTime(tickDelta);
        float h = 1.0F + MathHelper.sin(g * 100.0F) * g * 0.01F;
        g = MathHelper.clamp(g, 0.0F, 1.0F);
        g *= g;
        g *= g;
        float i = (1.0F + g * 0.4F) * h;
        float j = (1.0F + g * 0.1F) / h;
        matrices.scale(i, j, i);
    }

    @Override
    protected float getAnimationCounter(EvolvedCreeperEntity entity, float tickDelta) {
        float g = entity.getClientFuseTime(tickDelta);
        return (int) (g * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(g, 0.5F, 1.0F);
    }

    @Override
    public void render(EvolvedCreeperEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(EvolvedCreeperEntity entity) {
        return TEXTURE;
    }
}
