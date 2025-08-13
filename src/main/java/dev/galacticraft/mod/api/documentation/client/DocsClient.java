package dev.galacticraft.mod.api.documentation.client;

import dev.galacticraft.mod.api.documentation.DocsApi;
import dev.galacticraft.mod.api.documentation.reload.DocsReloadListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public final class DocsClient implements ClientModInitializer {
    public static KeyMapping INSPECT;

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new DocsReloadListener());

        INSPECT = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gc.inspect", GLFW.GLFW_KEY_G, "key.categories.galacticraft"));

        ItemTooltipCallback.EVENT.register((stack, ctx, flag, lines) -> {
            var page = DocsApi.pageForItem(stack.getItem());
            if (page != null) {
                lines.add(Component.translatable("tooltip.gc.inspect_hint", INSPECT.getTranslatedKeyMessage())
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        });

        ScreenEvents.BEFORE_INIT.register((client, screen, w, h) -> {
            ScreenKeyboardEvents.afterKeyPress(screen).register((scr, keyCode, scanCode, modifiers) -> {
                if (!INSPECT.matches(keyCode, scanCode)) return;

                if (scr instanceof AbstractContainerScreen<?> inv) {
                    Slot hovered = ((HoveredSlotAccessor) inv).gc$getHoveredSlot();
                    if (hovered != null && hovered.hasItem()) {
                        var page = DocsApi.pageForItem(hovered.getItem().getItem());
                        if (page != null) {
                            DocsApi.open(page);
                            return;
                        }
                    }
                }
                DocsApi.openHome();
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.screen != null) return;
            while (INSPECT.consumeClick()) {
                if (!tryOpenFromHovered(client)) DocsApi.openHome();
            }
        });
    }

    private static boolean tryOpenFromHovered(Minecraft client) {
        if (!(client.screen instanceof AbstractContainerScreen<?> s)) return false;
        Slot hovered = ((HoveredSlotAccessor) s).gc$getHoveredSlot();
        if (hovered == null || !hovered.hasItem()) return false;
        ItemStack st = hovered.getItem();
        var page = DocsApi.pageForItem(st.getItem());
        if (page == null) return false;
        DocsApi.open(page);
        return true;
    }
}