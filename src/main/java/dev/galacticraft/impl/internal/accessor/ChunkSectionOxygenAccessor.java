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

package dev.galacticraft.impl.internal.accessor;

import dev.galacticraft.impl.network.s2c.OxygenUpdatePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;

@ApiStatus.Internal
public interface ChunkSectionOxygenAccessor {
    Iterator<BlockPos> galacticraft$get(int x, int y, int z);
    boolean galacticraft$has(int x, int y, int z, BlockPos pos);
    void galacticraft$ensureSpaceFor(BlockPos pos);
    void galacticraft$add(int x, int y, int z, BlockPos pos);
    void galacticraft$removeAll(BlockPos pos);
    void galacticraft$deallocate(BlockPos pos);
    void galacticraft$remove(int x, int y, int z, BlockPos pos);

    boolean galacticraft$isEmpty();

    void galacticraft$writeTag(CompoundTag apiTag);
    void galacticraft$readTag(CompoundTag apiTag);

    OxygenUpdatePayload.OxygenSectionData galacticraft$updatePayload();

    void galacticraft$loadData(OxygenUpdatePayload.OxygenSectionData data);
}
