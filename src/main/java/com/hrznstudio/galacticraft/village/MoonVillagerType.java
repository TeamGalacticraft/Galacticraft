/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.village;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerTypeHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerType;

public class MoonVillagerType {
    public static final VillagerType MOON_HIGHLANDS = VillagerTypeHelper.register(new Identifier(Constants.MOD_ID, "moon_highlands"));
    public static final VillagerType MOON_HIGHLANDS_ROCKS = VillagerTypeHelper.register(new Identifier(Constants.MOD_ID, "moon_highlands_rocks"));

    public static void register() {
        VillagerTypeHelper.addVillagerTypeToBiome(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS, MOON_HIGHLANDS);
        VillagerTypeHelper.addVillagerTypeToBiome(GalacticraftBiomes.Moon.HIGHLANDS_ROCKS, MOON_HIGHLANDS_ROCKS);
        VillagerTypeHelper.addVillagerTypeToBiome(GalacticraftBiomes.Moon.HIGHLANDS_VALLEY, MOON_HIGHLANDS_ROCKS);
    }
}
