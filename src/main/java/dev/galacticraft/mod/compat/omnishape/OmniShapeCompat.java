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

package dev.galacticraft.mod.compat.omnishape;

import dev.galacticraft.mod.content.GCBlocks;
import dev.omnishape.api.facade.FacadeRegistry;
import net.minecraft.world.level.block.Block;

public final class OmniShapeCompat {
    private OmniShapeCompat() {
    }

    public static void register() {
        // Register wires
        FacadeRegistry.register(GCBlocks.ALUMINUM_WIRE);
        FacadeRegistry.register(GCBlocks.HEAVY_ALUMINUM_WIRE);
        // Register all pipe colours
        for (Block pipe : GCBlocks.GLASS_FLUID_PIPES.values()) {
            FacadeRegistry.register(pipe);
        }
    }
}