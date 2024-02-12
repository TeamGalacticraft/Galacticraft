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
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;

import java.util.Locale;
import java.util.UUID;

public class EntityParticleOption implements ParticleOptions {
    public static final Codec<EntityParticleOption> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particle").forGetter(EntityParticleOption::getType),
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(EntityParticleOption::getEntityUUID)
            ).apply(instance, EntityParticleOption::new)
    );

    public static final ParticleOptions.Deserializer<EntityParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public EntityParticleOption fromCommand(ParticleType<EntityParticleOption> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new EntityParticleOption(particleTypeIn, UUID.fromString(reader.readString()));
        }

        @Override
        public EntityParticleOption fromNetwork(ParticleType<EntityParticleOption> particleTypeIn, FriendlyByteBuf buffer) {
            return new EntityParticleOption(particleTypeIn, buffer.readUUID());
        }
    };

    private final ParticleType<?> particleType;
    private final UUID entityUUID;

    public EntityParticleOption(ParticleType<?> particleType, UUID entityUUID) {
        this.particleType = particleType;
        this.entityUUID = entityUUID;
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.entityUUID != null);
        if (this.entityUUID != null) {
            buffer.writeUUID(this.entityUUID);
        }
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %s", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.entityUUID.toString());
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }
}
