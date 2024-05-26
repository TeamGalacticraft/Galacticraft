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

package dev.galacticraft.mod.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.GCBakedModel;
import dev.galacticraft.mod.client.model.GCModelLoader;
import dev.galacticraft.mod.client.model.GCModelState;
import dev.galacticraft.mod.client.model.GCSheets;
import dev.galacticraft.mod.content.entity.Buggy;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BuggyRenderer extends EntityRenderer<Buggy> {
    public static final ResourceLocation MODEL = Constant.id("models/misc/buggy.json");
    public static final GCModelState MAIN_MODEL = new GCModelState("MainBody");
    public static final GCModelState RADAR_DISH = new GCModelState("RadarDish_Dish");
    public static final GCModelState WHEEL_LEFT_COVER = new GCModelState("Wheel_Left_Cover");
    public static final GCModelState WHEEL_RIGHT = new GCModelState("Wheel_Right");
    public static final GCModelState WHEEL_LEFT = new GCModelState("Wheel_Left");
    public static final GCModelState WHEEL_RIGHT_COVER = new GCModelState("Wheel_Right_Cover");
    public static final GCModelState CARGO_LEFT = new GCModelState("CargoLeft");
    public static final GCModelState CARGO_MID = new GCModelState("CargoMid");
    public static final GCModelState CARGO_RIGHT = new GCModelState("CargoRight");

    private GCBakedModel buggyModel;

    public BuggyRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Buggy buggy, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (this.buggyModel == null)
            this.buggyModel = GCModelLoader.INSTANCE.getModel(MODEL);
        float pitch = Mth.lerp(tickDelta, buggy.xRotO, buggy.getXRot());
        VertexConsumer consumer = vertexConsumers.getBuffer(GCSheets.obj(GCSheets.OBJ_ATLAS));
        matrices.pushPose();
        matrices.scale(1.0F, 1.0F, 1.0F);
        matrices.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        matrices.mulPose(Axis.ZP.rotationDegrees(-pitch));
        matrices.scale(0.41F, 0.41F, 0.41F);

        // Front wheels
        matrices.pushPose();
        float dZ = -2.727F;
        float dY = 0.976F;
        float dX = 1.25F;
        float rotation = buggy.wheelRotationX;
        matrices.translate(dX, dY, dZ);
        matrices.mulPose(Axis.YP.rotationDegrees(buggy.wheelRotationZ));
        this.buggyModel.render(matrices, WHEEL_RIGHT_COVER, consumer, light, OverlayTexture.NO_OVERLAY);
        matrices.mulPose(Axis.XP.rotationDegrees(rotation));
        this.buggyModel.render(matrices, WHEEL_RIGHT, consumer, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();

        matrices.pushPose();
        matrices.translate(-dX, dY, dZ);
        matrices.mulPose(Axis.YP.rotationDegrees(buggy.wheelRotationZ));
        this.buggyModel.render(matrices, WHEEL_LEFT_COVER, consumer, light, OverlayTexture.NO_OVERLAY);
        matrices.mulPose(Axis.XP.rotationDegrees(rotation));
        this.buggyModel.render(matrices, WHEEL_LEFT, consumer, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();

        // Back wheels
        matrices.pushPose();
        dX = 1.9F;
        dZ = -dZ;
        matrices.translate(dX, dY, dZ);
        matrices.mulPose(Axis.YP.rotationDegrees(-buggy.wheelRotationZ));
        matrices.mulPose(Axis.XP.rotationDegrees(rotation));
        this.buggyModel.render(matrices, WHEEL_RIGHT, consumer, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();

        matrices.pushPose();
        matrices.translate(-dX, dY, dZ);
        matrices.mulPose(Axis.YP.rotationDegrees(-buggy.wheelRotationZ));
        matrices.mulPose(Axis.XP.rotationDegrees(rotation));
        this.buggyModel.render(matrices, WHEEL_LEFT, consumer, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();

        this.buggyModel.render(matrices, MAIN_MODEL, consumer, light, OverlayTexture.NO_OVERLAY);

        // Radar Dish
        matrices.pushPose();
        matrices.translate(-1.178F, 4.1F, -2.397F);
        int ticks = buggy.tickCount + buggy.getId() * 10000;
        matrices.mulPose(Axis.XP.rotationDegrees((float) Math.sin(ticks * 0.05) * 50.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees((float) Math.cos(ticks * 0.1) * 50.0F));
        this.buggyModel.render(matrices, RADAR_DISH, consumer, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();

//        if (buggy.buggyType > 0) {
//            ClientUtil.drawBakedModel(this.cargoLeft);
//
//            if (entity.buggyType > 1) {
//                ClientUtil.drawBakedModel(this.cargoMid);
//
//                if (entity.buggyType > 2) {
//                    ClientUtil.drawBakedModel(this.cargoRight);
//                }
//            }
//        }

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Buggy entity) {
        return null;
    }
}
