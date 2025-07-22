package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Inject(
            method = "destroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        Player player = mc.player;
        if (level == null || player == null) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof WireBlockEntity wire && wire.hasOverlay()) {
            // Use your cached hit result here to determine if breaking wire or frame
            if (wire.isBreakingFrame()) {
                // Only break frame overlay
                //level.setBlock(pos, wire.getOverlay().camouflage(), 3);
                wire.setOverlay(null);
                cir.setReturnValue(true);
            } else {
                // Break full block (wire + frame)
                level.removeBlock(pos, false);
                cir.setReturnValue(true);
            }
        }
    }
}