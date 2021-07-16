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

package dev.galacticraft.mod.api.rocket.part;

import dev.galacticraft.api.client.rocket.render.RocketPartRenderer;
import dev.galacticraft.api.client.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.impl.rocket.travelpredicate.config.AccessTypeTravelPredicateConfig;
import dev.galacticraft.impl.rocket.travelpredicate.config.AccessWeightTravelPredicateConfig;
import dev.galacticraft.impl.rocket.travelpredicate.type.AccessWeightPredicateType;
import dev.galacticraft.impl.rocket.travelpredicate.type.ConstantTravelPredicateType;
import dev.galacticraft.mod.Constant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class GalacticraftRocketParts {
    public static final RocketPart DEFAULT_CONE = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.default_cone"))
            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.CONE)
            .build();

    public static final RocketPart DEFAULT_BODY = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.default_body"))
            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.BODY)
            .build();

    public static final RocketPart DEFAULT_FIN = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.default_fin"))
            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.FIN)
            .build();

    public static final RocketPart NO_BOOSTER = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.default_booster"))
            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.BOOSTER)
            .recipe(false)
            .build();

    public static final RocketPart DEFAULT_BOTTOM = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.default_bottom"))
            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.BOTTOM)
            .build();

    public static final RocketPart ADVANCED_CONE = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.advanced_cone"))
            .travelPredicate(ConstantTravelPredicateType.INSTANCE.configure(new AccessTypeTravelPredicateConfig(TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.CONE)
            .build();

    public static final RocketPart SLOPED_CONE = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.cone_sloped"))
            .travelPredicate(ConstantTravelPredicateType.INSTANCE.configure(new AccessTypeTravelPredicateConfig(TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.CONE)
            .build();

    public static final RocketPart BOOSTER_TIER_1 = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.booster_1"))
            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(2, TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.BOOSTER)
            .build();

    public static final RocketPart BOOSTER_TIER_2 = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.booster_2"))
            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(3, TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.BOOSTER)
            .build();

    public static final RocketPart NO_UPGRADE = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.default_upgrade"))
            .travelPredicate(ConstantTravelPredicateType.INSTANCE.configure(new AccessTypeTravelPredicateConfig(TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.UPGRADE)
            .build();

    public static final RocketPart STORAGE_UPGRADE = RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft.storage_upgrade"))
            .travelPredicate(ConstantTravelPredicateType.INSTANCE.configure(new AccessTypeTravelPredicateConfig(TravelPredicateType.AccessType.PASS)))
            .type(RocketPartType.UPGRADE)
            .build();

    public static void register() {
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "default_cone"), DEFAULT_CONE);
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "default_body"), DEFAULT_BODY);
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "default_fin"), DEFAULT_FIN);
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "default_booster"), NO_BOOSTER);
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "default_bottom"), DEFAULT_BOTTOM);
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "advanced_cone"), ADVANCED_CONE);
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "sloped_cone"), SLOPED_CONE);
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "booster_1"), BOOSTER_TIER_1);
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "booster_2"), BOOSTER_TIER_2);
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "default_upgrade"), NO_UPGRADE);
        Registry.register(AddonRegistry.ROCKET_PART, new Identifier(Constant.MOD_ID, "storage_upgrade"), STORAGE_UPGRADE);
    }

    @NotNull
    public static RocketPart getDefaultPartForType(RocketPartType type) {
        return switch (type) {
            case BODY -> DEFAULT_BODY;
            case CONE -> DEFAULT_CONE;
            case FIN -> DEFAULT_FIN;
            case BOTTOM -> DEFAULT_BOTTOM;
            case BOOSTER -> NO_BOOSTER;
            case UPGRADE -> NO_UPGRADE;
        };
    }

    @NotNull
    public static Identifier getDefaultPartIdForType(RocketPartType type) {
        return switch (type) {
            case BODY -> new Identifier(Constant.MOD_ID, "default_body");
            case CONE -> new Identifier(Constant.MOD_ID, "default_cone");
            case FIN -> new Identifier(Constant.MOD_ID, "default_fin");
            case BOTTOM -> new Identifier(Constant.MOD_ID, "default_bottom");
            case BOOSTER -> new Identifier(Constant.MOD_ID, "default_booster");
            case UPGRADE -> new Identifier(Constant.MOD_ID, "default_upgrade");
        };
    }

    @NotNull
    @Environment(EnvType.CLIENT)
    public static RocketPartRenderer getPartToRenderForType(DynamicRegistryManager manager, @NotNull RocketPartType type) {
        return RocketPartRendererRegistry.INSTANCE.getRenderer(manager.get(AddonRegistry.ROCKET_PART_KEY).getId(getDefaultPartForType(type)));
    }

    public static List<RocketPart> getUnlockedParts(PlayerEntity player, RocketPartType type) {
        List<RocketPart> parts = new LinkedList<>();
        Registry<RocketPart> registry = RocketPart.getRegistry(player.world.getRegistryManager());
        for (RocketPart part : registry) {
            if (part.type() == type && part.isUnlocked(player)) {
                parts.add(part);
            }
        }
        return parts;
    }

    public static List<RocketPart> getUnlockedParts(PlayerEntity player) {
        List<RocketPart> parts = new LinkedList<>();
        Registry<RocketPart> registry = RocketPart.getRegistry(player.world.getRegistryManager());
        for (RocketPart part : registry) {
            if (part.isUnlocked(player)) {
                parts.add(part);
            }
        }
        return parts;
    }
}
