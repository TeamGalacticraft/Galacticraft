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

package dev.galacticraft.mod.world.dimension;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalLong;

public class GCDimensionTypes {
    public static final ResourceKey<DimensionType> MOON = key("moon");
    public static final ResourceKey<DimensionType> VENUS = key("venus");

    public static void bootstrapRegistries(BootstapContext<DimensionType> context) {
        context.register(MOON, new DimensionType(
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
                GCTags.INFINIBURN_MOON,
                Constant.id("moon"), // fixme: GCDimensionEffects somehow seems to load client classes
                0.1f,
                new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)
        ));
        context.register(VENUS, new DimensionType(
                OptionalLong.empty(), // fixedTime
                true, // hasSkyLight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                1.0,  // coordinateScale
                false, // bedWorks
                false, // respawnAnchorWorks
                -64, // minY
                384, // height
                384, // logicalHeight
                GCTags.INFINIBURN_VENUS, // infiniburn
                Constant.id("venus"), // effectsLocation // fixme
                0.1F, // ambientLight
                new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)
        ));
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull ResourceKey<DimensionType> key(@NotNull String id) {
        return Constant.key(Registries.DIMENSION_TYPE, id);
    }
}
