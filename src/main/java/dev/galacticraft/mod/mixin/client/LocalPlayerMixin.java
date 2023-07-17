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

import com.mojang.authlib.GameProfile;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.render.misc.FootprintRenderer;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.entity.LanderEntity;
import dev.galacticraft.mod.content.item.RocketItem;
import dev.galacticraft.mod.world.dimension.GCDimensions;
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
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    @Shadow @Final protected Minecraft minecraft;
    @Shadow public Input input;

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

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
            if (player.getVehicle() instanceof LanderEntity lander) {
                if (lander.onGround()) {
                    return;
                }

                float turnFactor = 2.0F;
                float angle = 45;

                    if (this.input.up) {
                        lander.setXRot(Math.min(Math.max(lander.getXRot() - 0.5F * turnFactor, -angle), angle));
                        ClientPlayNetworking.send(Constant.Packet.LANDER_PITCH, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(false)));
                    }

                    if (this.input.down) {
                        lander.setXRot(Math.min(Math.max(lander.getXRot() + 0.5F * turnFactor, -angle), angle));
                        ClientPlayNetworking.send(Constant.Packet.LANDER_PITCH, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(true)));
                    }

                    if (this.input.right) {
                        lander.setYRot(lander.getYRot() - 0.5F * turnFactor);
                        ClientPlayNetworking.send(Constant.Packet.LANDER_YAW, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(false)));
                    }

                    if (this.input.left) {
                        lander.setYRot(lander.getYRot() + 0.5F * turnFactor);
                        ClientPlayNetworking.send(Constant.Packet.LANDER_YAW, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(true)));
                    }

                    if (this.input.jumping) {
                        Vec3 deltaMovement = lander.getDeltaMovement();
                        lander.setDeltaMovement(new Vec3(deltaMovement.x(), Math.min(deltaMovement.y() + 0.03F, lander.getY() < 90 ? -0.15 : -1.0), deltaMovement.z()));
                        ClientPlayNetworking.send(Constant.Packet.LANDER_ACCERLERATE, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(false)));
                    }
                    if (this.input.shiftKeyDown) {
                        Vec3 deltaMovement = lander.getDeltaMovement();
                        lander.setDeltaMovement(new Vec3(deltaMovement.x(), Math.min(deltaMovement.y() - 0.022F, -1.0), deltaMovement.z()));
                        ClientPlayNetworking.send(Constant.Packet.LANDER_ACCERLERATE, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(true)));
                    }

            }
        }
    }

    @Inject(method = "isCrouching", at = @At("HEAD"), cancellable = true)
    private void isHoldingRocket(CallbackInfoReturnable<Boolean> cir) {
        if (getVehicle() == null) {
            if (getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RocketItem || getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RocketItem) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "move", at = @At("TAIL"))
    private void addFootprints(MoverType moverType, Vec3 motion, CallbackInfo ci) {
        updateFeet((LocalPlayer) (Object) this, motion);
    }

    public double distanceSinceLastStep;
    public int lastStep;

    private void updateFeet(LocalPlayer player, Vec3 motion) {
        double motionSqrd = motion.x() * motion.x() + motion.z() * motion.z();

        // If the player is on the moon, not airbourne and not riding anything
        if (motionSqrd > 0.001 && player.level() != null && player.level().dimension().location().equals(GCDimensions.MOON.location()) && player.getVehicle() == null && !player.getAbilities().flying)
        {
            int iPosX = Mth.floor(player.getX());
            int iPosY = Mth.floor(player.getY() - 0.05);
            int iPosZ = Mth.floor(player.getZ());
            BlockPos pos1 = new BlockPos(iPosX, iPosY, iPosZ);
            BlockState state = player.level().getBlockState(pos1);


            // If the block below is the moon block
            if (state.is(GCBlocks.MOON_TURF)) {
                // If it has been long enough since the last step
                if (distanceSinceLastStep > 0.35)
                {
                    Vector3d pos = new Vector3d(player.getX(), player.getY(), player.getZ());
                    // Set the footprint position to the block below and add
                    // random number to stop z-fighting
                    pos.y = Mth.floor(player.getY()) + player.getRandom().nextFloat() / 100.0F;

                    // Adjust footprint to left or right depending on step
                    // count
                    switch (lastStep) {
                        case 0:
                            pos.add(Math.sin(Math.toRadians(-player.getYRot() + 90)) * 0.25, 0, Math.cos(Math.toRadians(-player.getYRot() + 90)) * 0.25);
                            break;
                        case 1:
                            pos.add(Math.sin(Math.toRadians(-player.getYRot() - 90)) * 0.25, 0, Math.cos(Math.toRadians(-player.getYRot() - 90)) * 0.25);
                            break;
                    }

                    pos = FootprintRenderer.getFootprintPosition(player.level(), player.getYRot() - 180, pos, player.getOnPos());

                    long chunkKey = ChunkPos.asLong(Mth.floor(pos.x()) >> 4, Mth.floor(pos.z()) >> 4);
                    FootprintRenderer.addFootprint(chunkKey, player.level().dimension(), pos, player.getYRot(), player.getGameProfile().getName());

                    // Increment and cap step counter at 1
                    lastStep = (lastStep + 1) % 2;
                    distanceSinceLastStep = 0;
                } else
                {
                    distanceSinceLastStep = distanceSinceLastStep + motionSqrd;
                }
            }
        }
    }
}
