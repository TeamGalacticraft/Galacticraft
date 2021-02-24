/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.api.rocket.part;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.regisry.AddonRegistry;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class GCRocketParts {
    public static final RocketPart DEFAULT_CONE = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_cone"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "default_cone"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_cone"))
            .type(RocketPartType.CONE)
            .renderState(GalacticraftBlocks.ROCKET_CONE_BASIC_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart DEFAULT_BODY = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_body"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "default_body"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_body"))
            .type(RocketPartType.BODY)
            .renderState(GalacticraftBlocks.ROCKET_BODY_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart DEFAULT_FIN = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_fin"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "default_fin"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_fin"))
            .type(RocketPartType.FIN)
            .renderState(GalacticraftBlocks.ROCKET_FINS_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart NO_BOOSTER = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_booster"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "default_booster"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_booster"))
            .type(RocketPartType.BOOSTER)
            .renderState(Blocks.AIR.getDefaultState())
            .renderItem(new ItemStack(Items.BARRIER))
            .recipe(false)
            .build());

    public static final RocketPart DEFAULT_BOTTOM = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_bottom"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "default_bottom"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_bottom"))
            .type(RocketPartType.BOTTOM)
            .renderState(GalacticraftBlocks.ROCKET_BOTTOM_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart ADVANCED_CONE = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "advanced_cone"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "advanced_cone"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.advanced_cone"))
            .type(RocketPartType.CONE)
            .renderState(GalacticraftBlocks.ROCKET_CONE_ADVANCED_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart SLOPED_CONE = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "sloped_cone"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "sloped_cone"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.cone_sloped"))
            .type(RocketPartType.CONE)
            .renderState(GalacticraftBlocks.ROCKET_CONE_SLOPED_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart BOOSTER_TIER_1 = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "booster_1"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "booster_1"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.booster_1"))
            .type(RocketPartType.BOOSTER)
            .renderState(GalacticraftBlocks.ROCKET_BOOSTER_TIER_1_RENDER_BLOCK.getDefaultState())
            .tier(1)
            .build());

    public static final RocketPart BOOSTER_TIER_2 = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "booster_2"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "booster_2"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.booster_2"))
            .type(RocketPartType.BOOSTER)
            .renderState(GalacticraftBlocks.ROCKET_BOOSTER_TIER_2_RENDER_BLOCK.getDefaultState())
            .tier(2)
            .build());

    public static final RocketPart NO_UPGRADE = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_upgrade"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "default_upgrade"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_upgrade"))
            .type(RocketPartType.UPGRADE)
            .renderState(Blocks.AIR.getDefaultState())
            .renderItem(new ItemStack(Items.BARRIER))
            .build());

    public static final RocketPart STORAGE_UPGRADE = Registry.register(AddonRegistry.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "storage_upgrade"), RocketPart.Builder.create(new Identifier(Constants.MOD_ID, "storage_upgrade"))
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.storage_upgrade"))
            .type(RocketPartType.UPGRADE)
            .renderState(Blocks.AIR.getDefaultState())
            .renderItem(new ItemStack(Items.CHEST))
            .build());

    public static void register() {
    }

    @NotNull
    public static RocketPart getDefaultPartForType(RocketPartType type) {
        switch (type) {
            case BODY:
                return DEFAULT_BODY;
            case CONE:
                return DEFAULT_CONE;
            case FIN:
                return DEFAULT_FIN;
            case BOTTOM:
                return DEFAULT_BOTTOM;
            case BOOSTER:
                return NO_BOOSTER;
            case UPGRADE:
                return NO_UPGRADE;
            default:
                throw new IllegalArgumentException("invalid part type");
        }
    }

    @NotNull
    public static RocketPart getPartToRenderForType(RocketPartType type) {
        switch (type) {
            case BODY:
                return DEFAULT_BODY;
            case CONE:
                return DEFAULT_CONE;
            case FIN:
                return DEFAULT_FIN;
            case BOTTOM:
                return DEFAULT_BOTTOM;
            case BOOSTER:
                return BOOSTER_TIER_1;
            case UPGRADE:
                return STORAGE_UPGRADE;
            default:
                throw new IllegalArgumentException("invalid part type");
        }
    }

    public static List<RocketPart> getUnlockedParts(PlayerEntity player, RocketPartType type) {
        List<RocketPart> parts = new LinkedList<>();
        for (RocketPart part : AddonRegistry.ROCKET_PARTS) {
            if (part.getType() == type && part.isUnlocked(player)) {
                parts.add(part);
            }
        }
        return parts;
    }

    public static List<RocketPart> getUnlockedParts(PlayerEntity player) {
        List<RocketPart> parts = new LinkedList<>();
        for (RocketPart part : AddonRegistry.ROCKET_PARTS) {
            if (part.isUnlocked(player)) {
                parts.add(part);
            }
        }
        return parts;
    }
}
