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

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public record DungeonPiecePool(List<DungeonPieceDefinition> pieces) {
    public DungeonPiecePool(List<DungeonPieceDefinition> pieces) {
        this.pieces = List.copyOf(pieces);
    }

    public static DungeonPiecePool of(DungeonPieceDefinition... pieces) {
        return new DungeonPiecePool(Arrays.asList(pieces));
    }

    public List<DungeonPieceDefinition> weightedOrder(
            RandomSource random,
            Predicate<DungeonPieceDefinition> predicate
    ) {
        List<DungeonPieceDefinition> remaining = new ArrayList<>();

        for (DungeonPieceDefinition definition : this.pieces) {
            if (predicate.test(definition)) {
                remaining.add(definition);
            }
        }

        List<DungeonPieceDefinition> result = new ArrayList<>(remaining.size());

        while (!remaining.isEmpty()) {
            int totalWeight = 0;

            for (DungeonPieceDefinition definition : remaining) {
                totalWeight += definition.weight();
            }

            int selected = random.nextInt(totalWeight);

            DungeonPieceDefinition chosen = null;

            for (DungeonPieceDefinition definition : remaining) {
                selected -= definition.weight();

                if (selected < 0) {
                    chosen = definition;
                    break;
                }
            }

            if (chosen == null) {
                throw new IllegalStateException("Failed weighted dungeon-piece selection");
            }

            result.add(chosen);
            remaining.remove(chosen);
        }

        return result;
    }
}