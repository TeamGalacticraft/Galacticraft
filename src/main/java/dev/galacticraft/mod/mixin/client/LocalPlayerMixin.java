/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import com.mojang.authlib.GameProfile;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.ControllableEntity;
import dev.galacticraft.mod.content.item.RocketItem;
import dev.galacticraft.mod.network.packets.ControlEntityPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Shadow
    @Final
    protected Minecraft minecraft;
    @Shadow
    public Input input;

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(at = @At("RETURN"), method = "aiStep")
    private void gcRocketJumpCheck(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (player.isPassenger()) {
            if (player.getVehicle() instanceof Rocket rocket) {

                if (this.input.jumping && rocket.getLaunchStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                    rocket.onJump();
                    ClientPlayNetworking.send(Constant.Packet.ROCKET_JUMP, PacketByteBufs.create());
                }

                float turnFactor = 2.0F;
                float angle = 45;

                if (rocket.getLaunchStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    if (this.input.up) {
                        player.getVehicle().setXRot(Math.min(Math.max(player.getVehicle().getXRot() - 0.5F * turnFactor, -angle), angle));
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

    @Inject(method = "isCrouching", at = @At("HEAD"), cancellable = true)
    private void gc$isCrouching(CallbackInfoReturnable<Boolean> cir) {
        if (this.isHoldingRocket()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isShiftKeyDown", at = @At("HEAD"), cancellable = true)
    private void gc$isShiftKeyDown(CallbackInfoReturnable<Boolean> cir) {
        if (this.isHoldingRocket()) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean isHoldingRocket() {
        return this.getVehicle() == null && (this.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RocketItem || this.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RocketItem);
    }

    @Inject(method = "move", at = @At("TAIL"))
    private void gc$footprints(MoverType type, Vec3 motion, CallbackInfo ci) {

    }
}