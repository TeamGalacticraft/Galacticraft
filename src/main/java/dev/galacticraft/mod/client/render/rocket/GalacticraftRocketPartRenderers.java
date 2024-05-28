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

package dev.galacticraft.mod.client.render.rocket;

import com.google.common.base.Suppliers;
import dev.galacticraft.api.entity.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.impl.client.rocket.render.BakedModelRocketPartRenderer;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.GCModelLoader;
import dev.galacticraft.mod.content.GCRocketParts;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.Objects;

public class GalacticraftRocketPartRenderers {
    private static final ResourceLocation DEFAULT_CONE = Constant.id("models/misc/rocket_cone_basic.json");
    private static final ResourceLocation ADVANCED_CONE = Constant.id("models/misc/rocket_cone_advanced.json");
    private static final ResourceLocation SLOPED_CONE = Constant.id("models/misc/rocket_cone_sloped.json");
    private static final ResourceLocation DEFAULT_BODY = Constant.id("models/misc/rocket_body.json");
    private static final ResourceLocation DEFAULT_FIN = Constant.id("models/misc/rocket_fins.json");
    private static final ResourceLocation DEFAULT_ENGINE = Constant.id("models/misc/rocket_engine.json");
    private static final ResourceLocation BOOSTER_TIER_1 = Constant.id("models/misc/rocket_thruster_tier_1.json");
    private static final ResourceLocation BOOSTER_TIER_2 = Constant.id("models/misc/rocket_thruster_tier_2.json");

    public static void register() {
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_1_CONE, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(GCModelLoader.INSTANCE.getModel(DEFAULT_CONE))), Sheets::translucentCullBlockSheet));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.ADVANCED_CONE, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(GCModelLoader.INSTANCE.getModel(ADVANCED_CONE))), Sheets::translucentCullBlockSheet));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.SLOPED_CONE, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(GCModelLoader.INSTANCE.getModel(SLOPED_CONE))), Sheets::translucentCullBlockSheet));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_1_BODY, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(GCModelLoader.INSTANCE.getModel(DEFAULT_BODY))), () -> RenderType.entitySolid(Constant.id("textures/rocket/rocket.png"))));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_1_FIN, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(GCModelLoader.INSTANCE.getModel(DEFAULT_FIN))), Sheets::translucentCullBlockSheet));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_1_ENGINE, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(GCModelLoader.INSTANCE.getModel(DEFAULT_ENGINE))), Sheets::translucentCullBlockSheet));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.STORAGE_UPGRADE, new BakedModelItemRocketPartRenderer(Items.CHEST.getDefaultInstance(), null));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_1_BOOSTER, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(GCModelLoader.INSTANCE.getModel(BOOSTER_TIER_1))), Sheets::translucentCullBlockSheet));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_2_BOOSTER, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(GCModelLoader.INSTANCE.getModel(BOOSTER_TIER_2))), Sheets::translucentCullBlockSheet));
    }

    public static void registerModelLoader() {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            out.accept(DEFAULT_CONE);
            out.accept(SLOPED_CONE);
            out.accept(ADVANCED_CONE);
            out.accept(DEFAULT_BODY);
            out.accept(DEFAULT_ENGINE);
            out.accept(DEFAULT_FIN);
            out.accept(BOOSTER_TIER_1);
            out.accept(BOOSTER_TIER_2);
        });
    }
}