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

package dev.galacticraft.mod.test;

import org.junit.jupiter.api.BeforeAll;

public class MiscellaneousTests {
    @BeforeAll
    public static void setup() {
        TestSetup.setup();
    }

//    @Test //fixme: apparently mods are not loaded in the test environment
//    public void gratingHasAllFluids() {
//        Set<ResourceLocation> fluids = FluidLoggable.FLUID.getAllValues().map(Property.Value::value).collect(Collectors.toSet());
//        for (ResourceLocation resourceLocation : BuiltInRegistries.FLUID.keySet()) {
//            assertTrue(fluids.contains(resourceLocation));
//        }
//    }
}
