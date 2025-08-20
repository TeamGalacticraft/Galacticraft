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
import net.minecraft.world.entity.player.PlayerModelPart;
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

        list.remove(list.size() - 1);
        list.add(
                Button.builder(Component.translatable(Translations.Ui.CAPE_BUTTON),b ->
                                Minecraft.getInstance().setScreen(new CapeRootScreen((SkinCustomizationScreen)(Object)this)))
                        .width(150)
                        .build()
        );
    }
}