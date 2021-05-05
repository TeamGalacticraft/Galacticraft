/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import dev.galacticraft.api.celestialbody.CelestialBodyType;
import dev.galacticraft.api.entity.Rocket;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.GearInventoryProvider;
import dev.galacticraft.mod.accessor.SoundSystemAccessor;
import dev.galacticraft.mod.item.GalacticraftItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements GearInventoryProvider {
    private final @Unique FullFixedItemInv gearInv = createInv();
    @Shadow public Input input;

    private FullFixedItemInv createInv() {
        FullFixedItemInv inv = new FullFixedItemInv(12);
        inv.setOwnerListener((invView, slot, prev, current) -> {
            if (current.getItem() == GalacticraftItem.FREQUENCY_MODULE) {
                ((SoundSystemAccessor) MinecraftClient.getInstance().getSoundManager().soundSystem).gc_updateAtmosphericMultiplier(1.0f);
            } else if (prev.getItem() == GalacticraftItem.FREQUENCY_MODULE) {
                boolean hasFreqModule = false;
                for (int i = 0; i < invView.getSlotCount(); i++) {
                    if (i == slot) continue;
                    if (invView.getInvStack(i).getItem() == GalacticraftItem.FREQUENCY_MODULE) {
                        ((SoundSystemAccessor) MinecraftClient.getInstance().getSoundManager().soundSystem).gc_updateAtmosphericMultiplier(1.0f);
                        hasFreqModule = true;
                        break;
                    }
                }
                if (!hasFreqModule) {
                    ((SoundSystemAccessor) MinecraftClient.getInstance().getSoundManager().soundSystem)
                            .gc_updateAtmosphericMultiplier(CelestialBodyType.getByDimType(MinecraftClient.getInstance().world.getRegistryManager(), MinecraftClient.getInstance().world.getRegistryKey())
                                    .map(body -> body.getAtmosphere().getPressure()).orElse(1.0f));
                }
            }
        });
        return inv;
    }

    @Override
    public FullFixedItemInv getGearInv() {
        return this.gearInv;
    }

    @Override
    public CompoundTag writeGearToNbt(CompoundTag tag) {
        return this.getGearInv().toTag(tag);
    }

    @Override
    public void readGearFromNbt(CompoundTag tag) {
        this.getGearInv().fromTag(tag);
    }

    @Inject(at=@At("RETURN"), method = "tickMovement")
    private void gcRocketJumpCheck(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (player.hasVehicle()) {
            if (player.getVehicle() instanceof Rocket) {

                if (this.input.jumping && ((Rocket) player.getVehicle()).getStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                    ((Rocket) player.getVehicle()).onJump();
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "rocket_jump"), new PacketByteBuf(Unpooled.buffer())));
                }

                if (((Rocket) player.getVehicle()).getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    if (this.input.pressingForward) {
                        player.getVehicle().prevPitch = player.getVehicle().pitch;
                        player.getVehicle().pitch -= 2.0F;
                        player.getVehicle().pitch %= 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "rocket_pitch"), new PacketByteBuf(Unpooled.buffer().writeBoolean(false))));
                    } else if (this.input.pressingBack) {
                        player.getVehicle().prevPitch = player.getVehicle().pitch;
                        player.getVehicle().pitch += 2.0F;
                        player.getVehicle().pitch %= 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "rocket_pitch"), new PacketByteBuf(Unpooled.buffer().writeBoolean(true))));
                    }

                    if (this.input.pressingLeft) {
                        player.getVehicle().prevYaw = player.getVehicle().yaw;
                        player.getVehicle().yaw -= 2.0F;
                        player.getVehicle().yaw %= 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "rocket_yaw"), new PacketByteBuf(Unpooled.buffer().writeBoolean(false))));
                    } else if (this.input.pressingRight) {
                        player.getVehicle().prevYaw = player.getVehicle().yaw;
                        player.getVehicle().yaw += 2.0F;
                        player.getVehicle().yaw %= 360.0F;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "rocket_yaw"), new PacketByteBuf(Unpooled.buffer().writeBoolean(true))));

                    }
                }
            }
        }
    }

}
