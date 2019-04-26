package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.blocks.special.aluminumwire.WireNetwork;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(BooleanSupplier booleanSupplier_1, CallbackInfo ci) {
        AtomicInteger i = new AtomicInteger();
        WireNetwork.networkMap.forEach((wireNetwork, blockPos) ->  { i.getAndIncrement(); wireNetwork.update(); });
        System.out.println("asdjkfkfajhkafdkhjfsdjkfd-ddddddddddddddd" + i);
    }
}