/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.content.item.StandardWrenchItem;
import dev.galacticraft.mod.misc.footprint.Footprint;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import dev.galacticraft.mod.tag.GCBlockTags;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviourMixin {
    @Shadow
    public abstract boolean is(TagKey<Block> tag);

    @Inject(method = "onRemove", at = @At("TAIL"))
    private void handleFootprints(Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if (is(GCBlockTags.FOOTPRINTS)) {
            FootprintManager footprintManager = level.galacticraft$getFootprintManager();
            Long2ObjectMap<List<Footprint>> footprintChunkMap = footprintManager.getFootprints();

            if (footprintChunkMap != null) {
                long chunkKey = ChunkPos.asLong(pos);
                List<Footprint> footprintList = footprintChunkMap.get(chunkKey);

                if (footprintList != null && !footprintList.isEmpty()) {
                    List<Footprint> toRemove = new ArrayList<>();

                    for (Footprint footprint : footprintList) {
                        if (footprint.position.x > pos.getX() && footprint.position.x < pos.getX() + 1 && footprint.position.z > pos.getZ() && footprint.position.z < pos.getZ() + 1) {
                            toRemove.add(footprint);
                        }
                    }

                    if (!toRemove.isEmpty()) {
                        footprintList.removeAll(toRemove);
                    }
                }
            }

            footprintManager.footprintBlockChanges.add(GlobalPos.of(level.dimension(), pos));
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void handleWrenching(ItemStack itemStack, Level level, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<ItemInteractionResult> cir) {
        if (itemStack.getItem() instanceof StandardWrenchItem) {
            // Prevent the default behaviour such as opening a GUI when right-clicking
            // on a block with a wrench to allow the wrench to rotate the block instead
            cir.setReturnValue(ItemInteractionResult.FAIL);
        }
    }
}
