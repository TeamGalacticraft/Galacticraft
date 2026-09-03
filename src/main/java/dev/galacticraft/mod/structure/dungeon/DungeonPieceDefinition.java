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

package dev.galacticraft.mod.structure.dungeon;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record DungeonPieceDefinition(
        ResourceLocation template,
        DungeonWing wing,
        DungeonPieceCategory category,
        int weight,
        int maxPerDungeon,
        int minimumDepth,
        int maximumDepth,
        boolean allowSameCategoryConsecutively
) {
    public DungeonPieceDefinition {
        Objects.requireNonNull(template, "template");
        Objects.requireNonNull(wing, "wing");
        Objects.requireNonNull(category, "category");

        if (weight <= 0) {
            throw new IllegalArgumentException("weight must be > 0");
        }

        if (maxPerDungeon < 0) {
            throw new IllegalArgumentException("maxPerDungeon cannot be negative");
        }

        if (minimumDepth < 0) {
            throw new IllegalArgumentException("minimumDepth cannot be negative");
        }

        if (maximumDepth < minimumDepth) {
            throw new IllegalArgumentException(
                    "maximumDepth cannot be less than minimumDepth"
            );
        }
    }

    public boolean canGenerateAtDepth(int depth) {
        return depth >= this.minimumDepth && depth <= this.maximumDepth;
    }

    /**
     * A max of zero means unlimited.
     */
    public boolean hasGenerationLimit() {
        return this.maxPerDungeon > 0;
    }
}