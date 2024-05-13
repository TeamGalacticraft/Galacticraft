/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.api.pipe;

import dev.galacticraft.mod.api.block.entity.Colored;
import dev.galacticraft.mod.api.block.entity.Connected;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface Pipe extends Connected, Colored {
    /**
     * Sets the {@link PipeNetwork} associated with this pipe
     * @param network The network to associate with
     */
    void setNetwork(@Nullable PipeNetwork network);

    /**
     * Returns the associated {@link PipeNetwork}
     * @return The associated {@link PipeNetwork}
     */
    @Contract(pure = true)
    @Nullable PipeNetwork getNetwork();

    /**
     * Returns whether this pipe is able to connect to another block on the specified face/direction
     * @param direction the direction offset to the block to check adjacency to
     * @return Whether this pipe is able to connect to another block on the specified face/direction
     */
    default boolean canConnect(Direction direction) {
        return true;
    }

    Storage<FluidVariant> getInsertable();

    /**
     * Returns the maximum amount of fluid allowed to be transferred through this pipe.
     * @return the maximum amount of fluid allowed to be transferred through this pipe.
     */
    long getMaxTransferRate();

    void forceCreateNetwork();
}
