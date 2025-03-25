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

package dev.galacticraft.mod.world.gen.custom;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;

public class SpecialAsteroidBlock {
    public Block block;
    public int probability;
    public double thickness; //Arbitrary scale from 0 to 1;
    public int index;
    public static ArrayList<SpecialAsteroidBlock> register = new ArrayList<>();

    public SpecialAsteroidBlock(Block block, int probability, double thickness) {
        this.block = block;
        this.probability = probability;
        this.thickness = thickness;
        this.index = register.size();
        register.add(this);
    }
}
