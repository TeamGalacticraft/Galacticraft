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

package dev.galacticraft.impl.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;

import java.util.Map;
import java.util.function.Supplier;

public record MapKvCodec<K, V, M extends Map<K, V>>(Supplier<M> supplier, Encoder<K> encoderA, Decoder<K> decoderA,
                                                    Encoder<V> encoderB, Decoder<V> decoderB) implements Codec<M> {

    public static <A, B, M extends Map<A, B>> MapKvCodec<A, B, M> create(Supplier<M> supplier, Encoder<A> encoderA, Decoder<A> decoderA, Encoder<B> encoderB, Decoder<B> decoderB) {
        return new MapKvCodec<>(supplier, encoderA, decoderA, encoderB, decoderB);
    }

    public static <A, B, M extends Map<A, B>> MapKvCodec<A, B, M> create(Supplier<M> supplier, Codec<A> codecA, Codec<B> codecB) {
        return new MapKvCodec<>(supplier, codecA, codecA, codecB, codecB);
    }

    @Override
    public <T> DataResult<Pair<M, T>> decode(DynamicOps<T> ops, T input) {
        M map = supplier.get();
        ops.getMap(input).getOrThrow().entries().forEach(pair -> map.put(this.decoderA.decode(ops, pair.getFirst()).getOrThrow().getFirst(), this.decoderB.decode(ops, pair.getSecond()).getOrThrow().getFirst()));
        return DataResult.success(new Pair<>(map, input));
    }

    @Override
    public <T> DataResult<T> encode(M input, DynamicOps<T> ops, T prefix) {
        RecordBuilder<T> recordBuilder = ops.mapBuilder();
        input.forEach((k, v) -> recordBuilder.add(encoderA.encode(k, ops, prefix), encoderB.encode(v, ops, prefix)));
        return recordBuilder.build(prefix);
    }
}
