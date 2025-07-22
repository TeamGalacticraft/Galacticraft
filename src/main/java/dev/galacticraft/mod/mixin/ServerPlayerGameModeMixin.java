package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.omnishape.api.OmnishapeData;
import dev.omnishape.registry.OmnishapeBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow protected ServerLevel level;

    @Inject(
            method = "destroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ServerLevel level = this.level;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof WireBlockEntity wire && wire.hasOverlay()) {
            if (wire.isBreakingFrame()) {
                ItemStack frameStack = new ItemStack(OmnishapeBlocks.FRAME_BLOCK);
                OmnishapeData.writeToItem(frameStack, wire.getOverlay());
                Block.popResource(level, pos, frameStack);

                // Restore wire-only blockstate (removing overlay)
                BlockState newState = wire.getBlockState();
                wire.setOverlay(null);
                level.sendBlockUpdated(pos, wire.getBlockState(), wire.getBlockState(), 3);
                //level.setBlock(pos, newState, 3);
                cir.setReturnValue(true);
            }
            // If breaking wire, allow normal destroyBlock logic to proceed
        }
    }
}