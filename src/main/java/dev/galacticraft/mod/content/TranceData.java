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