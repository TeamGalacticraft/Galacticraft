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
import dev.galacticraft.mod.misc.cape.CapeMode;
import dev.galacticraft.mod.misc.cape.CapesClientState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayer.class)
@Environment(EnvType.CLIENT)
public abstract class AbstractClientPlayerMixin {
    @ModifyReturnValue(method = "getSkin", at = @At("RETURN"))
    private PlayerSkin gc$injectCape(PlayerSkin original) {
        CapesClientState.Entry entry = CapesClientState.forPlayer((AbstractClientPlayer)(Object)this);
        if (entry == null) return original;

        if (entry.mode == CapeMode.OFF) {
            return new PlayerSkin(
                    original.texture(), original.textureUrl(),
                    null,
                    original.elytraTexture(),
                    original.model(), original.secure()
            );
        } else if (entry.mode == CapeMode.GC && entry.gcCapeId != null) {
            var rl = CapesClientState.gcCapeTexture(entry.gcCapeId);
            if (rl != null) {
                return new PlayerSkin(
                        original.texture(), original.textureUrl(),
                        rl,
                        original.elytraTexture(),
                        original.model(), original.secure()
                );
            }
        }
        return original;
    }
}