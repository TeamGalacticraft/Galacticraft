package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.accessor.MinecraftServerAccessor;
import com.hrznstudio.galacticraft.accessor.ServerPlayerEntityAccessor;
import com.hrznstudio.galacticraft.api.research.PlayerResearchTracker;
import com.hrznstudio.galacticraft.api.research.ResearchNode;
import io.netty.buffer.Unpooled;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "remove", at = @At("RETURN"))
    private void removeResearchTracker(ServerPlayerEntity player, CallbackInfo ci) {
        ((ServerPlayerEntityAccessor) player).getResearchTracker().clearCriteria();
        ((MinecraftServerAccessor) player.getServer()).removeResearchTracker(player.getUuid());
    }

    @Inject(method = "savePlayerData", at = @At("RETURN"))
    private void saveresearch(ServerPlayerEntity player, CallbackInfo ci) {
        if (((ServerPlayerEntityAccessor) player).getResearchTracker() != null) {
            ((ServerPlayerEntityAccessor) player).getResearchTracker().save();
        }
    }

    @Redirect(method = "onDataPacksReloaded", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;reload(Lnet/minecraft/server/ServerAdvancementLoader;)V"))
    private void saveresearch(PlayerAdvancementTracker playerAdvancementTracker, ServerAdvancementLoader advancementLoader) {
        if (playerAdvancementTracker instanceof PlayerResearchTracker) {
            ((PlayerResearchTracker)playerAdvancementTracker).reload();
        } else {
            playerAdvancementTracker.reload(advancementLoader);
        }
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
//        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "research_sync"), buf));
    }
}
