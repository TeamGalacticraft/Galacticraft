/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.world.gen.surfacebuilder;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MultiBlockSurfaceConfig implements SurfaceConfig {
    private final BlockStateWithChance[] topMaterials;
    private final BlockStateWithChance[] underMaterials;
    private final BlockStateWithChance[] underwaterMaterials;

    private final Random random;

    public MultiBlockSurfaceConfig(BlockStateWithChance[] topMaterials, BlockStateWithChance[] underMaterials, BlockStateWithChance[] underwaterMaterials) {
        Arrays.sort(topMaterials, BlockStateWithChance::compareTo);
        Arrays.sort(underMaterials, BlockStateWithChance::compareTo);
        Arrays.sort(underwaterMaterials, BlockStateWithChance::compareTo);

        this.topMaterials = topMaterials;
        this.underMaterials = underMaterials;
        this.underwaterMaterials = underwaterMaterials;

        assert topMaterials.length > 0;
        assert underMaterials.length > 0;
        assert underwaterMaterials.length > 0;

        this.random = new Random();
    }

    @Override
    public BlockState getTopMaterial() {
        if (topMaterials.length == 1) return topMaterials[0].getState();

        for (int a = 0; a < 5; a++) {
            for (BlockStateWithChance topMaterial : topMaterials) {
                if (topMaterial.getChance() < random.nextInt(100)) {
                    if (topMaterial.getState().getBlock() != GalacticraftBlocks.MOON_BASALT) {
                        System.out.println("ok");
                    }
                    return topMaterial.getState();
                }
            }
        }
        return topMaterials[random.nextInt(topMaterials.length)].getState();
    }

    @Override
    public BlockState getUnderMaterial() {
        if (underMaterials.length == 1) return underMaterials[0].getState();

        for (int a = 0; a < 5; a++) {
            for (BlockStateWithChance underMaterial : underMaterials) {
                if (underMaterial.getChance() < random.nextInt(100)) {
                    if (underMaterial.getState().getBlock() != GalacticraftBlocks.MOON_BASALT) {
                        System.out.println("ok");
                    }
                    return underMaterial.getState();
                }
            }
        }
        return underMaterials[random.nextInt(underMaterials.length)].getState();
    }

    public BlockState getUnderwaterMaterial() {
        if (underwaterMaterials.length == 1) return underwaterMaterials[0].getState();

        for (int a = 0; a < 5; a++) {
            for (BlockStateWithChance underwaterMaterial : underwaterMaterials) {
                if (underwaterMaterial.getChance() < random.nextInt(100)) {
                    if (underwaterMaterial.getState().getBlock() != GalacticraftBlocks.MOON_BASALT) {
                        System.out.println("ok");
                    }
                    return underwaterMaterial.getState();
                }
            }
        }
        return underwaterMaterials[random.nextInt(underwaterMaterials.length)].getState();
    }

    public static MultiBlockSurfaceConfig deserialize(@NotNull Dynamic<?> dynamic) {
        BlockStateWithChance[] topMaterials = new BlockStateWithChance[dynamic.get("top_material_count").asInt(1)];
        for (int i = 0; i < topMaterials.length; i++) {
            topMaterials[i] = new BlockStateWithChance(dynamic.get("top_material_" + i).map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState()), dynamic.get("top_material_chance_" + i).asInt(0));
        }

        BlockStateWithChance[] underMaterials = new BlockStateWithChance[dynamic.get("under_material_count").asInt(1)];
        for (int i = 0; i < underMaterials.length; i++) {
            underMaterials[i] = new BlockStateWithChance(dynamic.get("under_material_" + i).map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState()), dynamic.get("under_material_chance_" + i).asInt(0));
        }

        BlockStateWithChance[] underwaterMaterials = new BlockStateWithChance[dynamic.get("underwater_material_count").asInt(1)];
        for (int i = 0; i < underwaterMaterials.length; i++) {
            underwaterMaterials[i] = new BlockStateWithChance(dynamic.get("underwater_material_" + i).map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState()), dynamic.get("underwater_material_chance_" + i).asInt(0));
        }

        return new MultiBlockSurfaceConfig(topMaterials, underMaterials, underwaterMaterials);
    }

}