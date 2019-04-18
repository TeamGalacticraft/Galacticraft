package io.github.teamgalacticraft.galacticraft.mixin;

import io.github.teamgalacticraft.galacticraft.api.world.dimension.CustomGravityDimension;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends LivingEntity {

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    @Inject(method = "getJumpVelocity", at = @At("RETURN"))
    public float getJumpVelocity(CallbackInfo ci) {
        if (this.world.dimension instanceof CustomGravityDimension) {
            return ((CustomGravityDimension)dimension).getGravity();
        }
        return 0.42f;
    }
}
