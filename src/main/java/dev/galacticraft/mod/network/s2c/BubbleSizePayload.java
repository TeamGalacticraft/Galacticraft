package dev.galacticraft.mod.network.s2c;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record BubbleSizePayload(BlockPos pos, double size) implements S2CPayload {
    public static final StreamCodec<ByteBuf, BubbleSizePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            p -> p.pos,
            ByteBufCodecs.DOUBLE,
            p -> p.size,
            BubbleSizePayload::new
    );

    public static final ResourceLocation ID = Constant.id("bubble_size");
    public static final CustomPacketPayload.Type<BubbleSizePayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public BubbleSizePayload {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }
    }

    @Override
    public void handle(@NotNull ClientPlayNetworking.Context context) {
        ClientLevel level = context.client().level;
        if (level != null && level.hasChunk(SectionPos.blockToSectionCoord(this.pos().getX()), SectionPos.blockToSectionCoord(this.pos().getZ()))) {
            BlockEntity entity = level.getBlockEntity(this.pos());
            if (entity instanceof OxygenBubbleDistributorBlockEntity machine) {
                machine.setSize(this.size());
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
