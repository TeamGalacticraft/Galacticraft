/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.world.dimension;

import com.mojang.serialization.Lifecycle;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.tag.GalacticraftTag;
import java.util.OptionalLong;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftDimensionType {
    public static final ResourceKey<Level> MOON_KEY = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(Constant.MOD_ID, "moon"));
    public static final ResourceKey<DimensionType> MOON_DIMENSION_TYPE_KEY = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, MOON_KEY.location());
    public static final ResourceKey<LevelStem> MOON_DIMENSION_OPTIONS_KEY = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, MOON_KEY.location());

    private static final DimensionType MOON = new DimensionType(
            OptionalLong.empty(),
            true,
            false,
            false,
            true,
            1.0,
            false,
            false,
            -64,
            384,
            384,
            GalacticraftTag.INFINIBURN_MOON,
            MOON_KEY.location(),
            0.1F,
            new DimensionType.MonsterSettings(false, false, ConstantInt.of(11), 15)
    );

    public static void register(Registry<DimensionType> registry) {
        BuiltinRegistries.register(registry,MOON_DIMENSION_TYPE_KEY, MOON);
    }
}
