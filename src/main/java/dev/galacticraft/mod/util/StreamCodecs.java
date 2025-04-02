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

package dev.galacticraft.mod.util;


import com.mojang.datafixers.util.*;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

public interface StreamCodecs {
    StreamCodec<ByteBuf, Long> LONG = StreamCodec.of(
            ByteBuf::writeLong,
            ByteBuf::readLong
    );

    static <B extends RegistryFriendlyByteBuf, V> StreamCodec<B, V> ofRegistryEntry(ResourceKey<Registry<V>> registry) {
        return StreamCodec.of(
                (b, v) -> b.writeResourceLocation(b.registryAccess().registryOrThrow(registry).getKey(v)),
                b -> Objects.requireNonNull(b.registryAccess().registryOrThrow(registry).get(b.readResourceLocation()))
        );
    }

    static <B extends ByteBuf, V> StreamCodec<B, @Nullable V> ofNullable(StreamCodec<B, @NotNull V> codec) {
        return StreamCodec.of(
                (b, v) -> {
                    if (v == null) {
                        b.writeBoolean(false);
                    } else {
                        b.writeBoolean(true);
                        codec.encode(b, v);
                    }
                },
                b -> b.readBoolean() ? codec.decode(b) : null
        );
    }

    static <B extends RegistryFriendlyByteBuf, V> StreamCodec<B, Holder<V>> ofHolder(ResourceKey<Registry<V>> registry) {
        return StreamCodec.of(
                (b, v) -> b.writeResourceLocation(b.registryAccess().registryOrThrow(registry).getKey(v.value())),
                b -> Objects.requireNonNull(b.registryAccess().registryOrThrow(registry).getHolderOrThrow(ResourceKey.create(registry, b.readResourceLocation())))
        );
    }

