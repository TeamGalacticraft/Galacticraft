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

package dev.galacticraft.api.rocket;

import dev.galacticraft.mod.content.GCRocketParts;
import net.minecraft.world.item.EitherHolder;

import java.util.Optional;

public class RocketPrefabs {
    public static final RocketData MISSING = new RocketData(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 0);
    public static final RocketData TIER_1 = new RocketData(
            new EitherHolder<>(GCRocketParts.TIER_1_CONE),
            new EitherHolder<>(GCRocketParts.TIER_1_BODY),
            new EitherHolder<>(GCRocketParts.TIER_1_FIN),
            null,
            new EitherHolder<>(GCRocketParts.TIER_1_ENGINE),
            null,
            0xFFFFFFFF
    );
    public static final RocketData TIER_1_STORAGE_UPGRADE = new RocketData(
            new EitherHolder<>(GCRocketParts.TIER_1_CONE),
            new EitherHolder<>(GCRocketParts.TIER_1_BODY),
            new EitherHolder<>(GCRocketParts.TIER_1_FIN),
            null,
            new EitherHolder<>(GCRocketParts.TIER_1_ENGINE),
            new EitherHolder<>(GCRocketParts.STORAGE_UPGRADE),
            0xFFFFFFFF
    );
    // Same as t1 for now
    public static final RocketData TIER_2 = new RocketData(
            new EitherHolder<>(GCRocketParts.TIER_1_CONE),
            new EitherHolder<>(GCRocketParts.TIER_1_BODY),
            new EitherHolder<>(GCRocketParts.TIER_1_FIN),
            null,
            new EitherHolder<>(GCRocketParts.TIER_1_ENGINE),
            null,
            0xFFFFFFFF
    );
}
