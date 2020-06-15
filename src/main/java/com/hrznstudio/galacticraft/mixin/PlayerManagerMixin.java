package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.accessor.ServerPlayerEntityAccessor;
import com.hrznstudio.galacticraft.api.research.ResearchNode;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "remove", at = @At("RETURN"))
    private void removeResearchTracker(ServerPlayerEntity player, CallbackInfo ci) {
        ((ServerPlayerEntityAccessor) player).getResearchTracker().clearCriteria();
    }

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void syncResearch(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(((ServerPlayerEntityAccessor) player).getResearchTracker().getResearchLoader().getManager().getResearch().size());
        Map<Identifier, ResearchNode> map =((ServerPlayerEntityAccessor) player).getResearchTracker().getResearchLoader().getManager().getResearch();
        for (Map.Entry<Identifier, ResearchNode> entry : map.entrySet()) {
            buf.writeIdentifier(entry.getKey());
            entry.getValue().toBuilder().toPacket(buf);
        }
        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "research_sync"), buf));
    }
}
