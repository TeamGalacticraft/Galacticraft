/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.screen.slot;

import com.google.common.base.Preconditions;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.lookup.filter.FluidFilter;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record FluidTankSettings(int x, int y, @NotNull SlotType<FluidVariant> type, @NotNull FluidFilter filter, boolean canInsertFluids,
                                boolean canExtractFluids, long capacity, int size,
                                @NotNull FluidTankSettings.TankChangeListener extractionListener,
                                @NotNull FluidTankSettings.TankChangeListener insertionListener) {
    public static class Builder {
        private final int x;
        private final int y;
        private final SlotType<FluidVariant> type;
        private boolean canInsertFluids = true;
        private boolean canExtractFluids = true;
        private long capacity = 64;
        private @NotNull FluidTankSettings.TankChangeListener extractionListener = TankChangeListener.DO_NOTHING;
        private @NotNull FluidTankSettings.TankChangeListener insertionListener = TankChangeListener.DO_NOTHING;
        private @NotNull FluidFilter filter = Constant.Filter.Fluid.ALWAYS;
        private int size = 48;

        private Builder(int x, int y, @NotNull SlotType<FluidVariant> type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        @Contract("_, _, _ -> new")
        public static @NotNull Builder create(int x, int y, @NotNull SlotType type) {
            Preconditions.checkNotNull(type);
            return new Builder(x, y, type);
        }

        public Builder filter(@NotNull FluidFilter filter) {
            this.filter = filter;
            return this;
        }

        public Builder disableInput() {
            this.canInsertFluids = false;
            return this;
        }

        public Builder disableOutput() {
            this.canExtractFluids = false;
            return this;
        }

        public Builder capacity(long capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder size(int size) {
            assert size > 0 && size <= 48;
            return this;
        }

        public Builder onExtract(@NotNull FluidTankSettings.TankChangeListener listener) {
            this.extractionListener = listener;
            return this;
        }

        public Builder onInsert(@NotNull FluidTankSettings.TankChangeListener listener) {
            this.insertionListener = listener;
            return this;
        }

        public FluidTankSettings build() {
            return new FluidTankSettings(this.x, this.y, this.type, this.filter, this.canInsertFluids, this.canExtractFluids, this.capacity, this.size, this.extractionListener, this.insertionListener);
        }
    }

    @FunctionalInterface
    public interface TankChangeListener {
        @NotNull TankChangeListener DO_NOTHING = (player, stack) -> {
        };

        void onChange(@Nullable PlayerEntity player, ItemStack stack);
    }
}
