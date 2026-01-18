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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.GCCreativeGuiSlots;
import dev.galacticraft.mod.client.gui.widget.RadioButton;
import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.network.c2s.CreativeGcTransferItemPayload;
import dev.galacticraft.mod.screen.slot.AccessorySlot;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeScreenMixin extends EffectRenderingInventoryScreen implements GCCreativeGuiSlots {

    @Unique private static final ResourceLocation GC_GUIBG_TEX = Constant.id("textures/gui/creative_tab_inventory.png");

    @Shadow private static CreativeModeTab selectedTab;

    @Shadow protected abstract void selectTab(CreativeModeTab group);

    @Shadow @Nullable private Slot destroyItemSlot;
    @Shadow @Final private static SimpleContainer CONTAINER;
    @Unique private final List<AccessorySlot> gc$slots = new ArrayList<>();

    @Unique private RadioButton creativeSwitchButton;

    @Unique
    private boolean bGCInventory = false;

    @Unique
    public boolean isGCInventoryEnabled() {
        return bGCInventory;
    }

    @Unique
    private boolean isCreativeGearInvAllowed() {
        return Galacticraft.CONFIG.enableCreativeGearInv();
    }

    private CreativeScreenMixin(AbstractContainerMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(null, null, null);
    }

    @ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), index = 0)
    private ResourceLocation gc$changeBackground(ResourceLocation original) {
        return isGCInventoryEnabled() ? GC_GUIBG_TEX : original;
    }

    @ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"), index = 1)
    private int gc$shiftEntityLeft(int original) {
        return isGCInventoryEnabled() ? leftPos + 55 : original;
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        creativeSwitchButton = new RadioButton(0, 0);
    }

    @Inject(method = "selectTab", at = @At(value = "HEAD"))
    private void gc$selectTab(CreativeModeTab group, CallbackInfo ci) {
        if (group.getType() == CreativeModeTab.Type.INVENTORY) {
            if (isCreativeGearInvAllowed()) {
                creativeSwitchButton.setX(leftPos + 11);
                creativeSwitchButton.setY(topPos + 18);
                creativeSwitchButton.visible = true;
                creativeSwitchButton.radioButtonOnClick = () -> {
                    bGCInventory = creativeSwitchButton.getIsBottomButtonActive();
                    regenerateSlots();
                };
                addRenderableWidget(creativeSwitchButton);
            } else {
                creativeSwitchButton.visible = false;
                creativeSwitchButton.radioButtonOnClick = null;
            }
        } else {
            removeWidget(creativeSwitchButton);
            bGCInventory = false;
            gc$slots.clear();
            creativeSwitchButton.visible = false;
            creativeSwitchButton.setIsBottomButtonActive(false);
        }
    }

    @Inject(method = "resize", at = @At(value = "TAIL"))
    private void resize(Minecraft client, int width, int height, CallbackInfo ci) {
        if (isGCInventoryEnabled()) {
            regenerateSlots();
            creativeSwitchButton.setIsBottomButtonActive(true);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {

        //Item duplication support
        if (button == 2) {
            Slot slot = gc$findSlot(mouseX, mouseY);
            if (slot != null) {
                getMenu().setCarried(slot.getItem().copy());
            }
            return;
        }

        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            if (isGCInventoryEnabled()) {
                ItemStack carried = getMenu().getCarried();
                Slot slot = gc$findSlot(mouseX, mouseY);
                if (slot != null) {
                    //Quick stack
                    if (Screen.hasShiftDown()) {
                        gcTryQuickStackToPlayerInv(slot);
                    } else if (carried.isEmpty() || slot.mayPlace(carried)) {
                        getMenu().setCarried(slot.getItem());
                        slot.set(carried);
                        slot.setChanged();
                        ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, slot.getContainerSlot(), carried.isEmpty() ? 0 : 1, carried));
                    }
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }

    @Unique
    private void regenerateSlots() {
        gc$slots.clear();
        getMenu().slots.clear();
        if (isGCInventoryEnabled()) {
            generatePlayerInventorySlots();
            generateGCSlots();
        } else {
            selectTab(BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.INVENTORY));
        }
    }

    @Nullable
    @Unique
    private Slot gc$findSlot(double x, double y) {
        for (int i = 0; i < gc$slots.size(); ++i) {
            Slot slot = (Slot) gc$slots.get(i);
            if (isHovering(slot.x, slot.y, 16, 16, x, y) && slot.isActive()) {
                return slot;
            }
        }
        return null;
    }

    @Unique
    private void generateGCSlots() {
        int yOffset = 6;

        for (int i = 0; i < 8; i++) {
            int column = i % 4;
            int row = 1 - (i / 4);
            generateGCSlot(99 + column * 18, yOffset + row * 27, i);
        }

        for (int i = 0; i < 4; i++) {
            int column = i / 2;
            int row = i % 2;
            generateGCSlot(27 + column * 18, yOffset + row * 27, i + 8);
        }
    }

    @Unique
    private void generateGCSlot(int x, int y, int idx) {
        gc$slots.add(new AccessorySlot(
                minecraft.player.galacticraft$getGearInv(),
                minecraft.player,
                idx,
                x,
                y,
                GCAccessorySlots.SLOT_TAGS.get(idx),
                GCAccessorySlots.SLOT_SPRITES.get(idx)
        ));
    }

    @Unique
    private boolean gcTryQuickStackToGCInv(Slot playerSlot) {
        if (playerSlot == null) {
            return false;
        }

        for (int i = 0; i < gc$slots.size(); i++) {
            Slot targetSlot = gc$slots.get(i);
            if (targetSlot.mayPlace(playerSlot.getItem()) && targetSlot.getItem().isEmpty()) {
                ItemStack newItem = playerSlot.getItem().copy();
                ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, targetSlot.getContainerSlot(), 1, newItem));
                playerSlot.set(ItemStack.EMPTY);
                playerSlot.setChanged();
                return true;
            }
        }
        return false;
    }

    @Unique
    private void gcTryQuickStackToPlayerInv(Slot gcSlot) {
        if (gcSlot == null) {
            return;
        }

        var playerInv = minecraft.player.getInventory();

        for (int i = 0; i <= 35; ++i) {
            if (playerInv.getItem(i).isEmpty()) {
                ItemStack newStack = gcSlot.getItem().copy();
                ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, gcSlot.getContainerSlot(), 0, ItemStack.EMPTY));
                ClientPlayNetworking.send(new CreativeGcTransferItemPayload(0, i, 1, newStack));
                playerInv.setItem(i, newStack);
                gcSlot.set(ItemStack.EMPTY);
                gcSlot.setChanged();
                minecraft.player.inventoryMenu.broadcastChanges();
                return;
            }
        }
    }

    @Inject(method = "slotClicked", at = @At("HEAD"))
    protected void slotClicked(Slot region, int slotId, int button, ClickType actionType, CallbackInfo ci) {
        if (region != null && actionType == ClickType.QUICK_MOVE) {
            gcTryQuickStackToGCInv(region);
        }
    }

    @Unique
    private void generatePlayerInventorySlots() {
        AbstractContainerMenu abstractContainerMenu = minecraft.player.inventoryMenu;
        for (int i = 0; i < abstractContainerMenu.slots.size(); ++i) {
            boolean hidden = i < 9 || i == 45;
            int x = hidden ? -2000 : 9 + ((i - 9) % 9) * 18;
            int y = hidden ? -2000 : (i >= 36 ? 112 : 54 + ((i - 9) / 9) * 18);
            getMenu().slots.add(new CreativeModeInventoryScreen.SlotWrapper(abstractContainerMenu.slots.get(i), i, x, y));
        }
        destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
        getMenu().slots.add(destroyItemSlot);
    }

    @Override
    public void gc$renderGcSlots(GuiGraphics graphics, int mouseX, int mouseY) {
        for (Slot slot : gc$slots) {
            renderSlot(graphics, slot);
            if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY) && slot.isActive()) {
                hoveredSlot = slot;
                if (slot.isHighlightable()) {
                    CreativeModeInventoryScreen.renderSlotHighlight(graphics, slot.x, slot.y, 0);
                }
            }
        }
    }
}
