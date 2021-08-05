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

package dev.galacticraft.mod.client.render.rocket;

import dev.galacticraft.api.client.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.impl.client.rocket.render.BakedModelRocketPartRenderer;
import dev.galacticraft.mod.Constant;
import dev.onyxstudios.foml.obj.OBJLoader;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

public class GalacticraftRocketPartRenderers {
    private static final Identifier DEFAULT_CONE = new Identifier(Constant.MOD_ID, "misc/rocket_cone_basic.obj");
    private static final Identifier ADVANCED_CONE = new Identifier(Constant.MOD_ID, "misc/rocket_cone_advanced.obj");
    private static final Identifier SLOPED_CONE = new Identifier(Constant.MOD_ID, "misc/rocket_cone_sloped.obj");
    private static final Identifier DEFAULT_BODY = new Identifier(Constant.MOD_ID, "misc/rocket_body.obj");
    private static final Identifier DEFAULT_FIN = new Identifier(Constant.MOD_ID, "misc/rocket_fins.obj");
    private static final Identifier DEFAULT_BOTTOM = new Identifier(Constant.MOD_ID, "misc/rocket_bottom.obj");
    private static final Identifier BOOSTER_TIER_1 = new Identifier(Constant.MOD_ID, "misc/rocket_thruster_tier_1.obj");
    private static final Identifier BOOSTER_TIER_2 = new Identifier(Constant.MOD_ID, "misc/rocket_thruster_tier_2.obj");

    public static void register() {
        Supplier<RenderLayer> supplier = () -> RenderLayer.getEntityTranslucent(new Identifier(Constant.MOD_ID, "textures/model/rocket.png"));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "default_cone"), new BakedModelRocketPartRenderer(() -> Objects.requireNonNull(OBJLoader.getModel(DEFAULT_CONE)), supplier));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "advanced_cone"), new BakedModelRocketPartRenderer(() -> Objects.requireNonNull(OBJLoader.getModel(ADVANCED_CONE)), supplier));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "sloped_cone"), new BakedModelRocketPartRenderer(() -> Objects.requireNonNull(OBJLoader.getModel(SLOPED_CONE)), supplier));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "default_body"), new BakedModelRocketPartRenderer(() -> Objects.requireNonNull(OBJLoader.getModel(DEFAULT_BODY)), supplier));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "default_fin"), new BakedModelRocketPartRenderer(() -> Objects.requireNonNull(OBJLoader.getModel(DEFAULT_FIN)), supplier));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "default_upgrade"), new BakedModelItemRocketPartRenderer(new ItemStack(Items.BARRIER), null));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "default_bottom"), new BakedModelRocketPartRenderer(() -> Objects.requireNonNull(OBJLoader.getModel(DEFAULT_BOTTOM)), supplier));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "default_booster"), new BakedModelItemRocketPartRenderer(new ItemStack(Items.BARRIER), null));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "storage_upgrade"), new BakedModelItemRocketPartRenderer(new ItemStack(Items.CHEST), null));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "booster_1"), new BakedModelRocketPartRenderer(() -> Objects.requireNonNull(OBJLoader.getModel(BOOSTER_TIER_1)), supplier));
        RocketPartRendererRegistry.INSTANCE.register(new Identifier(Constant.MOD_ID, "booster_2"), new BakedModelRocketPartRenderer(() -> Objects.requireNonNull(OBJLoader.getModel(BOOSTER_TIER_2)), supplier));
    }

    public static void registerModels() {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            out.accept(DEFAULT_CONE);
            out.accept(SLOPED_CONE);
            out.accept(ADVANCED_CONE);
            out.accept(DEFAULT_BODY);
            out.accept(DEFAULT_BOTTOM);
            out.accept(DEFAULT_FIN);
            out.accept(BOOSTER_TIER_1);
            out.accept(BOOSTER_TIER_2);
        });
    }
}