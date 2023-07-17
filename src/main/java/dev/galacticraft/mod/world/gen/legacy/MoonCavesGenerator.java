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

package dev.galacticraft.mod.world.gen.legacy;

import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class MoonCavesGenerator {
    /**
     * The number of Chunks to gen-check in any given direction.
     */
    protected int range = 8;

    /**
     * The RNG used by the MapGen classes.
     */
    protected RandomSource rand = RandomSource.create();

    public void generate(WorldGenRegion primer) {
        this.rand.setSeed(primer.getSeed());
        final long r0 = this.rand.nextLong();
        final long r1 = this.rand.nextLong();

        int chunkX = primer.getCenter().x;
        int chunkZ = primer.getCenter().z;

        for (int x0 = chunkX - this.range; x0 <= chunkX + this.range; ++x0) {
            for (int y0 = chunkZ - this.range; y0 <= chunkZ + this.range; ++y0) {
                final long randX = x0 * r0;
                final long randZ = y0 * r1;
                this.rand.setSeed(randX ^ randZ ^ primer.getSeed());
                this.recursiveGenerate(x0, y0, chunkX, chunkZ, primer);
            }
        }
    }

    /**
     * Recursively called by generate() (generate) and optionally by itself.
     */
    protected void recursiveGenerate(int xChunkCoord, int zChunkCoord, int origXChunkCoord, int origZChunkCoord, WorldGenRegion primer) {
        int var7 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);

        if (this.rand.nextInt(15) != 0)
        {
            var7 = 0;
        }

        for (int var8 = 0; var8 < var7; ++var8)
        {
            final double var9 = xChunkCoord * 16 + this.rand.nextInt(16);
            final double var11 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            final double var13 = zChunkCoord * 16 + this.rand.nextInt(16);
            int var15 = 1;

            if (this.rand.nextInt(4) == 0)
            {
                this.generateLargeCaveNode(this.rand.nextLong(), origXChunkCoord, origZChunkCoord, primer, var9, var11, var13);
                var15 += this.rand.nextInt(4);
            }

            for (int var16 = 0; var16 < var15; ++var16)
            {
                final float var17 = this.rand.nextFloat() * Mth.HALF_PI;
                final float var18 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float var19 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                if (this.rand.nextInt(10) == 0)
                {
                    var19 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.generateCaveNode(this.rand.nextLong(), origXChunkCoord, origZChunkCoord, primer, var9, var11, var13, var19, var17, var18, 0, 0, 1.0D);
            }
        }
    }

    protected void generateLargeCaveNode(long par1, int par3, int par4, WorldGenRegion primer, double par6, double par8, double par10) {
        this.generateCaveNode(par1, par3, par4, primer, par6, par8, par10, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    protected void generateCaveNode(long par1, int par3, int par4, WorldGenRegion primer, double par6, double par8, double par10, float par12, float par13, float par14, int par15, int par16, double par17) {
        final double d4 = par3 * 16 + 8;
        final double d5 = par4 * 16 + 8;
        float f3 = 0.0F;
        float f4 = 0.0F;
        final Random random = new Random(par1);

        if (par16 <= 0)
        {
            final int j1 = this.range * 16 - 16;
            par16 = j1 - random.nextInt(j1 / 4);
        }

        boolean flag = false;

        if (par15 == -1)
        {
            par15 = par16 / 2;
            flag = true;
        }

        final int k1 = random.nextInt(par16 / 2) + par16 / 4;

        for (final boolean flag1 = random.nextInt(6) == 0; par15 < par16; ++par15)
        {
            final double d6 = 1.5D + Mth.sin(par15 * (float) Math.PI / par16) * par12 * 1.0F;
            final double d7 = d6 * par17;
            final float f5 = Mth.cos(par14);
            final float f6 = Mth.sin(par14);
            par6 += Mth.cos(par13) * f5;
            par8 += f6;
            par10 += Mth.sin(par13) * f5;

            if (flag1)
            {
                par14 *= 0.92F;
            } else
            {
                par14 *= 0.7F;
            }

            par14 += f4 * 0.1F;
            par13 += f3 * 0.1F;
            f4 *= 0.9F;
            f3 *= 0.75F;
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!flag && par15 == k1 && par12 > 1.0F && par16 > 0)
            {
                this.generateCaveNode(random.nextLong(), par3, par4, primer, par6, par8, par10, random.nextFloat() * 0.5F + 0.5F, par13 - Mth.HALF_PI, par14 / 3.0F, par15, par16, 1.0D);
                this.generateCaveNode(random.nextLong(), par3, par4, primer, par6, par8, par10, random.nextFloat() * 0.5F + 0.5F, par13 + Mth.HALF_PI, par14 / 3.0F, par15, par16, 1.0D);
                return;
            }

            if (flag || random.nextInt(4) != 0)
            {
                final double d8 = par6 - d4;
                final double d9 = par10 - d5;
                final double d10 = par16 - par15;
                final double d11 = par12 + 2.0F + 16.0F;

                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11)
                {
                    return;
                }

                if (par6 >= d4 - 16.0D - d6 * 2.0D && par10 >= d5 - 16.0D - d6 * 2.0D && par6 <= d4 + 16.0D + d6 * 2.0D && par10 <= d5 + 16.0D + d6 * 2.0D)
                {
                    int l1 = Mth.floor(par6 - d6) - par3 * 16 - 1;
                    int i2 = Mth.floor(par6 + d6) - par3 * 16 + 1;
                    int j2 = Mth.floor(par8 - d7) - 1;
                    int k2 = Mth.floor(par8 + d7) + 1;
                    int l2 = Mth.floor(par10 - d6) - par4 * 16 - 1;
                    int i3 = Mth.floor(par10 + d6) - par4 * 16 + 1;

                    if (l1 < 0)
                    {
                        l1 = 0;
                    }

                    if (i2 > 16)
                    {
                        i2 = 16;
                    }

                    if (j2 < 1)
                    {
                        j2 = 1;
                    }

                    if (k2 > 120)
                    {
                        k2 = 120;
                    }

                    if (l2 < 0)
                    {
                        l2 = 0;
                    }

                    if (i3 > 16)
                    {
                        i3 = 16;
                    }

                    int j3;
                    for (j3 = l1; j3 < i2; ++j3)
                    {
                        for (int l3 = l2; l3 < i3; ++l3)
                        {
                            for (int i4 = k2 + 1; i4 >= j2 - 1; --i4)
                            {
                                if (i4 >= 0 && i4 < 128)
                                {
                                    if (i4 != j2 - 1 && j3 != l1 && j3 != i2 - 1 && l3 != l2 && l3 != i3 - 1)
                                    {
                                        i4 = j2;
                                    }
                                }
                            }
                        }
                    }

                    for (int localY = j2; localY < k2; localY++) {
                        final double yfactor = (localY + 0.5D - par8) / d7;
                        final double yfactorSq = yfactor * yfactor;

                        for (int localX = l1; localX < i2; localX++) {
                            final double zfactor = (localX + par3 * 16 + 0.5D - par6) / d6;
                            final double zfactorSq = zfactor * zfactor;

                            for (int localZ = l2; localZ < i3; localZ++) {
                                final double xfactor = (localZ + par4 * 16 + 0.5D - par10) / d6;
                                final double xfactorSq = xfactor * xfactor;

                                if (xfactorSq + zfactorSq < 1.0D) {
                                    if (yfactor > -0.7D && xfactorSq + yfactorSq + zfactorSq < 1.0D) {
                                        BlockPos pos = primer.getCenter().getWorldPosition().offset(localX, localY, localZ);
                                        BlockState state = primer.getBlockState(pos);
                                        if (state.is(GCTags.MOON_CARVER_REPLACEABLES)) {
                                            primer.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (flag) {
                        break;
                    }
                }
            }
        }
    }
}
