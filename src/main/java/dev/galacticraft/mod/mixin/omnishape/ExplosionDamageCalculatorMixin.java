package dev.galacticraft.mod.mixin.omnishape;

import dev.galacticraft.mod.compat.omnishape.OmnishapeCompat;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ExplosionDamageCalculator.class)
public class ExplosionDamageCalculatorMixin {
    @Inject(method = "getBlockExplosionResistance", at = @At("HEAD"), cancellable = true)
    private void overrideCamoResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, FluidState fluid, CallbackInfoReturnable<Optional<Float>> cir) {
        if (!OmnishapeCompat.isLoaded()) return;

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof WireBlockEntity wire && wire.getOverlay() != null) {
            BlockState camo = wire.getOverlay().camouflage();
            float resistance = camo.getBlock().getExplosionResistance();
            cir.setReturnValue(Optional.of(Math.max(resistance, fluid.getExplosionResistance())));
        }
    }
}