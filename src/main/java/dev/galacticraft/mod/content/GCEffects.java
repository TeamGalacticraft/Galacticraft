package dev.galacticraft.mod.content;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.effects.MoonTangleTranceEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.Registry;

public final class GCEffects {
    public static final Holder<MobEffect> MOON_TANGLE_TRANCE =
            Registry.registerForHolder(
                    BuiltInRegistries.MOB_EFFECT,
                    ResourceLocation.fromNamespaceAndPath(Constant.MOD_ID, "moon_tangle_trance"),
                    new MoonTangleTranceEffect()
            );

    private GCEffects() {}

    public static void init() { /* trigger <clinit> */ }
}