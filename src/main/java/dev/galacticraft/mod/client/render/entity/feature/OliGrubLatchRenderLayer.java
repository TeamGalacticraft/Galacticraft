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

package dev.galacticraft.mod.client.render.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.OliGrubEntityModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.entity.OliGrubEntity;
import dev.galacticraft.mod.content.entity.OliGrubEntity.AnchorPoint;
import dev.galacticraft.mod.mixin.client.AnimalModelAgeableListModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;


/**
 * Renders a latched OliGrub on the player's limb with manual per-limb offsets/rotations.
 */
public class OliGrubLatchRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation GRUB_TEXTURE = Constant.id(Constant.EntityTexture.OLI_GRUB);
    private final OliGrubEntityModel<OliGrubEntity> grubModel;

    private final @Nullable ModelPart head;
    private final @Nullable ModelPart body;
    private final @Nullable ModelPart rightArm;
    private final @Nullable ModelPart leftArm;
    private final @Nullable ModelPart rightLeg;
    private final @Nullable ModelPart leftLeg;



    public OliGrubLatchRenderLayer(RenderLayerParent<T, M> context) {
        super(context);
        this.grubModel = new OliGrubEntityModel<>(Minecraft.getInstance().getEntityModels()
                .bakeLayer(GCEntityModelLayer.OLI_GRUB));

        ModelPart h = null, b = null, ra = null, la = null, rl = null, ll = null;
        var model = context.getModel();

        if (model instanceof HumanoidModel<?> hm) {
            h  = hm.head;
            b  = hm.body;
            ra = hm.rightArm;
            la = hm.leftArm;
            rl = hm.rightLeg;
            ll = hm.leftLeg;
        } else if (model instanceof HierarchicalModel<?> hier) {
            var root = hier.root();
            h  = safeChild(root, PartNames.HEAD);
            b  = safeChild(root, PartNames.BODY);
            ra = safeChild(root, PartNames.RIGHT_ARM);
            la = safeChild(root, PartNames.LEFT_ARM);
            rl = safeChild(root, PartNames.RIGHT_LEG);
            ll = safeChild(root, PartNames.LEFT_LEG);
        } else if (model instanceof AnimalModelAgeableListModel animal) {
            h = animal.callGetHeadParts().iterator().hasNext() ? animal.callGetHeadParts().iterator().next() : null;
            b = animal.callGetBodyParts().iterator().hasNext() ? animal.callGetBodyParts().iterator().next() : null;
        }

        this.head = h; this.body = b; this.rightArm = ra; this.leftArm = la; this.rightLeg = rl; this.leftLeg = ll;
    }

    private static @Nullable ModelPart safeChild(ModelPart root, String name) {
        try { return root.getChild(name); } catch (Exception e) { return null; }
    }

    private @Nullable ModelPart limbFor(AnchorPoint a) {
        return switch (a) {
            case HEAD      -> head;
            case BODY      -> body;
            case RIGHT_ARM -> rightArm;
            case LEFT_ARM  -> leftArm;
            case RIGHT_LEG -> rightLeg;
            case LEFT_LEG  -> leftLeg;
        };
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light,
                       T entity, float limbAngle, float limbDistance, float tickDelta,
                       float ageInTicks, float headYaw, float headPitch) {
        if (!(entity instanceof Player player)) return;

        for (var passenger : player.getPassengers()) {
            if (!(passenger instanceof OliGrubEntity grub) || !grub.isLatched()) continue;

            var limb = limbFor(grub.getAnchor());
            if (limb == null) continue;

            pose.pushPose();

            limb.translateAndRotate(pose);

            pose.translate(grub.getLocalOffset().x, grub.getLocalOffset().y, grub.getLocalOffset().z);

            float rx = grub.getLocalRotX();
            float ry = grub.getLocalRotY();
            float rz = grub.getLocalRotZ();
            if (rx != 0f) pose.mulPose(Axis.XP.rotationDegrees(rx));
            if (ry != 0f) pose.mulPose(Axis.YP.rotationDegrees(ry));
            if (rz != 0f) pose.mulPose(Axis.ZP.rotationDegrees(rz));

            pose.scale(0.85f, 0.85f, 0.85f);

            float grubAge = grub.tickCount + tickDelta;
            grubModel.prepareMobModel(grub, 0f, 0f, tickDelta);
            grubModel.setupAnim(grub, 0f, 0f, grubAge, 0f, 0f);
            VertexConsumer vc = buffers.getBuffer(grubModel.renderType(GRUB_TEXTURE));
            grubModel.renderToBuffer(pose, vc, light, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);

            pose.popPose();
        }
    }
}