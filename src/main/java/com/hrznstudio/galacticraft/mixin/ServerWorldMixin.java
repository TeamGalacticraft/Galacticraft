package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.blocks.special.aluminumwire.WireNetwork;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(BooleanSupplier booleanSupplier_1, CallbackInfo ci) {
        //System.out.println(Thread.currentThread().getName());
        WireNetwork.networkMap.forEach((wireNetwork, blockPos) -> wireNetwork.update());
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void close(CallbackInfo ci) {
        WireNetwork.networkMap.clear();
    }
}