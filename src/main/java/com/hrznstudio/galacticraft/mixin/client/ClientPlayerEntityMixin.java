package com.hrznstudio.galacticraft.mixin.client;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.entity.RocketEntity;
import com.hrznstudio.galacticraft.api.rocket.LaunchStage;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
@Environment(EnvType.CLIENT)
public class ClientPlayerEntityMixin {
    @Shadow public Input input;

    @Inject(at=@At("RETURN"), method = "tickMovement")
    private void gcRocketJumpCheck(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (player.hasVehicle()) {
            if (player.getVehicle() instanceof RocketEntity) {

                if (this.input.jumping && ((RocketEntity) player.getVehicle()).getStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                    ((RocketEntity) player.getVehicle()).onJump();
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_jump"), new PacketByteBuf(Unpooled.buffer())));
                }

                if (((RocketEntity) player.getVehicle()).getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    if (this.input.pressingForward) {
                        player.getVehicle().prevPitch = player.getVehicle().pitch;
                        player.getVehicle().pitch -= 2.0F;
                        player.getVehicle().pitch %= 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_pitch"), new PacketByteBuf(Unpooled.buffer().writeBoolean(false))));
                    } else if (this.input.pressingBack) {
                        player.getVehicle().prevPitch = player.getVehicle().pitch;
                        player.getVehicle().pitch += 2.0F;
                        player.getVehicle().pitch %= 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_pitch"), new PacketByteBuf(Unpooled.buffer().writeBoolean(true))));
                    }

                    if (this.input.pressingLeft) {
                        player.getVehicle().prevYaw = player.getVehicle().yaw;
                        player.getVehicle().yaw -= 2.0F;
                        player.getVehicle().yaw %= 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_yaw"), new PacketByteBuf(Unpooled.buffer().writeBoolean(false))));
                    } else if (this.input.pressingRight) {
                        player.getVehicle().prevYaw = player.getVehicle().yaw;
                        player.getVehicle().yaw += 2.0F;
                        player.getVehicle().yaw %= 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_yaw"), new PacketByteBuf(Unpooled.buffer().writeBoolean(true))));

                    }
                }
            }
        }
    }
}
