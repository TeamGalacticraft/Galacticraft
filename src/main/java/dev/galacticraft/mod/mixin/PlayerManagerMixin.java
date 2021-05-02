package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.GearInventoryProvider;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void gc_syncInv(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        CompoundTag tag = ((GearInventoryProvider) player).writeGearToNbt(new CompoundTag());
        for (ServerPlayerEntity player1 : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(player1, new Identifier(Constant.MOD_ID, "gear_inv_sync_full"), new PacketByteBuf(Unpooled.buffer().writeInt(player.getEntityId())).writeCompoundTag(tag));
        }
    }
}