    static <B extends RegistryFriendlyByteBuf, V> StreamCodec<B, Holder.Reference<V>> ofReference(ResourceKey<Registry<V>> registry) {
        return StreamCodec.of(
                (b, v) -> b.writeResourceKey(v.key()),
                b -> Objects.requireNonNull(b.registryAccess().registryOrThrow(registry).getHolderOrThrow(b.readResourceKey(registry)))
        );
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, ResourceKey<T>> ofResourceKey(ResourceKey<? extends Registry<T>> registryKey) {
        return StreamCodec.of(
                (buf, key) -> buf.writeResourceLocation(key.location()),
                buf -> ResourceKey.create(registryKey, buf.readResourceLocation())
        );
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> from1, StreamCodec<? super B, T2> codec2, Function<C, T2> from2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> from3, StreamCodec<? super B, T4> codec4, Function<C, T4> from4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> from5, StreamCodec<? super B, T6> codec6, Function<C, T6> from6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> from7,
            Function7<T1, T2, T3, T4, T5, T6, T7, C> to
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(B object) {
                return to.apply(codec1.decode(object), codec2.decode(object), codec3.decode(object), codec4.decode(object), codec5.decode(object), codec6.decode(object), codec7.decode(object));
            }

            @Override
            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> from1, StreamCodec<? super B, T2> codec2, Function<C, T2> from2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> from3, StreamCodec<? super B, T4> codec4, Function<C, T4> from4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> from5, StreamCodec<? super B, T6> codec6, Function<C, T6> from6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> from7, StreamCodec<? super B, T8> codec8, Function<C, T8> from8,
            Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> to
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(B object) {
                return to.apply(codec1.decode(object), codec2.decode(object), codec3.decode(object), codec4.decode(object), codec5.decode(object), codec6.decode(object), codec7.decode(object), codec8.decode(object));
            }

            @Override
            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
                codec8.encode(object, from8.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> from1, StreamCodec<? super B, T2> codec2, Function<C, T2> from2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> from3, StreamCodec<? super B, T4> codec4, Function<C, T4> from4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> from5, StreamCodec<? super B, T6> codec6, Function<C, T6> from6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> from7, StreamCodec<? super B, T8> codec8, Function<C, T8> from8,
            StreamCodec<? super B, T9> codec9, Function<C, T9> from9,
            Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> to
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(B object) {
                return to.apply(codec1.decode(object), codec2.decode(object), codec3.decode(object), codec4.decode(object), codec5.decode(object), codec6.decode(object), codec7.decode(object), codec8.decode(object), codec9.decode(object));
            }

            @Override
            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
                codec8.encode(object, from8.apply(object2));
                codec9.encode(object, from9.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> from1, StreamCodec<? super B, T2> codec2, Function<C, T2> from2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> from3, StreamCodec<? super B, T4> codec4, Function<C, T4> from4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> from5, StreamCodec<? super B, T6> codec6, Function<C, T6> from6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> from7, StreamCodec<? super B, T8> codec8, Function<C, T8> from8,
            StreamCodec<? super B, T9> codec9, Function<C, T9> from9, StreamCodec<? super B, T10> codec10, Function<C, T10> from10,
            Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, C> to
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(B object) {
                return to.apply(codec1.decode(object), codec2.decode(object), codec3.decode(object), codec4.decode(object), codec5.decode(object), codec6.decode(object), codec7.decode(object), codec8.decode(object), codec9.decode(object), codec10.decode(object));
            }

            @Override
            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
                codec8.encode(object, from8.apply(object2));
                codec9.encode(object, from9.apply(object2));
                codec10.encode(object, from10.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> from1, StreamCodec<? super B, T2> codec2, Function<C, T2> from2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> from3, StreamCodec<? super B, T4> codec4, Function<C, T4> from4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> from5, StreamCodec<? super B, T6> codec6, Function<C, T6> from6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> from7, StreamCodec<? super B, T8> codec8, Function<C, T8> from8,
            StreamCodec<? super B, T9> codec9, Function<C, T9> from9, StreamCodec<? super B, T10> codec10, Function<C, T10> from10,
            StreamCodec<? super B, T11> codec11, Function<C, T11> from11,
            Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, C> to
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(B object) {
                return to.apply(codec1.decode(object), codec2.decode(object), codec3.decode(object), codec4.decode(object), codec5.decode(object), codec6.decode(object), codec7.decode(object), codec8.decode(object), codec9.decode(object), codec10.decode(object), codec11.decode(object));
            }

            @Override
            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
                codec8.encode(object, from8.apply(object2));
                codec9.encode(object, from9.apply(object2));
                codec10.encode(object, from10.apply(object2));
                codec11.encode(object, from11.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> from1, StreamCodec<? super B, T2> codec2, Function<C, T2> from2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> from3, StreamCodec<? super B, T4> codec4, Function<C, T4> from4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> from5, StreamCodec<? super B, T6> codec6, Function<C, T6> from6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> from7, StreamCodec<? super B, T8> codec8, Function<C, T8> from8,
            StreamCodec<? super B, T9> codec9, Function<C, T9> from9, StreamCodec<? super B, T10> codec10, Function<C, T10> from10,
            StreamCodec<? super B, T11> codec11, Function<C, T11> from11, StreamCodec<? super B, T12> codec12, Function<C, T12> from12,
            Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, C> to
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(B object) {
                return to.apply(codec1.decode(object), codec2.decode(object), codec3.decode(object), codec4.decode(object), codec5.decode(object), codec6.decode(object), codec7.decode(object), codec8.decode(object), codec9.decode(object), codec10.decode(object), codec11.decode(object), codec12.decode(object));
            }

            @Override
            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
                codec8.encode(object, from8.apply(object2));
                codec9.encode(object, from9.apply(object2));
                codec10.encode(object, from10.apply(object2));
                codec11.encode(object, from11.apply(object2));
                codec12.encode(object, from12.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> from1, StreamCodec<? super B, T2> codec2, Function<C, T2> from2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> from3, StreamCodec<? super B, T4> codec4, Function<C, T4> from4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> from5, StreamCodec<? super B, T6> codec6, Function<C, T6> from6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> from7, StreamCodec<? super B, T8> codec8, Function<C, T8> from8,
            StreamCodec<? super B, T9> codec9, Function<C, T9> from9, StreamCodec<? super B, T10> codec10, Function<C, T10> from10,
            StreamCodec<? super B, T11> codec11, Function<C, T11> from11, StreamCodec<? super B, T12> codec12, Function<C, T12> from12,
            StreamCodec<? super B, T13> codec13, Function<C, T13> from13,
            Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, C> to
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(B object) {
                return to.apply(codec1.decode(object), codec2.decode(object), codec3.decode(object), codec4.decode(object), codec5.decode(object), codec6.decode(object), codec7.decode(object), codec8.decode(object), codec9.decode(object), codec10.decode(object), codec11.decode(object), codec12.decode(object), codec13.decode(object));
            }

            @Override
            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
                codec8.encode(object, from8.apply(object2));
                codec9.encode(object, from9.apply(object2));
                codec10.encode(object, from10.apply(object2));
                codec11.encode(object, from11.apply(object2));
                codec12.encode(object, from12.apply(object2));
                codec13.encode(object, from13.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> from1, StreamCodec<? super B, T2> codec2, Function<C, T2> from2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> from3, StreamCodec<? super B, T4> codec4, Function<C, T4> from4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> from5, StreamCodec<? super B, T6> codec6, Function<C, T6> from6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> from7, StreamCodec<? super B, T8> codec8, Function<C, T8> from8,
            StreamCodec<? super B, T9> codec9, Function<C, T9> from9, StreamCodec<? super B, T10> codec10, Function<C, T10> from10,
            StreamCodec<? super B, T11> codec11, Function<C, T11> from11, StreamCodec<? super B, T12> codec12, Function<C, T12> from12,
            StreamCodec<? super B, T13> codec13, Function<C, T13> from13, StreamCodec<? super B, T14> codec14, Function<C, T14> from14,
            Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, C> to
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(B object) {
                return to.apply(codec1.decode(object), codec2.decode(object), codec3.decode(object), codec4.decode(object), codec5.decode(object), codec6.decode(object), codec7.decode(object), codec8.decode(object), codec9.decode(object), codec10.decode(object), codec11.decode(object), codec12.decode(object), codec13.decode(object), codec14.decode(object));
            }

            @Override
            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
                codec8.encode(object, from8.apply(object2));
                codec9.encode(object, from9.apply(object2));
                codec10.encode(object, from10.apply(object2));
                codec11.encode(object, from11.apply(object2));
                codec12.encode(object, from12.apply(object2));
                codec13.encode(object, from13.apply(object2));
                codec14.encode(object, from14.apply(object2));
            }
        };
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <B extends ByteBuf, T> @NotNull StreamCodec<B, T[]> array(StreamCodec<B, T> codec, IntFunction<T[]> constructor) {
        return StreamCodec.of(
                (b, a) -> {
                    VarInt.write(b, a.length);
                    for (T t : a) {
                        codec.encode(b, t);
                    }
                },
                b -> {
                    int len = VarInt.read(b);
                    T[] a = constructor.apply(len);
                    for (int i = 0; i < len; i++) {
                        a[i] = codec.decode(b);
                    }
                    return a;
                }
        );
    }

    static <E extends Enum<E>> StreamCodec<ByteBuf, E> ofEnum(E[] values) {
        return StreamCodec.of((b, e) -> b.writeByte(e.ordinal()), b -> values[b.readByte()]);
    }

    static <T> StreamCodec<RegistryFriendlyByteBuf, T> wrapCodec(Codec<T> codec) {
        return StreamCodec.of(
                (b, v) -> b.writeNbt(codec.encode(v, NbtOps.INSTANCE, new CompoundTag()).getOrThrow()),
                b -> codec.decode(NbtOps.INSTANCE, b.readNbt()).getOrThrow().getFirst()
        );
    }
}
