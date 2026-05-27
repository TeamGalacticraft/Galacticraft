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

package dev.galacticraft.mod.world.gen.spawner;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class MoonVillageVillagerSpawnData extends SavedData {
    private static final String ID = "galacticraft_moon_village_villagers";
    private static final String SEEDED_VILLAGES_TAG = "seeded_villages";
    private static final SavedData.Factory<MoonVillageVillagerSpawnData> FACTORY = new SavedData.Factory<>(
            MoonVillageVillagerSpawnData::new,
            (tag, registryLookup) -> MoonVillageVillagerSpawnData.load(tag),
            null
    );

    private final Set<Long> seededVillages = new HashSet<>();

    public static MoonVillageVillagerSpawnData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, ID);
    }

    private static MoonVillageVillagerSpawnData load(CompoundTag tag) {
        MoonVillageVillagerSpawnData data = new MoonVillageVillagerSpawnData();
        for (long villageKey : tag.getLongArray(SEEDED_VILLAGES_TAG)) {
            data.seededVillages.add(villageKey);
        }
        return data;
    }

    public boolean hasSeeded(long villageKey) {
        return this.seededVillages.contains(villageKey);
    }

    public void markSeeded(long villageKey) {
        if (this.seededVillages.add(villageKey)) {
            this.setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registryLookup) {
        tag.putLongArray(SEEDED_VILLAGES_TAG, this.seededVillages.stream().mapToLong(Long::longValue).toArray());
        return tag;
    }
}