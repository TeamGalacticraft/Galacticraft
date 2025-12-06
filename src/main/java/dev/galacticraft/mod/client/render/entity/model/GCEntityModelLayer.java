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
import dev.galacticraft.mod.client.render.block.entity.SolarPanelBlockEntityRenderer;
import dev.galacticraft.mod.client.render.block.entity.RocketWorkbenchBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class GCEntityModelLayer {
    private static final String DEFAULT_LAYER = "main";

    public static final ModelLayerLocation GAZER = registerModelLayer("gazer");
    public static final ModelLayerLocation RUMBLER = registerModelLayer("rumbler");
    public static final ModelLayerLocation COMET_CUBE = registerModelLayer("comet_cube");
    public static final ModelLayerLocation OLI_GRUB = registerModelLayer("oli_grub");
    public static final ModelLayerLocation CHEESE_SLIME = registerModelLayer("cheese_slime");
    public static final ModelLayerLocation CHEESE_SLIME_OUTER = registerModelLayer("cheese_slime", "outer");
    public static final ModelLayerLocation GREY = registerModelLayer("grey");
    public static final ModelLayerLocation ARCH_GREY = registerModelLayer("arch_grey");
    public static final ModelLayerLocation LANDER = registerModelLayer("lander");
    public static final ModelLayerLocation PARACHEST = registerModelLayer("parachest");
    public static final ModelLayerLocation MOON_VILLAGER = registerModelLayer("moon_villager");

    // Bosses
    public static final ModelLayerLocation SKELETON_BOSS = registerModelLayer("skeleton_boss");

    // Block Entity Renderers
    public static final ModelLayerLocation SOLAR_PANEL = registerModelLayer("solar_panel");
    public static final ModelLayerLocation ROCKET_WORKBENCH = registerModelLayer("rocket_workbench");

    private static ModelLayerLocation registerModelLayer(String id) {
        return registerModelLayer(id, DEFAULT_LAYER);
    }

    private static ModelLayerLocation registerModelLayer(String id, String layer) {
        return new ModelLayerLocation(Constant.id(id), layer);
    }

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(GAZER, GazerEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(RUMBLER, RumblerEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(COMET_CUBE, CometCubeEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(OLI_GRUB, OliGrubEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CHEESE_SLIME, CheeseSlimeEntityModel::createInnerBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CHEESE_SLIME_OUTER, CheeseSlimeEntityModel::createOuterBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(GREY, GreyEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ARCH_GREY, ArchGreyEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(LANDER, LanderModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(PARACHEST, ParachestModel::createParachuteLayer);
        EntityModelLayerRegistry.registerModelLayer(MOON_VILLAGER, MoonVillagerModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(SKELETON_BOSS, EvolvedSkeletonBossModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(SOLAR_PANEL, SolarPanelBlockEntityRenderer::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ROCKET_WORKBENCH, RocketWorkbenchBlockEntityRenderer::getTexturedModelData);
    }
}
