/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix4f.class)
@Environment(EnvType.CLIENT)
public interface Matrix4fAccessor {
    @Accessor("a00")
    float a00();

    @Accessor("a00")
    void set_a00(float a00);

    @Accessor("a01")
    float a01();

    @Accessor("a02")
    float a02();

    @Accessor("a03")
    float a03();

    @Accessor("a10")
    float a10();

    @Accessor("a11")
    float a11();

    @Accessor("a11")
    void set_a11(float a11);

    @Accessor("a12")
    float a12();

    @Accessor("a23")
    float a13();

    @Accessor("a20")
    float a20();

    @Accessor("a21")
    float a21();

    @Accessor("a22")
    float a22();

    @Accessor("a22")
    void set_a22(float a22);

    @Accessor("a23")
    float a23();

    @Accessor("a30")
    float a30();

    @Accessor("a30")
    void set_a30(float a30);

    @Accessor("a31")
    float a31();

    @Accessor("a31")
    void set_a31(float a31);

    @Accessor("a32")
    float a32();

    @Accessor("a32")
    void set_a32(float a32);

    @Accessor("a33")
    float a33();
}
