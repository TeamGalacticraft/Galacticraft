package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.world.gen.feature.features.DeferredBlockPlacement;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @Inject(method = "onChunkReadyToSend", at = @At("TAIL"))
    private void flushDeferredBlocks(LevelChunk chunk, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) chunk.getLevel();
        DeferredBlockPlacement.flush(level, chunk.getPos());
    }
}
