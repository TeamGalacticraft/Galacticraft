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

package dev.galacticraft.api.entity.attribute;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public final class GcApiEntityAttributes {
    public static final Attribute CAN_BREATHE_IN_SPACE = Registry.register(BuiltInRegistries.ATTRIBUTE, Constant.id("can_breathe_in_space"), (new RangedAttribute("galacticraft.attribute.name.generic.can_breathe_in_space", 0.0D, 0.0D, 1.0D)).setSyncable(true));

    public static final Attribute LOCAL_GRAVITY_LEVEL = Registry.register(BuiltInRegistries.ATTRIBUTE, Constant.id("local_gravity_level"), (new RangedAttribute("galacticraft.attribute.name.generic.local_gravity_level", 0.0D, 0.0D, 1.0D)).setSyncable(true));

    private GcApiEntityAttributes() {}

    public static void init() {}
}
