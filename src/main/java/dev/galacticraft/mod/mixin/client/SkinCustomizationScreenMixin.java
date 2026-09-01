/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import com.llamalad7.mixinextras.sugar.Local;
import dev.galacticraft.mod.misc.cape.CapesClientRole;
import dev.galacticraft.mod.screen.CapeRootScreen;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;


@Environment(EnvType.CLIENT)
@Mixin(SkinCustomizationScreen.class)
public abstract class SkinCustomizationScreenMixin extends Screen {
    protected SkinCustomizationScreenMixin(Component title) { super(title); }

    @Inject(
            method = "addOptions",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER)
    )
    private void gc$swapCapeToggle(CallbackInfo ci, @Local List<AbstractWidget> list) {
        CapesClientRole.ensureLoadedAsync();
        if (!CapesClientRole.isEligibleClient()) return;
        if (list.isEmpty()) return;

        AbstractWidget last = list.get(list.size() - 1);
        if (!(last instanceof CycleButton<?> cb)) return;

        String label = cb.getMessage().getString().toLowerCase(Locale.ROOT);
        if (!label.contains("cape")) return;

        list.removeLast();
        list.add(
                Button.builder(Component.translatable(Translations.Ui.CAPE_BUTTON),b ->
                                Minecraft.getInstance().setScreen(new CapeRootScreen((SkinCustomizationScreen)(Object)this)))
                        .width(150)
                        .build()
        );
    }
}