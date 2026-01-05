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
import dev.galacticraft.mod.accessor.GCInventoryFlag;
import dev.galacticraft.mod.client.gui.widget.RadioButton;
import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.network.c2s.CreativeGcTransferItemPayload;
import dev.galacticraft.mod.screen.slot.AccessorySlot;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.util.*;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeScreenMixin extends EffectRenderingInventoryScreen implements GCInventoryFlag, GCCreativeGuiSlots {

    private static final ResourceLocation GC_GUIBG_TEX = Constant.id("textures/gui/creative_tab_inventory.png");

    @Shadow private static CreativeModeTab selectedTab;

    @Shadow protected abstract void selectTab(CreativeModeTab group);

    @Shadow protected abstract void renderTabButton(GuiGraphics graphics, CreativeModeTab group);

    @Shadow private EditBox searchBox;

    @Shadow protected abstract boolean canScroll();

    @Shadow @Final private static ResourceLocation SCROLLER_SPRITE;
    @Shadow @Final private static ResourceLocation SCROLLER_DISABLED_SPRITE;
    @Shadow private float scrollOffs;
    @Shadow @Nullable private Slot destroyItemSlot;
    @Shadow @Final private static SimpleContainer CONTAINER;
    @Unique private final List<AccessorySlot> gc$slots = new ArrayList<>();

    @Unique private List<Slot> GCInvSlots;

    @Unique RadioButton creativeSwitchButton;

    @Unique
    private boolean bGCInventory = false;

    @Override
    public boolean gc$isGCInventoryEnabled()
    {
        return bGCInventory;
    }

    @Override
    public List<AccessorySlot> gc$getRenderSlots()
    {
        return gc$slots;
    }

    public CreativeScreenMixin(AbstractContainerMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(null, null, null);
    }

    /**
     * @author MaverX
     * @reason It's easier to rewrite the function entirely than to try to inject something into it.
     */
    @Overwrite
    public void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY)
    {

        for(CreativeModeTab creativeModeTab : CreativeModeTabs.tabs()) {
            if (creativeModeTab != selectedTab) {
                renderTabButton(graphics, creativeModeTab);
            }
        }

        graphics.blit(isGCInventoryEnabled() ? GC_GUIBG_TEX : selectedTab.getBackgroundTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);
        searchBox.render(graphics, mouseX, mouseY, delta);
        int i = leftPos + 175;
        int j = topPos + 18;
        int k = j + 112;
        if (selectedTab.canScroll()) {
            ResourceLocation resourceLocation =  canScroll() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
            graphics.blitSprite(resourceLocation, i, j + (int)((float)(k - j - 17) * scrollOffs), 12, 15);
        }

        renderTabButton(graphics, selectedTab);
        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, isGCInventoryEnabled() ? leftPos + 55 : leftPos + 73, topPos + 6, leftPos + 105, topPos + 49, 20, 0.0625F, (float)mouseX, (float)mouseY, minecraft.player);
        }
    }

    @Unique
    public boolean isGCInventoryEnabled()
    {
        return bGCInventory;
    }

    @Unique
    private boolean isCreativeGearInvAllowed()
    {
        return Galacticraft.CONFIG.enableCreativeGearInv();
    }

    @Unique
    private static Slot makeSlotWrapper(Slot target, int invSlot, int x, int y) {
        try {
            Class<?> cls = Class.forName(
                    "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$SlotWrapper"
            );
            Constructor<?> c = cls.getDeclaredConstructor(Slot.class, int.class, int.class, int.class);
            c.setAccessible(true);
            return (Slot) c.newInstance(target, invSlot, x, y);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create SlotWrapper", e);
        }
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci)
    {
        creativeSwitchButton = new RadioButton(0,0);
    }

    @Inject(method = "selectTab", at = @At(value = "HEAD"))
    void gc$selectTab(CreativeModeTab group, CallbackInfo ci)
    {
        if (group.getType() == CreativeModeTab.Type.INVENTORY)
        {
            if (isCreativeGearInvAllowed())
            {
                creativeSwitchButton.setX(leftPos+11);
                creativeSwitchButton.setY(topPos+18);
                creativeSwitchButton.radioButtonOnClick = () -> {
                    bGCInventory = creativeSwitchButton.getIsBottomButtonActive();
                    regenerateSlots();
                };
                addRenderableWidget(creativeSwitchButton);
            } else {
                creativeSwitchButton.setX(-2000);
                creativeSwitchButton.setY(-2000);
                creativeSwitchButton.radioButtonOnClick = null;
            }

        }
        if (group.getType() != CreativeModeTab.Type.INVENTORY)
        {
            removeWidget(creativeSwitchButton);
            bGCInventory = false;
            gc$slots.clear();
            creativeSwitchButton.setX(-2000);
            creativeSwitchButton.setY(-2000);
            creativeSwitchButton.setIsBottomButtonActive(false);
        }
    }

    @Inject(method = "resize", at = @At(value = "TAIL"))
    void resize(Minecraft client, int width, int height, CallbackInfo ci)
    {
        if (isGCInventoryEnabled())
        {
            regenerateSlots();
            creativeSwitchButton.setIsBottomButtonActive(true);
        }

    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {

        //Item duplication support
        if (button == 2)
        {
            Slot slot = gc$findSlot(mouseX, mouseY);
            if (slot != null)
            {
                getMenu().setCarried(slot.getItem().copy());
            }
            return;
        }

        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {


            if(isGCInventoryEnabled())
            {
                ItemStack carried = getMenu().getCarried();
                Slot slot = gc$findSlot(mouseX, mouseY);
                if (slot != null)
                {
                    //Quick stack
                    if (Screen.hasShiftDown())
                    {
                        gcTryQuickStackToPlayerInv(slot);
                        cir.setReturnValue(true);
                        return;
                    }
                    if (!carried.isEmpty() && slot.mayPlace(carried))
                    {
                        if (slot.getItem().isEmpty())
                        {
                            slot.set(carried);
                            slot.setChanged();
                            getMenu().setCarried(ItemStack.EMPTY);
                            ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, slot.getContainerSlot(), 1, carried));

                            Minecraft.getInstance().getSoundManager().play(
                                    SimpleSoundInstance.forUI(
                                            SoundEvents.ARMOR_EQUIP_LEATHER,
                                            1.0F
                                    )
                            );
                            cir.setReturnValue(true);
                            return;
                        }
                        else if (slot.mayPlace(carried))
                        {
                            ItemStack oldItem = slot.getItem();
                            ItemStack newItem = carried;

                            getMenu().setCarried(oldItem);
                            slot.set(newItem);
                            slot.setChanged();
                            ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, slot.getContainerSlot(), 1, newItem));
                            cir.setReturnValue(true);
                            return;

                        }

                    }
                    else if (carried.isEmpty())
                    {
                        ItemStack slotStack = slot.getItem();
                        getMenu().setCarried(slotStack);
                        slot.set(ItemStack.EMPTY);
                        slot.setChanged();
                        ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, slot.getContainerSlot(), 0, ItemStack.EMPTY));
                        cir.setReturnValue(true);
                        return;
                    }

                    cir.setReturnValue(true);
                    return;
                }

            }
        }
    }

    @Unique
    private void regenerateSlots()
    {
        gc$slots.clear();
        getMenu().slots.clear();

        if (isGCInventoryEnabled())
        {
            generatePlayerInventorySlots();
            generateGCSlots();
        }
        else
        {
            selectTab(BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.INVENTORY));
        }


    }

    @Nullable
    @Unique
    private Slot gc$findSlot(double x, double y) {
        for (int i = 0; i < gc$slots.size(); ++i) {
            Slot slot = (Slot)gc$slots.get(i);
            if (isHovering(slot.x, slot.y, 16, 16, x, y) && slot.isActive()) {
                return slot;
            }
        }

        return null;
    }

    @Unique
    private void generateGCSlots()
    {
        int offset = 6;
        for (int i = 0; i < 4; i++)
        {
            int col = i%2;
            int row = i/2;
            generateGCSlot(27+row*18, offset+(col*27), i);
        }

        for (int i = 4; i < 12; i++)
        {
            int col = i%4;
            int row = i/4;
            row = 1 - row;
            generateGCSlot(99+col*18, (row*27)+33, i);
        }
    }

    @Unique
    private void generateGCSlot(int x, int y, int idx)
    {
        AccessorySlot s = new AccessorySlot(
                minecraft.player.galacticraft$getGearInv(),
                minecraft.player,
                idx,
                x,
                y,
                GCAccessorySlots.SLOT_TAGS.get(idx),
                GCAccessorySlots.SLOT_SPRITES.get(idx)
        );

        gc$slots.add(s);
    }

    @Unique
    private boolean gcTryQuickStackToGCInv(Slot playerSlot)
    {
        if(playerSlot == null) return false;
        var gcInv = minecraft.player.galacticraft$getGearInv();

        boolean canPlaceInAny = false;
        for(int i = 0; i < gc$slots.size(); i++)
        {
            if( gc$slots.get(i).mayPlace(playerSlot.getItem()) && gc$slots.get(i).getItem().isEmpty())
            {
                canPlaceInAny = true;
                ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, i, 1, playerSlot.getItem().copy()));
                Minecraft.getInstance().getSoundManager().play(
                        SimpleSoundInstance.forUI(
                                SoundEvents.ARMOR_EQUIP_LEATHER,
                                1.0F
                        )
                );
                playerSlot.set(ItemStack.EMPTY);
                playerSlot.setChanged();
                break;
            }
        }
        return canPlaceInAny;

    }

    @Unique
    private boolean gcTryQuickStackToPlayerInv(Slot gcSlot)
    {
        if(gcSlot == null) return false;
        var playerInv = minecraft.player.getInventory();
        var playerSlots = getMenu().slots;

        boolean canPlaceInAny = false;
        for (int i = 0; i <= 35; ++i)
        {
            if( playerInv.getItem(i).isEmpty())
            {
                canPlaceInAny = true;

                ItemStack newStack = gcSlot.getItem().copy();

                ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, gcSlot.getContainerSlot(), 0, ItemStack.EMPTY));
                ClientPlayNetworking.send(new CreativeGcTransferItemPayload(0, i, 1, newStack));
                playerInv.setItem(i, newStack);

                gcSlot.set(ItemStack.EMPTY);
                gcSlot.setChanged();

                minecraft.player.inventoryMenu.broadcastChanges();
                break;
            }
        }
        return canPlaceInAny;

    }

    @Inject(method = "slotClicked", at = @At("HEAD"))
    protected void slotClicked(Slot region, int slotId, int button, ClickType actionType, CallbackInfo ci)
    {
        if (region != null && actionType == ClickType.QUICK_MOVE) {
            if (gcTryQuickStackToGCInv(region)) { return; };
        }
    }

    @Unique
    private void generatePlayerInventorySlots()
    {
        AbstractContainerMenu abstractContainerMenu = minecraft.player.inventoryMenu;
        for (int i = 0; i < abstractContainerMenu.slots.size(); ++i) {
            int n;
            int j;
            if (i >= 5 && i < 9) {
                int k = i - 5;
                int l = k / 2;
                int m = k % 2;
                n = -2000;
                j = -2000;
            } else if (i >= 0 && i < 5) {
                n = -2000;
                j = -2000;
            } else if (i == 45) {
                n = -2000;
                j = -2000;
            } else {
                int k = i - 9;
                int l = k % 9;
                int m = k / 9;
                n = 9 + l * 18;
                if (i >= 36) {
                    j = 112;
                } else {
                    j = 54 + m * 18;
                }
            }
            getMenu().slots.add( makeSlotWrapper((Slot)abstractContainerMenu.slots.get(i),i, n, j) );
        }

        destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
        getMenu().slots.add(destroyItemSlot);
    }

    @Override
    public void gc$renderGcSlots(GuiGraphics graphics, int mouseX, int mouseY) {

        for (Slot s : gc$slots) {
            renderSlot(graphics, s);
            if (isHovering(s.x, s.y, 16, 16, mouseX, mouseY) && s.isActive())
            {
                hoveredSlot = s;
                if (s.isHighlightable())
                {
                    CreativeModeInventoryScreen.renderSlotHighlight(graphics,s.x, s.y, 0);
                }

            }
        }
    }


}
