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

package dev.galacticraft.mod.content.entity.damage;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class GCDamageTypes {
    public static final ResourceKey<DamageType> OIL_BOOM = Constant.key(Registries.DAMAGE_TYPE, "oil_boom");
    public static final ResourceKey<DamageType> VINE_POISON = Constant.key(Registries.DAMAGE_TYPE, "vine_poison");
    public static final ResourceKey<DamageType> SUFFOCATION = Constant.key(Registries.DAMAGE_TYPE, "suffocation");
    public static final ResourceKey<DamageType> SULFURIC_ACID = Constant.key(Registries.DAMAGE_TYPE, "sulfuric_acid");

    public static void bootstrapRegistries(BootstapContext<DamageType> context) {
        context.register(OIL_BOOM, new DamageType("oil_boom", DamageScaling.ALWAYS, 0.1f));
        context.register(VINE_POISON, new DamageType("vine_poison", 0.0f));
        context.register(SUFFOCATION, new DamageType("suffocation", 0.0f));
        context.register(SULFURIC_ACID, new DamageType("sulfuric_acid", 0.0f));
    }
}
