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

    private boolean hasRunOnceForWorldReload = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(BooleanSupplier booleanSupplier_1, CallbackInfo ci) {
        if (!hasRunOnceForWorldReload) {
            hasRunOnceForWorldReload = true;
            WireNetwork.blockPlaced(); //Runs at the end of tick() - BE's should've ticked, meaning there are wires in the map... right?
        }
        WireNetwork.networkMap.forEach((wireNetwork, blockPos) -> wireNetwork.update());
    }

    public void close() {
        WireNetwork.networkMap.clear();
        hasRunOnceForWorldReload = false;
    }
}