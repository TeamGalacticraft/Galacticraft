package dev.galacticraft.impl.network.s2c;

import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.StreamCodecs;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

public record OxygenUpdatePayload(long chunk, OxygenData[] data) implements S2CPayload {
    public static final ResourceLocation ID = Constant.id("oxygen_update");
    public static final Type<OxygenUpdatePayload> TYPE = new Type<>(ID);
    public static final StreamCodec<ByteBuf, OxygenUpdatePayload> CODEC = StreamCodec.composite(
            StreamCodecs.LONG,
            d -> d.chunk,
            StreamCodecs.array(OxygenData.CODEC, OxygenData[]::new),
            d -> d.data,
            OxygenUpdatePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(ClientPlayNetworking.@NotNull Context context) {
        LevelChunk chunk = context.client().level.getChunk(ChunkPos.getX(this.chunk), ChunkPos.getZ(this.chunk));
        for (OxygenData datum : this.data) {
            ChunkSectionOxygenAccessor accessor = (ChunkSectionOxygenAccessor)chunk.getSection(datum.section);
            accessor.galacticraft$setBits(datum.data);
        }
    }

    public record OxygenData(byte section, @NotNull BitSet data) {
        private static final StreamCodec<ByteBuf, BitSet> BIT_SET_CODEC = ByteBufCodecs.BYTE_ARRAY.map(BitSet::valueOf, BitSet::toByteArray);
        public static final StreamCodec<ByteBuf, OxygenData> CODEC = StreamCodec.composite(
                ByteBufCodecs.BYTE,
                d -> d.section,
                BIT_SET_CODEC,
                d -> d.data,
                OxygenData::new
        );
    }
}
