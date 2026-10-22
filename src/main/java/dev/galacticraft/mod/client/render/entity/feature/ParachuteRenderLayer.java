/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.client.render.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.Parachute;
import dev.galacticraft.mod.content.item.ParachuteItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;

/**
 * Renders the open parachute above a player, based on the poses of Galacticraft 4.
 */
public class ParachuteRenderLayer<T extends Player, M extends EntityModel<T>> extends RenderLayer<T, M> {
    /**
     * How far the strings are attached from the middle of the body, so they end at the shoulders.
     */
    private static final float STRING_X = 7.0F;
    /**
     * How far the whole parachute is moved down, so the strings end on top of the shoulders instead of next to the head.
     */
    private static final float OFFSET_Y = 7.0F / 16.0F;

    private final ModelPart root;

    public ParachuteRenderLayer(RenderLayerParent<T, M> context, ModelPart root) {
        super(context);
        this.root = root;

        root.getChild("parachute_0").zRot = Mth.PI / 6.0F;
        root.getChild("parachute_2").zRot = -Mth.PI / 6.0F;
        this.poseString("parachute_string_0", -STRING_X, 0.40142575F, 2.7052605F);
        this.poseString("parachute_string_1", -STRING_X, -0.40142575F, 2.7052605F);
        this.poseString("parachute_string_2", STRING_X, 0.40142575F, -2.7052605F);
        this.poseString("parachute_string_3", STRING_X, -0.40142575F, -2.7052605F);
    }

    private void poseString(String name, float x, float xRot, float zRot) {
        ModelPart string = this.root.getChild(name);
        string.setPos(x, -7.0F, 2.0F);
        string.xRot = xRot;
        string.zRot = zRot;
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!entity.galacticraft$isParachuting() || entity.isInvisible()) {
            return;
        }
        matrices.pushPose();
        matrices.translate(0.0F, OFFSET_Y, 0.0F);
        this.root.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        ParachuteItem parachute = Parachute.getParachute(entity);
        DyeColor color = parachute != null && parachute.getColor() != null ? parachute.getColor() : DyeColor.WHITE;
        return Constant.id("textures/model/parachute/" + color.getName() + ".png");
    }
}
