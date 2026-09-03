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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Locale;
import java.util.Objects;

public record DungeonConnector(
        BlockPos localPosition,
        Direction direction,
        DungeonConnectorType type,
        String name
) {
    public static final String PREFIX = "gc_connector:";

    public DungeonConnector {
        Objects.requireNonNull(localPosition, "localPosition");
        Objects.requireNonNull(direction, "direction");
        Objects.requireNonNull(type, "type");

        name = name == null ? "" : name.toLowerCase(Locale.ROOT);
    }

    public boolean isNamed(String name) {
        return this.name.equalsIgnoreCase(name);
    }

    public boolean isNamed() {
        return !this.name.isEmpty();
    }

    /**
     * Parses:
     *
     * gc_connector:standard:north
     * gc_connector:standard:north:name
     */
    public static DungeonConnector parse(BlockPos position, String metadata) {
        if (!metadata.startsWith(PREFIX)) {
            throw new IllegalArgumentException(
                    "Not a dungeon connector marker: " + metadata
            );
        }

        String[] parts = metadata.split(":", 4);

        if (parts.length < 3) {
            throw new IllegalArgumentException(
                    "Invalid dungeon connector marker: " + metadata
            );
        }

        DungeonConnectorType type = DungeonConnectorType.fromId(parts[1]);

        Direction direction = Direction.byName(parts[2]);
        if (direction == null) {
            throw new IllegalArgumentException(
                    "Unknown connector direction '" + parts[2] + "' in " + metadata
            );
        }

        String name = parts.length >= 4 ? parts[3] : "";

        return new DungeonConnector(
                position.immutable(),
                direction,
                type,
                name
        );
    }
}