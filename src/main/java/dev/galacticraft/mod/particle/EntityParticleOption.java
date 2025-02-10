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

package dev.galacticraft.mod.particle;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntityParticleOption implements ParticleOptions {
    private final ParticleType<?> type;
    private final UUID uuid;

    public EntityParticleOption(ParticleType<?> type, UUID uuid) {
        this.type = type;
        this.uuid = uuid;
    }

    public static MapCodec<EntityParticleOption> codec(ParticleType<? extends EntityParticleOption> type) {
        return UUIDUtil.CODEC.xmap(uuid -> new EntityParticleOption(type, uuid), o -> o.uuid).fieldOf("uuid");
    }

    public static StreamCodec<ByteBuf, EntityParticleOption> streamCodec(ParticleType<?> type) {
        return UUIDUtil.STREAM_CODEC.map(uuid -> new EntityParticleOption(type, uuid), o -> o.uuid);
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return this.type;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
