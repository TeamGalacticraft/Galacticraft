/*
 * Copyright (c) 2020 Team Galacticraft
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

import alexiil.mc.lib.attributes.item.FixedItemInv;
import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.accessor.GearInventoryProvider;
import dev.galacticraft.mod.attribute.GalacticraftAttributes;
import dev.galacticraft.mod.attribute.oxygen.InfiniteOxygenTank;
import dev.galacticraft.mod.attribute.oxygen.OxygenTank;
import dev.galacticraft.mod.util.DrawableUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public abstract class InGameHudMixin extends DrawableHelper implements DrawableUtils {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;color4f(FFFF)V", shift = At.Shift.AFTER, ordinal = 0))
    private void draw(MatrixStack matrices, float delta, CallbackInfo ci) {
        if (CelestialBodyType.getByDimType(client.player.world.getRegistryKey()).isPresent() && !CelestialBodyType.getByDimType(client.player.world.getRegistryKey()).get().getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN)) {
            fill(matrices, this.client.getWindow().getScaledWidth() - (Constants.TextureCoordinate.OVERLAY_WIDTH * 2) - 11, 4, this.client.getWindow().getScaledWidth() - Constants.TextureCoordinate.OVERLAY_WIDTH - 9, 6 + Constants.TextureCoordinate.OVERLAY_HEIGHT, 0);
            fill(matrices, this.client.getWindow().getScaledWidth() - Constants.TextureCoordinate.OVERLAY_WIDTH - 6, 4, this.client.getWindow().getScaledWidth() - 4, 6 + Constants.TextureCoordinate.OVERLAY_HEIGHT, 0);

            client.getTextureManager().bindTexture(Constants.ScreenTexture.OVERLAY);
            FixedItemInv inv = ((GearInventoryProvider) client.player).getGearInv();
            OxygenTank tank = GalacticraftAttributes.OXYGEN_TANK_ATTRIBUTE.getFirst(inv.getSlot(7));
            if (client.player.isCreative() && tank.getCapacity() == 0) tank = InfiniteOxygenTank.INSTANCE;
            this.drawOxygenBuffer(matrices, this.client.getWindow().getScaledWidth() - Constants.TextureCoordinate.OVERLAY_WIDTH - 5, 5, this.getZOffset(), tank.getAmount(), tank.getCapacity());
            tank = GalacticraftAttributes.OXYGEN_TANK_ATTRIBUTE.getFirst(inv.getSlot(6));
            if (client.player.isCreative() && tank.getCapacity() == 0) tank = InfiniteOxygenTank.INSTANCE;
            this.drawOxygenBuffer(matrices, this.client.getWindow().getScaledWidth() - (Constants.TextureCoordinate.OVERLAY_WIDTH * 2) - 10, 5, this.getZOffset(), tank.getAmount(), tank.getCapacity());
        }
    }
}
