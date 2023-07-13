/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.data.tag;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class GCBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public GCBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(GCTags.INFINIBURN_MOON)
                .add(this.reverseLookup(GCBlocks.MOON_BASALT_BRICK));
        this.tag(GCTags.BASE_STONE_MOON)
                .add(this.reverseLookup(GCBlocks.MOON_ROCK));
        this.tag(GCTags.MOON_CARVER_REPLACEABLES)
                .add(this.reverseLookup(GCBlocks.MOON_ROCK))
                .add(this.reverseLookup(GCBlocks.MOON_SURFACE_ROCK))
                .add(this.reverseLookup(GCBlocks.MOON_BASALT))
                .add(this.reverseLookup(GCBlocks.MOON_DIRT))
                .add(this.reverseLookup(GCBlocks.MOON_TURF));
        this.tag(GCTags.MOON_CRATER_CARVER_REPLACEABLES)
                .add(this.reverseLookup(GCBlocks.MOON_ROCK))
                .add(this.reverseLookup(GCBlocks.MOON_SURFACE_ROCK))
                .add(this.reverseLookup(GCBlocks.MOON_BASALT))
                .add(this.reverseLookup(GCBlocks.MOON_DIRT))
                .add(this.reverseLookup(GCBlocks.MOON_TURF));
        this.tag(GCTags.MOON_STONE_ORE_REPLACABLES)
                .add(this.reverseLookup(GCBlocks.MOON_ROCK))
                .add(this.reverseLookup(GCBlocks.MOON_BASALT));
        this.tag(GCTags.LUNASLATE_ORE_REPLACABLES)
                .add(this.reverseLookup(GCBlocks.LUNASLATE));

        this.tag(BlockTags.CLIMBABLE)
                .add(this.reverseLookup(GCBlocks.TIN_LADDER));

        this.tag(BlockTags.WALLS)
                .add(this.reverseLookup(GCBlocks.TIN_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.COPPER_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.BRONZE_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.STEEL_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.TITANIUM_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.IRON_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.ALUMINUM_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.METEORIC_IRON_DECORATION_WALL))

                .add(this.reverseLookup(GCBlocks.DETAILED_TIN_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_COPPER_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_BRONZE_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_STEEL_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_TITANIUM_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_IRON_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_ALUMINUM_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_METEORIC_IRON_DECORATION_WALL))

                .add(this.reverseLookup(GCBlocks.MOON_ROCK_WALL))
                .add(this.reverseLookup(GCBlocks.COBBLED_MOON_ROCK_WALL))
                .add(this.reverseLookup(GCBlocks.MOON_BASALT_WALL))
                .add(this.reverseLookup(GCBlocks.MOON_BASALT_BRICK_WALL));

        this.tag(BlockTags.WALL_POST_OVERRIDE)
                .add(this.reverseLookup(GCBlocks.GLOWSTONE_TORCH))
                .add(this.reverseLookup(GCBlocks.UNLIT_TORCH));
    }
}
