package io.github.teamgalacticraft.galacticraft.entity.damage;

import net.minecraft.entity.damage.DamageSource;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftDamageSource  extends DamageSource{
    public static final DamageSource VINE_POISON = new GalacticraftDamageSource("vine_poison");

    private GalacticraftDamageSource(String string_1) {
        super(string_1);
    }
}
