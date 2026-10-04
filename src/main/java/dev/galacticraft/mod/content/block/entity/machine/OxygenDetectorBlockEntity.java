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

package dev.galacticraft.mod.content.block.entity.machine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.machine.configuration.IOFace;
import dev.galacticraft.machinelib.api.machine.configuration.SecuritySettings;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.transfer.ResourceType;
import dev.galacticraft.machinelib.api.util.BlockFace;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.screen.OxygenDetectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class OxygenDetectorBlockEntity extends MachineBlockEntity {
    private boolean andMode = false;
    private boolean oxygenWorld = false;

    public OxygenDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.OXYGEN_DETECTOR, pos, state, StorageSpec.empty());
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        this.oxygenWorld = level.getDefaultBreathable();
    }

    public boolean canAccess(Player player) {
        SecuritySettings settings = getSecurity();

        settings.tryUpdate(player.getUUID());
        return settings.hasAccess(player);
    }

    public void setMode(boolean isAnd) {
        if (andMode != isAnd) {
            andMode = isAnd;
            this.setChanged();

            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public boolean isAnd() {
        return andMode;
    }

    @Override
    public boolean faceHasOverride(BlockFace face) {
        return true;
    }

    private boolean isFaceActive(Direction direction) {
        IOFace option = getIOConfig().get(BlockFace.from(this.getBlockState(), direction));
        return option.getType() != ResourceType.OVERRIDE; // Override disables detecting oxygen. (and switches to base texture)
    }

    private boolean isOxygenPresent(Direction direction) {
        BlockPos pos = this.worldPosition.relative(direction);
        boolean isFree = !this.level.getBlockState(pos).isSolidRender(this.level, pos);

        return isFree && (oxygenWorld || this.level.isBreathable(pos));
    }

    private boolean isOxygenPresent() {
        boolean isAnyActive = false;
        for (Direction direction : Direction.values()) {
            if (!isFaceActive(direction)) continue;
            isAnyActive = true; // For preventing unnecessary call

            if (!andMode && isOxygenPresent(direction)) return true;
            if (andMode && !isOxygenPresent(direction)) return false;
        }

        return isAnd() && isAnyActive; // After loop ends, AND mode needs to return true unlike OR mode.
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider lookup) {
        super.saveAdditional(tag, lookup);

        tag.putBoolean("and", andMode);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider lookup) {
        super.loadAdditional(tag, lookup);

        andMode = tag.getBoolean("and");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(Provider registryLookup) {
        CompoundTag tag = super.getUpdateTag(registryLookup);
        tag.putBoolean("and", andMode);

        return tag;
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state,
            @NotNull ProfilerFiller profiler) {
        return isOxygenPresent() ? MachineStatuses.ACTIVE : MachineStatuses.IDLE;
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inventory,
            Player player) {
        return new OxygenDetectorMenu(syncId, player, this);
    }
}
