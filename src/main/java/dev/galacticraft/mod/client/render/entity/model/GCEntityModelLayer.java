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

package dev.galacticraft.mod.client.render.entity.model;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.*;
import dev.galacticraft.mod.client.render.block.entity.BasicSolarPanelBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class GCEntityModelLayer {
    public static final ModelLayerLocation GAZER = new ModelLayerLocation(Constant.id("gazer"), "main");
    public static final ModelLayerLocation RUMBLER = new ModelLayerLocation(Constant.id("rumbler"), "main");
    public static final ModelLayerLocation COMET_CUBE = new ModelLayerLocation(Constant.id("comet_cube"), "main");
    public static final ModelLayerLocation OLI_GRUB = new ModelLayerLocation(Constant.id("oli_grub"), "main");
    public static final ModelLayerLocation GREY = new ModelLayerLocation(Constant.id("grey"), "main");
    public static final ModelLayerLocation ARCH_GREY = new ModelLayerLocation(Constant.id("arch_grey"), "main");
    public static final ModelLayerLocation SOLAR_PANEL = new ModelLayerLocation(Constant.id("solar_panel"), "main");
    public static final ModelLayerLocation LANDER = new ModelLayerLocation(Constant.id("lander"), "main");
    public static final ModelLayerLocation PARACHEST = new ModelLayerLocation(Constant.id("parachest"), "main");
    public static final ModelLayerLocation MOON_VILLAGER = new ModelLayerLocation(Constant.id("moon_villager"), "main");
    // Bosses
    public static final ModelLayerLocation SKELETON_BOSS = new ModelLayerLocation(Constant.id("skeleton_boss"), "main");

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(GAZER, GazerEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(RUMBLER, RumblerEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(COMET_CUBE, CometCubeEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(OLI_GRUB, OliGrubEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(GREY, GreyEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ARCH_GREY, ArchGreyEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(LANDER, LanderModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(PARACHEST, ParachestModel::createParachuteLayer);
        EntityModelLayerRegistry.registerModelLayer(MOON_VILLAGER, MoonVillagerModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(SKELETON_BOSS, EvolvedSkeletonBossModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(SOLAR_PANEL, BasicSolarPanelBlockEntityRenderer::getTexturedModelData);
    }
}
