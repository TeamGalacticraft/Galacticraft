package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.rocket.LaunchStage;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Shadow public Input input;

    @Inject(at=@At("RETURN"), method = "tickMovement")
    private void gcRocketJumpCheck(CallbackInfo ci) {
        if (((ClientPlayerEntity) (Object)this).hasVehicle()) {
            if (((ClientPlayerEntity) (Object)this).getVehicle() instanceof RocketEntity) {
                if (this.input.jumping) {
                    ((RocketEntity) ((ClientPlayerEntity) (Object) this).getVehicle()).onJump();
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_jump"), new PacketByteBuf(Unpooled.buffer())));
                }

                if (((RocketEntity) ((ClientPlayerEntity) (Object) this).getVehicle()).getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    if (this.input.pressingForward) {
                        ((ClientPlayerEntity) (Object) this).getVehicle().pitch = (((ClientPlayerEntity) (Object) this).getVehicle().pitch - 2.0F) % 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_pitch_update"), new PacketByteBuf(Unpooled.buffer().writeByte((int) (((ClientPlayerEntity) (Object) this).getVehicle().pitch / 360F * 256F)))));
                    } else if (this.input.pressingBack) {
                        ((ClientPlayerEntity) (Object) this).getVehicle().pitch = (((ClientPlayerEntity) (Object) this).getVehicle().pitch + 2.0F) % 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_pitch_update"), new PacketByteBuf(Unpooled.buffer().writeByte((int) (((ClientPlayerEntity) (Object) this).getVehicle().pitch / 360F * 256F)))));
                    }

                    if (this.input.pressingLeft) {
                        ((ClientPlayerEntity) (Object) this).getVehicle().yaw = (((ClientPlayerEntity) (Object) this).getVehicle().yaw - 2.0F) % 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_yaw_update"), new PacketByteBuf(Unpooled.buffer().writeByte((int) (((ClientPlayerEntity) (Object) this).getVehicle().yaw / 360F * 256F)))));
                    } else if (this.input.pressingRight) {
                        ((ClientPlayerEntity) (Object) this).getVehicle().yaw = (((ClientPlayerEntity) (Object) this).getVehicle().yaw + 2.0F) % 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_yaw_update"), new PacketByteBuf(Unpooled.buffer().writeByte((int) (((ClientPlayerEntity) (Object) this).getVehicle().yaw / 360F * 256F)))));

                    }
                }
            }
        }
    }
}
