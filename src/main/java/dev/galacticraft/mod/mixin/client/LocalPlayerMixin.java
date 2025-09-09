/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import dev.galacticraft.api.entity.ControllableEntity;
import dev.galacticraft.api.entity.IgnoreShift;
import dev.galacticraft.mod.content.entity.vehicle.AdvancedVehicle;
import dev.galacticraft.mod.content.entity.vehicle.RocketEntity;
import dev.galacticraft.mod.content.entity.vehicle.AbstractLanderEntity;
import dev.galacticraft.mod.content.item.RocketItem;
import dev.galacticraft.mod.network.c2s.ControlEntityPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
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

    @Inject(method = "aiStep", at = @At("RETURN"))
    private void gcRocketJumpCheck(CallbackInfo ci) {
        if (this.isPassenger() && this.getVehicle() instanceof ControllableEntity controllable) {
            boolean invertControls = this.minecraft.options.getCameraType().isFirstPerson();
            controllable.inputTick(input.leftImpulse, input.forwardImpulse, input.up, input.down, input.left, input.right, input.jumping, input.shiftKeyDown, invertControls);
            ClientPlayNetworking.send(new ControlEntityPayload(input.leftImpulse, input.forwardImpulse, input.up, input.down, input.left, input.right, input.jumping, input.shiftKeyDown, invertControls));
        }
    }

    @ModifyReturnValue(method = "isCrouching", at = @At("RETURN"))
    private boolean gc$isCrouching(boolean original) {
        if (this.isPassenger() && this.getVehicle() instanceof IgnoreShift) {
            return false;
        }
        return this.isHoldingRocket() || original;
    }

    @ModifyReturnValue(method = "isShiftKeyDown", at = @At("RETURN"))
    private boolean gc$isShiftKeyDown(boolean original) {
        if (this.isPassenger() && this.getVehicle() instanceof IgnoreShift) {
            return false;
        }
        return this.isHoldingRocket() || original;
    }

    @Unique
    private boolean isHoldingRocket() {
        return this.getVehicle() == null && (this.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RocketItem || this.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RocketItem);
    }

    @Inject(method = "move", at = @At("TAIL"))
    private void gc$footprints(MoverType type, Vec3 motion, CallbackInfo ci) {

    }

    @Inject(method = "startRiding", at = @At("TAIL"), cancellable = true)
    private void gc$enterAdvancedVehicle(Entity vehicle, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (vehicle instanceof RocketEntity) {
            this.minecraft.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
        } else if (vehicle instanceof AbstractLanderEntity) {
            this.minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"), cancellable = true)
    private void gc$exitAdvancedVehicle(CallbackInfo ci) {
        Entity vehicle = this.getVehicle();
        if (vehicle instanceof AdvancedVehicle) {
            this.minecraft.options.setCameraType(CameraType.FIRST_PERSON);
        }
    }
}