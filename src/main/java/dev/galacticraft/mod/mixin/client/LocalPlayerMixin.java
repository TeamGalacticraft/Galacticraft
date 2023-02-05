/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.mod.Constant;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow @Final protected Minecraft minecraft;
    @Shadow public Input input;

    @Inject(at=@At("RETURN"), method = "aiStep")
    private void gcRocketJumpCheck(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (player.isPassenger()) {
            if (player.getVehicle() instanceof Rocket rocket) {

                if (this.input.jumping && rocket.getLaunchStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                    rocket.onJump();
                    ClientPlayNetworking.send(Constant.Packet.ROCKET_JUMP, PacketByteBufs.create());
                }

                if (rocket.getLaunchStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    if (this.input.up) {
                        player.getVehicle().setXRot((player.getVehicle().getXRot() - 2.0F) % 360.0f);
                        ClientPlayNetworking.send(Constant.Packet.ROCKET_PITCH, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(false)));
                    } else if (this.input.down) {
                        player.getVehicle().setXRot((player.getVehicle().getXRot() + 2.0F) % 360.0f);
                        ClientPlayNetworking.send(Constant.Packet.ROCKET_PITCH, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(true)));
                    }

                    if (this.input.left) {
                        player.getVehicle().setYRot((player.getVehicle().getYRot() - 2.0F) % 360.0f);
                        ClientPlayNetworking.send(Constant.Packet.ROCKET_YAW, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(false)));
                    } else if (this.input.right) {
                        player.getVehicle().setYRot((player.getVehicle().getYRot() + 2.0F) % 360.0f);
                        ClientPlayNetworking.send(Constant.Packet.ROCKET_YAW, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(true)));
                    }
                }
            }
        }
    }
}
