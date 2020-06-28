package com.hrznstudio.galacticraft.api.rocket.part;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;

public class RocketParts {
    public static final RocketPart DEFAULT_CONE = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_cone"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_cone"))
            .type(RocketPartType.CONE)
            .renderState(GalacticraftBlocks.ROCKET_CONE_BASIC_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart DEFAULT_BODY = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_body"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_body"))
            .type(RocketPartType.BODY)
            .renderState(GalacticraftBlocks.ROCKET_BODY_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart DEFAULT_FIN = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_fin"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_fin"))
            .type(RocketPartType.FIN)
            .renderState(GalacticraftBlocks.ROCKET_FINS_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart NO_BOOSTER = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_booster"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_booster"))
            .type(RocketPartType.BOOSTER)
            .renderState(Blocks.AIR.getDefaultState())
            .renderItem(new ItemStack(Items.BARRIER))
            .recipe(false)
            .build());

    public static final RocketPart DEFAULT_BOTTOM = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_bottom"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_bottom"))
            .type(RocketPartType.BOTTOM)
            .renderState(GalacticraftBlocks.ROCKET_BOTTOM_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart ADVANCED_CONE = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "advanced_cone"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.advanced_cone"))
            .type(RocketPartType.CONE)
            .renderState(GalacticraftBlocks.ROCKET_CONE_ADVANCED_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart SLOPED_CONE = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "sloped_cone"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.cone_sloped"))
            .type(RocketPartType.CONE)
            .renderState(GalacticraftBlocks.ROCKET_CONE_SLOPED_RENDER_BLOCK.getDefaultState())
            .build());

    public static final RocketPart BOOSTER_TIER_1 = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "booster_1"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.booster_1"))
            .type(RocketPartType.BOOSTER)
            .renderState(GalacticraftBlocks.ROCKET_BOOSTER_TIER_1_RENDER_BLOCK.getDefaultState())
            .tier((parts) -> 1)
            .build());

    public static final RocketPart BOOSTER_TIER_2 = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "booster_2"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.booster_2"))
            .type(RocketPartType.BOOSTER)
            .renderState(GalacticraftBlocks.ROCKET_BOOSTER_TIER_2_RENDER_BLOCK.getDefaultState())
            .tier((parts) -> 2)
            .build());

    public static final RocketPart NO_UPGRADE = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_upgrade"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.default_upgrade"))
            .type(RocketPartType.UPGRADE)
            .renderState(Blocks.AIR.getDefaultState())
            .renderItem(new ItemStack(Items.BARRIER))
            .build());

    public static final RocketPart STORAGE_UPGRADE = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "storage_upgrade"), RocketPart.Builder.create()
            .name(new TranslatableText("rocket_part.galacticraft-rewoven.storage_upgrade"))
            .type(RocketPartType.UPGRADE)
            .renderState(Blocks.AIR.getDefaultState())
            .renderItem(new ItemStack(Items.CHEST))
            .build());

    public static void register() {
    }

    @Nonnull
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

    @Nonnull
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
}
