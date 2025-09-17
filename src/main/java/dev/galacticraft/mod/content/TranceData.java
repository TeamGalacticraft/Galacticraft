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

package dev.galacticraft.mod.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class TranceData {
    public enum Stage { NAUSEA, HALLUCINATE }

    public boolean active = false;
    public @Nullable Stage stage = null;

    public @Nullable ResourceKey<Level> targetDim = null;
    public @Nullable BlockPos targetViewPos = null;

    public long hallucinateUntilGameTime = 0L;

    // Optional persistence (works with AttachmentType.persistent)
    public static final Codec<TranceData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.fieldOf("active").forGetter(d -> d.active),
            Codec.STRING.optionalFieldOf("stage").xmap(
                    opt -> opt.map(Stage::valueOf).orElse(null),
                    st -> java.util.Optional.ofNullable(st == null ? null : st.name())
            ).forGetter(d -> d.stage),
            Level.RESOURCE_KEY_CODEC.optionalFieldOf("target_dim").forGetter(d -> java.util.Optional.ofNullable(d.targetDim)),
            BlockPos.CODEC.optionalFieldOf("target_view_pos").forGetter(d -> java.util.Optional.ofNullable(d.targetViewPos)),
            Codec.LONG.fieldOf("hallucinate_until").forGetter(d -> d.hallucinateUntilGameTime)
    ).apply(i, (active, stage, dim, pos, until) -> {
        TranceData d = new TranceData();
        d.active = active;
        d.stage = stage;
        d.targetDim = dim.orElse(null);
        d.targetViewPos = pos.orElse(null);
        d.hallucinateUntilGameTime = until;
        return d;
    }));
}