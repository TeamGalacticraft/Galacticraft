package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.Galacticraft;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.util.shape.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(targets = "net/minecraft/structure/pool/StructurePoolBasedGenerator$StructurePoolGenerator")
public class StructurePoolGeneratorMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"), method = "generatePiece", remap = false)
    private void extraDebugInfoGC(PoolStructurePiece piece, MutableObject<VoxelShape> mutableObject, int minY, int currentSize, boolean bl, CallbackInfo ci) {
        if (Galacticraft.configManager.get().isDebugLogEnabled()) {
            Galacticraft.logger.warn("Pool referencer: {}", piece.toString());
        }
    }
}
