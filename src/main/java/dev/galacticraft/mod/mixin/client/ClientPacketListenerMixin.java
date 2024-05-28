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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.galacticraft.api.entity.IgnoreShift;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @WrapWithCondition(method = "handleSetEntityPassengersPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private boolean shouldShowOverylay(Gui instance, Component message, boolean tinted, @Local(index = 2) Entity vehicle) {
        if (vehicle instanceof IgnoreShift ignoreShift)
            return !ignoreShift.shouldIgnoreShiftExit();
        return true;
    }

    @WrapWithCondition(method = "handleSetEntityPassengersPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GameNarrator;sayNow(Lnet/minecraft/network/chat/Component;)V"))
    private boolean shouldSayNow(GameNarrator instance, Component message, @Local(index = 2) Entity vehicle) {
        if (vehicle instanceof IgnoreShift ignoreShift)
            return !ignoreShift.shouldIgnoreShiftExit();
        return true;
    }
}
