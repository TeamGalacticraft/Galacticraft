/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

import java.util.OptionalLong;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftDimensionType {
    public static final RegistryKey<World> MOON_KEY = RegistryKey.of(Registry.WORLD_KEY, new Identifier(Constant.MOD_ID, "moon"));
    public static final RegistryKey<DimensionType> MOON_DIMENSION_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, MOON_KEY.getValue());
    public static final RegistryKey<DimensionOptions> MOON_DIMENSION_OPTIONS_KEY = RegistryKey.of(Registry.DIMENSION_KEY, MOON_KEY.getValue());

    private static final DimensionType MOON = DimensionType.create(
            OptionalLong.empty(),
            true,
            false,
            false,
            true,
            1.0,
            false,
            false,
            false,
            false,
            true,
            -64,
            384,
            384,
            GalacticraftTag.INFINIBURN_MOON.getId(),
            MOON_KEY.getValue(),
            0.1F
    );

    public static void register(MutableRegistry<DimensionType> registry) {
        registry.add(MOON_DIMENSION_TYPE_KEY, MOON, Lifecycle.stable());
    }
}
