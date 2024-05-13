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

package dev.galacticraft.mod.village;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.biome.GCBiomes;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerTypeHelper;
import net.minecraft.world.entity.npc.VillagerType;

import java.util.LinkedList;
import java.util.List;

public class MoonVillagerTypes {
    public static final List<VillagerType> MOON_VILLAGER_TYPE_REGISTRY = new LinkedList<>();

    public static final VillagerType MOON_HIGHLANDS = VillagerTypeHelper.register(Constant.id("moon_highlands"));
//    public static final VillagerType MOON_HIGHLANDS_ROCKS = VillagerTypeHelper.register(Constant.id("moon_highlands_edge"));

    public static void register() {
        VillagerType.BY_BIOME.put(GCBiomes.Moon.LUNAR_HIGHLANDS, MOON_HIGHLANDS);
        MOON_VILLAGER_TYPE_REGISTRY.add(MOON_HIGHLANDS);
//        MOON_VILLAGER_TYPE_REGISTRY.add(MOON_HIGHLANDS_ROCKS);
    }
}
