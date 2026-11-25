/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.galacticraft.mod.accessor.GCLevelAccessor;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import dev.galacticraft.mod.misc.footprint.ServerFootprintManager;
import dev.galacticraft.mod.tag.GCDimensionTypeTags;
import dev.galacticraft.mod.world.dimension.GCDimensions;
import dev.galacticraft.mod.world.gen.spawner.EvolvedPillagerSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerLevel.class)
abstract class ServerLevelMixin extends Level implements GCLevelAccessor {
    @SuppressWarnings("ConstantConditions")
    private ServerLevelMixin() {
        super(null, null, null, null, null, false, false, 0, 0);
    }

    @Shadow
    @Final
    @Mutable
    private List<CustomSpawner> customSpawners;

    @Shadow
    public abstract @NotNull MinecraftServer getServer();

    @Unique
    private final FootprintManager galacticraft$footprintManager = new ServerFootprintManager();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void setSpawnersGC(CallbackInfo ci, @Local(argsOnly = true) ResourceKey<Level> worldKey) {
        if (worldKey.equals(GCDimensions.MOON)) {
            this.customSpawners = ImmutableList.<CustomSpawner>builder().add(new EvolvedPillagerSpawner()).build();
        }
    }

    @Inject(method = "sendBlockUpdated", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", remap = false))
    private void onBlockChanges(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
        // Skip if both old and new states are solid blocks
        if (oldState.isSolid() && newState.isSolid()) {
            return;
        }

        // Check if the block is a leaf (for oxygen collection logic)
        if (newState.is(BlockTags.LEAVES)) {
            // Oxygen collector code update (if needed)
        }
    }

    @Inject(method = "tickChunk", at = @At("HEAD"))
    private void tickFootprints(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        var profiler = getProfiler();
        profiler.push("footprints");
        this.galacticraft$footprintManager.tick((ServerLevel) (Object) this, chunk.getPos().toLong());
        profiler.pop();
    }

    @Override
    public FootprintManager galacticraft$getFootprintManager() {
        return this.galacticraft$footprintManager;
    }

    @WrapMethod(method = "advanceWeatherCycle")
    private void galacticraft$disableWeatherCycle(Operation<Void> original) {
        if (this.galacticraft$hasDimensionTypeTag(GCDimensionTypeTags.NO_WEATHER)) {
            if (this.rainLevel > 0.0F || this.thunderLevel > 0.0F) {
                this.oRainLevel = 0.0F;
                this.rainLevel = 0.0F;
                this.oThunderLevel = 0.0F;
                this.thunderLevel = 0.0F;
                PlayerList playerList = this.getServer().getPlayerList();
                playerList.broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F), this.dimension());
                playerList.broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
                playerList.broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
            }
        } else {
            original.call();
        }
    }
}