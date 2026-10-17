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

package dev.galacticraft.mod.compat;

import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

/**
 * The blocks that come in every {@link PipeColor}. Recipe viewers group or hide their colored variants,
 * so the item list isn't cluttered with 17 versions of each.
 */
public final class PipeColorVariants {
    public static final List<Map<PipeColor, Block>> FAMILIES = List.of(
            GCBlocks.GLASS_FLUID_PIPES,
            GCBlocks.SEALABLE_FLUID_PIPES,
            GCBlocks.FLUID_PIPE_WALKWAYS
    );

    private PipeColorVariants() {
    }

    /**
     * {@return all colored variants, so every block except the clear one of each family}
     */
    public static List<Block> colored() {
        return FAMILIES.stream()
                .flatMap(family -> family.entrySet().stream())
                .filter(entry -> entry.getKey() != PipeColor.CLEAR)
                .map(Map.Entry::getValue)
                .toList();
    }
}
