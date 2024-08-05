package dev.galacticraft.impl.internal.mixin.gravity;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityGravityMixin {
    @Shadow protected abstract double getDefaultGravity();

    @Redirect(method = "getGravity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDefaultGravity()D"))
    private double replaceGravity(Entity instance) {
        return instance.level().galacticraft$getCelestialBody().value().gravity() * this.getDefaultGravity();
    }
}
