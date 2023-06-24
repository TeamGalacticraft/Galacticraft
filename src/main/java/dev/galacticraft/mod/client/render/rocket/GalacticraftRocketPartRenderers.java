/*
 * Copyright (c) 2019-2023 Team Galacticraft
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
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCRocketParts;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GalacticraftRocketPartRenderers {
    private static final ResourceLocation DEFAULT_CONE = new ResourceLocation(Constant.MOD_ID, "models/misc/rocket_cone_basic.json");
    private static final ResourceLocation ADVANCED_CONE = new ResourceLocation(Constant.MOD_ID, "models/misc/rocket_cone_advanced.json");
    private static final ResourceLocation SLOPED_CONE = new ResourceLocation(Constant.MOD_ID, "models/misc/rocket_cone_sloped.json");
    private static final ResourceLocation DEFAULT_BODY = new ResourceLocation(Constant.MOD_ID, "models/misc/rocket_body.json");
    private static final ResourceLocation DEFAULT_FIN = new ResourceLocation(Constant.MOD_ID, "models/misc/rocket_fins.json");
    private static final ResourceLocation DEFAULT_BOTTOM = new ResourceLocation(Constant.MOD_ID, "models/misc/rocket_bottom.json");
    private static final ResourceLocation BOOSTER_TIER_1 = new ResourceLocation(Constant.MOD_ID, "models/misc/rocket_thruster_tier_1.json");
    private static final ResourceLocation BOOSTER_TIER_2 = new ResourceLocation(Constant.MOD_ID, "models/misc/rocket_thruster_tier_2.json");

    private static BakedModel EMPTY_MODEL = new BakedModel() {
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
            return Collections.emptyList();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean usesBlockLight() {
            return false;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return null;
        }

        @Override
        public ItemTransforms getTransforms() {
            return ItemTransforms.NO_TRANSFORMS;
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }
    };

    public static void register() {
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_1_CONE, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(BakedModelManagerHelper.getModel(Minecraft.getInstance().getModelManager(), DEFAULT_CONE))), () -> RenderType.entityTranslucent(new ResourceLocation(Constant.MOD_ID, "textures/model/rocket.png"))));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.ADVANCED_CONE, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(BakedModelManagerHelper.getModel(Minecraft.getInstance().getModelManager(), ADVANCED_CONE))), () -> RenderType.entityTranslucent(new ResourceLocation(Constant.MOD_ID, "textures/model/rocket.png"))));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.SLOPED_CONE, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(BakedModelManagerHelper.getModel(Minecraft.getInstance().getModelManager(), SLOPED_CONE))), () -> RenderType.entityTranslucent(new ResourceLocation(Constant.MOD_ID, "textures/model/rocket.png"))));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_1_BODY, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(BakedModelManagerHelper.getModel(Minecraft.getInstance().getModelManager(), DEFAULT_BODY))), () -> RenderType.entityTranslucent(new ResourceLocation(Constant.MOD_ID, "textures/model/rocket.png"))));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_1_FIN, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(BakedModelManagerHelper.getModel(Minecraft.getInstance().getModelManager(), DEFAULT_FIN))), () -> RenderType.entityTranslucent(new ResourceLocation(Constant.MOD_ID, "textures/model/rocket.png"))));
        RocketPartRendererRegistry.INSTANCE.register(BuiltinObjects.INVALID_ROCKET_UPGRADE, new BakedModelItemRocketPartRenderer(Items.BARRIER.getDefaultInstance(), null)); //fixme
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_1_BOTTOM, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(BakedModelManagerHelper.getModel(Minecraft.getInstance().getModelManager(), DEFAULT_BOTTOM))), () -> RenderType.entityTranslucent(new ResourceLocation(Constant.MOD_ID, "textures/model/rocket.png"))));
        RocketPartRendererRegistry.INSTANCE.register(BuiltinObjects.INVALID_ROCKET_BOOSTER, new BakedModelItemRocketPartRenderer(Items.BARRIER.getDefaultInstance(), null));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.STORAGE_UPGRADE, new BakedModelItemRocketPartRenderer(Items.CHEST.getDefaultInstance(), null));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_1_BOOSTER, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(BakedModelManagerHelper.getModel(Minecraft.getInstance().getModelManager(), BOOSTER_TIER_1))), () -> RenderType.entityTranslucent(new ResourceLocation("galacticraft:textures/model/rocket.png"))));
        RocketPartRendererRegistry.INSTANCE.register(GCRocketParts.TIER_2_BOOSTER, new BakedModelRocketPartRenderer(Suppliers.memoize(() -> Objects.requireNonNull(BakedModelManagerHelper.getModel(Minecraft.getInstance().getModelManager(), BOOSTER_TIER_2))), () -> RenderType.entityTranslucent(new ResourceLocation("galacticraft:textures/model/rocket.png"))));
    }

    public static void registerModelLoader() {
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