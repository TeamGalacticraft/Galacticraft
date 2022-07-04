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

package dev.galacticraft.mod.world.poi;

import com.google.common.collect.ImmutableSet;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.poi.PointOfInterestType;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftPointOfInterestType {
    public static final RegistryKey<PointOfInterestType> LUNAR_CARTOGRAPHER = register(new Identifier(Constant.MOD_ID, "lunar_cartographer"), 1, 1, ImmutableSet.copyOf(GalacticraftBlock.LUNAR_CARTOGRAPHY_TABLE.getStateManager().getStates()));

    public static void register() {
    }

    private static RegistryKey<PointOfInterestType> register(Identifier id, int ticketCount, int searchDistance, Iterable<BlockState> blocks) {
        PointOfInterestHelper.register(id, ticketCount, searchDistance, blocks);
        return RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, id);
    }
}
