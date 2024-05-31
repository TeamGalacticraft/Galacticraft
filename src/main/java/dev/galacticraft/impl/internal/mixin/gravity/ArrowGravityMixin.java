package dev.galacticraft.impl.internal.mixin.gravity;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractArrow.class)
public class ArrowGravityMixin {
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 0.05000000074505806)) // that's what it is in the bytecode
    private double galacticraft_changeArrowGravity(double defaultValue) {
        return CelestialBody.getByDimension(((Entity) (Object) this).level()).map(celestialBody -> celestialBody.gravity() * defaultValue).orElse(defaultValue);
    }
}
