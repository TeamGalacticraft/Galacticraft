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

import java.util.ArrayList;
import java.util.Random;

public class SpecialAsteroidBlockHandler {
    ArrayList<SpecialAsteroidBlock> asteroidBlocks;

    public SpecialAsteroidBlockHandler(SpecialAsteroidBlock... asteroidBlocks) {
        this.asteroidBlocks = new ArrayList<SpecialAsteroidBlock>();
        for (SpecialAsteroidBlock asteroidBlock : this.asteroidBlocks) {
            for (int i = 0; i < asteroidBlock.probability; i++) {
                this.asteroidBlocks.add(asteroidBlock);
            }
        }
    }

    public SpecialAsteroidBlockHandler() {
        this.asteroidBlocks = new ArrayList<SpecialAsteroidBlock>();
    }

    public void addBlock(SpecialAsteroidBlock asteroidBlock) {
        for (int i = 0; i < asteroidBlock.probability; i++) {
            this.asteroidBlocks.add(asteroidBlock);
        }
    }

    public SpecialAsteroidBlock getBlock(Random rand, int size) {
        int s = this.asteroidBlocks.size();
        if (s < 10) {
            return this.asteroidBlocks.get(rand.nextInt(s));
        }

        Double r = rand.nextDouble();
        int index = (int) (s * Math.pow(r, (size + 5) * 0.05D));
        return this.asteroidBlocks.get(index);
    }
}
