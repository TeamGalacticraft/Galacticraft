package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.api.block.PipeShapedBlock;
import dev.galacticraft.mod.compat.omnishape.OmnishapeCompat;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.omnishape.api.OmnishapeData;
import dev.omnishape.registry.OmnishapeBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.galacticraft.mod.api.block.PipeShapedBlock.preciseHit;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Inject(
            method = "destroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!OmnishapeCompat.isLoaded()) return;
        Minecraft mc = Minecraft.getInstance();

        Level level = mc.level;
        Player player = mc.player;

        BlockEntity be = level.getBlockEntity(pos);
        BlockState bs = level.getBlockState(pos);
        if (!(bs.getBlock() instanceof PipeShapedBlock<?> pipe)) return;
        if (!(be instanceof WireBlockEntity wire) || !wire.hasOverlay()) return;

        // Perform server-side ray trace to determine if breaking frame
        BlockHitResult hitResult = (BlockHitResult) player.pick(5.0, 0.0F, false);
        if (!hitResult.getBlockPos().equals(pos)) return;

        Vec3 localHit = hitResult.getLocation().subtract(Vec3.atLowerCornerOf(pos));

        VoxelShape wireShape = pipe.makeShape(wire);
        VoxelShape frameShape = wire.getOrCreateHitbox(wire.getRotationMatrix());

        boolean hitWire = preciseHit(wireShape, localHit);
        boolean hitFrame = !hitWire && preciseHit(frameShape, localHit);

        if (hitFrame) {
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