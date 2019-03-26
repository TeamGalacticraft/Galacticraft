package io.github.teamgalacticraft.galacticraft.entity.damage;

import net.minecraft.entity.damage.DamageSource;

public class GCDamageSource extends DamageSource {
    public static final DamageSource VINE_POISON = new GCDamageSource("vine_poison");

    private GCDamageSource(String string_1) {
        super(string_1);
    }
}
