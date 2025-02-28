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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.StreamCodecs;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.EitherHolder;

import java.util.function.UnaryOperator;

public class GCDataComponents {
    public static final DataComponentType<RocketData> ROCKET_DATA = register("rocket_data", b -> b
            .persistent(RocketData.CODEC).networkSynchronized(RocketData.STREAM_CODEC));
    public static final DataComponentType<Long> AMOUNT = register("amount", b -> b
            .persistent(Codec.LONG).networkSynchronized(StreamCodecs.LONG));
    public static final DataComponentType<Integer> COLOR = register("color", b -> b
            .persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DataComponentType<Integer> TICKS_UNTIL_COOL = register("ticks_until_cool", b -> b
            .persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DataComponentType<Boolean> CREATIVE = register("creative", b -> b
            .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DataComponentType<ResourceKey<?>> KEY = register("key", b -> b
            .persistent(RecordCodecBuilder.create(i ->
                    i.group(
                            ResourceLocation.CODEC.fieldOf("registry").forGetter(ResourceKey::registry),
                            ResourceLocation.CODEC.fieldOf("location").forGetter(ResourceKey::location)
                    ).apply(i, (r, l) -> ResourceKey.create(ResourceKey.createRegistryKey(r), l))
            )).networkSynchronized(StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC,
                    ResourceKey::registry,
                    ResourceLocation.STREAM_CODEC,
                    ResourceKey::location,
                    (r, l) -> ResourceKey.create(ResourceKey.createRegistryKey(r), l)
            )));

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> op) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Constant.id(id), op.apply(DataComponentType.builder()).build());
    }

    public static void init() {}
}
