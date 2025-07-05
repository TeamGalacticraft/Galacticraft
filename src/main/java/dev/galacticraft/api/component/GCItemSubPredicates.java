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

package dev.galacticraft.api.component;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.advancements.critereon.ItemFullTankPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class GCItemSubPredicates {
    public static final ItemSubPredicate.Type<ItemFullTankPredicate> FULL_TANK = register("full_tank", ItemFullTankPredicate.CODEC);

    private static <T extends ItemSubPredicate> ItemSubPredicate.Type<T> register(String string, Codec<T> codec) {
        return Registry.register(BuiltInRegistries.ITEM_SUB_PREDICATE_TYPE, Constant.id(string), new ItemSubPredicate.Type<T>(codec));
    }

    public static void init() {
    }
}
