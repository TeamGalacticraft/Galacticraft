package dev.galacticraft.mod.content.effects;

import dev.galacticraft.mod.content.GCEffects;
import dev.galacticraft.mod.content.TranceSystems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MoonTangleTranceEffect extends MobEffect {
    public MoonTangleTranceEffect() {
        // NEUTRAL category, purple-ish UI color. Tweak color as you like.
        super(MobEffectCategory.NEUTRAL, 0x8C2BEA);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
        if (entity instanceof ServerPlayer sp) {
            sp.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 60, 0, true, true, true));
            TranceSystems.beginNausea(sp);
        }
    }
}