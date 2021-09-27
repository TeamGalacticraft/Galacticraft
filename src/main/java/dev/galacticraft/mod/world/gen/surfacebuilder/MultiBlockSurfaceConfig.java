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

package dev.galacticraft.mod.world.gen.surfacebuilder;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MultiBlockSurfaceConfig extends TernarySurfaceConfig {
    public static final Codec<MultiBlockSurfaceConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(BlockStateWithChance.CODEC.listOf().fieldOf("top_materials").forGetter((surfaceConfig) -> Lists.newArrayList(surfaceConfig.topMaterials)), BlockStateWithChance.CODEC.listOf().fieldOf("under_materials").forGetter((surfaceConfig) -> Lists.newArrayList(surfaceConfig.underMaterials)), BlockStateWithChance.CODEC.listOf().fieldOf("underwater_materials").forGetter((surfaceConfig) -> Lists.newArrayList(surfaceConfig.underwaterMaterials))).apply(instance, MultiBlockSurfaceConfig::new));

    private final BlockStateWithChance[] topMaterials;
    private final BlockStateWithChance[] underMaterials;
    private final BlockStateWithChance[] underwaterMaterials;

    private final Random random;

    public MultiBlockSurfaceConfig(BlockStateWithChance[] topMaterials, BlockStateWithChance[] underMaterials, BlockStateWithChance[] underwaterMaterials) {
        super(topMaterials[0].getState(), underMaterials[0].getState(), underwaterMaterials[0].getState());
        Arrays.sort(topMaterials, BlockStateWithChance::compareTo);
        Arrays.sort(underMaterials, BlockStateWithChance::compareTo);
        Arrays.sort(underwaterMaterials, BlockStateWithChance::compareTo);

        this.topMaterials = topMaterials;
        this.underMaterials = underMaterials;
        this.underwaterMaterials = underwaterMaterials;

        this.random = new Random();
    }

    public MultiBlockSurfaceConfig(List<BlockStateWithChance> topMaterials, List<BlockStateWithChance> underMaterials, List<BlockStateWithChance> underwaterMaterials) {
        this(topMaterials.toArray(new BlockStateWithChance[0]), topMaterials.toArray(new BlockStateWithChance[0]), topMaterials.toArray(new BlockStateWithChance[0]));
    }

    @Override
    public BlockState getTopMaterial() {
        return this.getState(topMaterials);
    }

    @Override
    public BlockState getUnderMaterial() {
        return this.getState(underMaterials);
    }

    public BlockState getUnderwaterMaterial() {
        return this.getState(underwaterMaterials);
    }

    public BlockState getState(BlockStateWithChance[] states) {
        if (states.length == 1) return states[0].getState();

        for (int a = 0; a < 5; a++) {
            for (BlockStateWithChance state : states) {
                if (state.getChance() < random.nextInt(100)) {
                    return state.getState();
                }
            }
        }
        return states[random.nextInt(states.length)].getState();
    }
}