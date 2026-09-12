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
import net.minecraft.world.level.block.state.BlockState;

public class OxygenDetectorBlockEntity extends MachineBlockEntity {
    private boolean AND = false;
    private boolean oxygenWorld = false;

    public OxygenDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.OXYGEN_DETECTOR, pos, state, StorageSpec.empty());
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

    private void sendUpdate() {
        setChanged(); // only for marking dirty, nothing to send.

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                worldPosition,
                getBlockState(),
                getBlockState(),
                3
            );
        }
    }

    public void setMode(boolean isAnd) {
        AND = isAnd;
        sendUpdate();
    }

    public boolean getMode() {
        return AND;
    }

    public boolean isAnd() {
        return AND;
    }

    public boolean isFaceActive(Direction direction) {
        IOFace option = getIOConfig().get(BlockFace.from(this.getBlockState(), direction));
        return option.getType() != ResourceType.NONE;
    }

    public boolean isAnyFaceActive() {
        for (Direction direction : Direction.values()) {
            if (isFaceActive(direction)) return true;
        }
        return false;
    }

    public boolean isOxygenPresent(Direction direction) {
        BlockPos pos = this.worldPosition.relative(direction);

        return this.level.isBreathable(pos) && !this.level.getBlockState(pos).isSolidRender(this.level, pos);
    }

    public boolean isOxygenPresent() {
        if (oxygenWorld) return true;

        for (Direction direction : Direction.values()) {
            if (!isFaceActive(direction)) continue;

            if (!AND && isOxygenPresent(direction)) return true;
            if (AND && !isOxygenPresent(direction)) return false;
        }

        return isAnd() && isAnyFaceActive(); // After loop ends, AND mode needs to return true unlike OR mode.
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider lookup) {
        super.saveAdditional(tag, lookup);

        tag.putBoolean("and", AND);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider lookup) {
        super.loadAdditional(tag, lookup);

        AND = tag.getBoolean("and");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(Provider registryLookup) {
        CompoundTag tag = super.getUpdateTag(registryLookup);
        tag.putBoolean("and", AND);

        return tag;
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state,
            @NotNull ProfilerFiller profiler) {
        return null;
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inventory,
            Player player) {
        return new OxygenDetectorMenu(syncId, player, this);
    }
}
