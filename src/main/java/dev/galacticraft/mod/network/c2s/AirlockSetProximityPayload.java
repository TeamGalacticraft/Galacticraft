package dev.galacticraft.mod.network.c2s;

import dev.galacticraft.impl.network.c2s.C2SPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.AirlockControllerBlockEntity;
import dev.galacticraft.mod.screen.AirlockControllerMenu;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record AirlockSetProximityPayload(byte proximity) implements C2SPayload {
    public static final StreamCodec<ByteBuf, AirlockSetProximityPayload> STREAM_CODEC =
            ByteBufCodecs.BYTE.map(AirlockSetProximityPayload::new, AirlockSetProximityPayload::proximity);
    public static final ResourceLocation ID = Constant.id("airlock_set_proximity");
    public static final CustomPacketPayload.Type<AirlockSetProximityPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    @Override
    public void handle(ServerPlayNetworking.@NotNull Context context) {
        if (context.player().containerMenu instanceof AirlockControllerMenu menu) {
            AirlockControllerBlockEntity be = menu.be;
            if (be != null && be.getLevel() != null && be.getLevel().isLoaded(be.getBlockPos())) {
                if (!(be instanceof dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity mb)
                        || mb.getSecurity().hasAccess(context.player())) {
                    // clamp 0..5 on the server just in case
                    byte clamped = (byte) Math.max(0, Math.min(5, this.proximity));
                    be.setProximityOpen(clamped);
                    be.setChanged();
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}