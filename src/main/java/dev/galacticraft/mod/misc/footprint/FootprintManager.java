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

package dev.galacticraft.mod.misc.footprint;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class FootprintManager {
    public List<GlobalPos> footprintBlockChanges = Lists.newArrayList();
    public Long2ObjectMap<List<Footprint>> globalFootprints = new Long2ObjectOpenHashMap<>();

    public void tick(Level level, long packedPos) {
        if (level.getGameTime() % 20 == 0) {

            Long2ObjectMap<List<Footprint>> footprintMap = globalFootprints;
            if (footprintMap != null) {
                List<Footprint> footprints = footprintMap.get(packedPos);

                if (footprints != null) {
                    List<Footprint> toRemove = new ArrayList<>();

                    for (Footprint footprint : footprints) {
                        footprint.age += (short)20;

                        if (footprint.age >= Footprint.MAX_AGE) {
                            toRemove.add(footprint);
                        }
                    }

                    if (!toRemove.isEmpty()) {
                        footprints.removeAll(toRemove);
                    }

                    footprintMap.put(packedPos, footprints);

                    onChange(level, packedPos, footprints);
                }
            }
        }
    }

    public void onChange(Level level, long pos, List<Footprint> footprints) {
    }

    public void addFootprint(long packedPos, Footprint footprint) {
        List<Footprint> footprints = globalFootprints.computeIfAbsent(packedPos, key -> new ArrayList<>());

        footprints.add(footprint);
    }

    public Long2ObjectMap<List<Footprint>> getFootprints() {
        return globalFootprints;
    }
}
