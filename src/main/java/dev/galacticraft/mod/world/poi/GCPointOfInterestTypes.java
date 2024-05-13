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

package dev.galacticraft.mod.world.poi;

import com.google.common.collect.ImmutableSet;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;

public class GCPointOfInterestTypes {
    public static final ResourceKey<PoiType> LUNAR_CARTOGRAPHER = register(Constant.id("lunar_cartographer"), 1, 1, ImmutableSet.copyOf(GCBlocks.LUNAR_CARTOGRAPHY_TABLE.getStateDefinition().getPossibleStates()));

    public static void register() {
    }

    private static ResourceKey<PoiType> register(ResourceLocation id, int ticketCount, int searchDistance, Iterable<BlockState> blocks) {
        PointOfInterestHelper.register(id, ticketCount, searchDistance, blocks);
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, id);
    }
}
