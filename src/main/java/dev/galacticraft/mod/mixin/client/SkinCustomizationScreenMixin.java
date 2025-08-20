package dev.galacticraft.mod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.galacticraft.mod.misc.cape.CapesClientRole;
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
        // Make sure the roles start loading in background; gate UI on eligibility
        CapesClientRole.ensureLoadedAsync();
        if (!CapesClientRole.isEligibleClient()) return;
        if (list.isEmpty()) return;

        // The widget just added by the loop is the last one
        AbstractWidget last = list.get(list.size() - 1);
        if (!(last instanceof CycleButton<?> cb)) return;

        // Heuristic: the label for the CAPE row contains "cape" in most locales
        String label = cb.getMessage().getString().toLowerCase(Locale.ROOT);
        if (!label.contains("cape")) return;

        // Replace the vanilla "Capes: On/Off" with a single "Cape" button
        list.remove(list.size() - 1);
        list.add(
                Button.builder(Component.translatable("galacticraft.options.cape"), b ->
                        {})
                        // Size is ignored by OptionsList.addSmall; it will place two per row automatically
                        .width(150)
                        .build()
        );
    }
}