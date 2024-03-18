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

package dev.galacticraft.mod.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public record ScaleParticleType(float scale) implements ParticleOptions {
    public static final Codec<ScaleParticleType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("scale").forGetter(type -> type.scale)
    ).apply(instance, ScaleParticleType::new));
    public static final Deserializer<ScaleParticleType> DESERIALIZER = new Deserializer<>() {
        @Override
        public ScaleParticleType fromCommand(ParticleType<ScaleParticleType> particleType, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ScaleParticleType(reader.readFloat());
        }

        @Override
        public ScaleParticleType fromNetwork(ParticleType<ScaleParticleType> particleType, FriendlyByteBuf buffer) {
            return new ScaleParticleType(buffer.readFloat());
        }
    };

    @Override
    public ParticleType<?> getType() {
        return GCParticleTypes.ACID_VAPOR_PARTICLE; // TODO: make this more general purpose
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.scale);
    }

    @Override
    public String writeToString() {
        return String.format(
                Locale.ROOT, "%s %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.scale
        );
    }
}
