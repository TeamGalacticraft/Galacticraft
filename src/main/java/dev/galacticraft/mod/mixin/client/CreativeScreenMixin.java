package dev.galacticraft.mod.mixin.client;

import de.javagl.obj.Obj;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.GCCreativeGuiSlots;
import dev.galacticraft.mod.accessor.GCInventoryFlag;
import dev.galacticraft.mod.api.config.Config;
import dev.galacticraft.mod.client.gui.widget.RadioButton;
import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.network.c2s.CreativeGcTransferItemPayload;
import dev.galacticraft.mod.screen.slot.AccessorySlot;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.util.*;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeScreenMixin implements GCInventoryFlag, GCCreativeGuiSlots {

    private static final ResourceLocation GC_GUIBG_TEX = Constant.id("textures/gui/creative_tab_inventory.png");

    @Shadow private static CreativeModeTab selectedTab;

    @Shadow protected abstract void selectTab(CreativeModeTab group);

    @Unique private final List<AccessorySlot> gc$slots = new ArrayList<>();

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

    @Unique private List<Slot> GCInvSlots;

    /**
     * @author MaverX
     * @reason It's easier to rewrite the function entirely than to try to inject something into it.
     */
    @Overwrite
    public void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY)
    {

        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen)(Object)this;
        int leftPos = ((AbstractContainerScreenAccessor)(Object)this).getLeftPos();
        int topPos = ((AbstractContainerScreenAccessor)(Object)this).getTopPos();
        int imageWidth = ((AbstractContainerScreenAccessor)(Object)this).getImageWidth();
        int imageHeight = ((AbstractContainerScreenAccessor)(Object)this).getImageHeight();

        for(CreativeModeTab creativeModeTab : CreativeModeTabs.tabs()) {
            if (creativeModeTab != selectedTab) {
                ((CreativeModeInventoryScreenAccessor)(Object)this).GCrenderTabButton(graphics, creativeModeTab);
            }
        }

        graphics.blit(isGCInventoryEnabled() ? GC_GUIBG_TEX : selectedTab.getBackgroundTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);
        ((CreativeModeInventoryScreenAccessor)(Object)this).getSearchBox().render(graphics, mouseX, mouseY, delta);
        int i = leftPos + 175;
        int j = topPos + 18;
        int k = j + 112;
        if (selectedTab.canScroll()) {
            ResourceLocation resourceLocation =  ((CreativeModeInventoryScreenAccessor)(Object)this).GCcanScroll() ? ((CreativeModeInventoryScreenAccessor)(Object)this).get_SCROLLER_SPRITE() : ((CreativeModeInventoryScreenAccessor)(Object)this).get_SCROLLER_DISABLED_SPRITE();
            graphics.blitSprite(resourceLocation, i, j + (int)((float)(k - j - 17) * ((CreativeModeInventoryScreenAccessor)(Object)this).getScrollOffs()), 12, 15);
        }

        ((CreativeModeInventoryScreenAccessor)(Object)this).GCrenderTabButton(graphics, selectedTab);
        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, isGCInventoryEnabled() ? leftPos + 54 : leftPos + 73, topPos + 6, leftPos + 105, topPos + 49, 20, 0.0625F, (float)mouseX, (float)mouseY, ((ScreenAccessor)(Object)this).getMinecraft().player);
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

    @Unique RadioButton creativeSwitchButton;

    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci)
    {
        creativeSwitchButton = new RadioButton(0,0);
    }

    @Inject(method = "selectTab", at = @At(value = "HEAD"))
    void gc$selectTab(CreativeModeTab group, CallbackInfo ci)
    {
        if(group.getType() == CreativeModeTab.Type.INVENTORY)
        {
            int leftP = ((AbstractContainerScreenAccessor)(Object)this).getLeftPos();
            int topP = ((AbstractContainerScreenAccessor)(Object)this).getTopPos();

            if(isCreativeGearInvAllowed())
            {
                creativeSwitchButton.setX(leftP+11);
                creativeSwitchButton.setY(topP+18);
                creativeSwitchButton.radioButtonOnClick = () ->{
                    bGCInventory = creativeSwitchButton.getIsBottomButtonActive();
                    regenerateSlots();
                };
                ((ScreenAccessor)(Object)this).gc$addRenderableWidget(creativeSwitchButton);
            }
            else
            {
                creativeSwitchButton.setX(-2000);
                creativeSwitchButton.setY(-2000);
                creativeSwitchButton.radioButtonOnClick = null;
            }

        }
        if(group.getType() != CreativeModeTab.Type.INVENTORY)
        {
            ((ScreenAccessor)(Object)this).gc$removeWidget(creativeSwitchButton);
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
        if(isGCInventoryEnabled())
        {
            regenerateSlots();
            creativeSwitchButton.setIsBottomButtonActive(true);
        }

    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {

        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen)(Object)this;

        //Dublication support
        if(button == 2)
        {
            Slot slot = gc$findSlot(mouseX, mouseY);
            if(slot != null)
            {
                self.getMenu().setCarried(slot.getItem().copy());
            }
            return;
        }

        CreativeModeInventoryScreenAccessor invAccessor = (CreativeModeInventoryScreenAccessor)(Object)this;
        AbstractContainerScreenAccessor absAccessor = (AbstractContainerScreenAccessor)(Object)this;
        int leftP = ((AbstractContainerScreenAccessor)(Object)this).getLeftPos();
        int topP = ((AbstractContainerScreenAccessor)(Object)this).getTopPos();
        var player = ((ScreenAccessor)(Object)this).getMinecraft().player;

        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {


            if(isGCInventoryEnabled())
            {
                ItemStack carried = self.getMenu().getCarried();
                Slot slot = gc$findSlot(mouseX, mouseY);
                if(slot != null)
                {
                    //Quick stack
                    if(Screen.hasShiftDown())
                    {
                        gcTryQuickStackToPlayerInv(slot);
                        cir.setReturnValue(true);
                        return;
                    }
                    if(!carried.isEmpty() && slot.mayPlace(carried))
                    {
                        if(slot.getItem().isEmpty())
                        {
                            slot.set(carried);
                            slot.setChanged();
                            self.getMenu().setCarried(ItemStack.EMPTY);
                            ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, slot.getContainerSlot(), 1, carried));

                            Minecraft.getInstance().getSoundManager().play(
                                    net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                                            SoundEvents.ARMOR_EQUIP_LEATHER,
                                            1.0F
                                    )
                            );
                            cir.setReturnValue(true);
                            return;
                        }
                        else if(slot.mayPlace(carried))
                        {
                            ItemStack oldItem = slot.getItem();
                            ItemStack newItem = carried;

                            self.getMenu().setCarried(oldItem);
                            slot.set(newItem);
                            slot.setChanged();
                            ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, slot.getContainerSlot(), 1, newItem));
                            cir.setReturnValue(true);
                            return;

                        }

                    }
                    else if(carried.isEmpty())
                    {
                        ItemStack slotStack = slot.getItem();
                        self.getMenu().setCarried(slotStack);
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
        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen)(Object)this;
        self.getMenu().slots.clear();

        if(isGCInventoryEnabled())
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
        for(int i = 0; i < gc$slots.size(); ++i) {
            Slot slot = (Slot)gc$slots.get(i);
            if (((AbstractContainerScreenAccessor) this).gcIsHovering(slot, x, y) && slot.isActive()) {
                return slot;
            }
        }

        return null;
    }

    @Unique
    private void generateGCSlots()
    {
        int offset = 6;
        for(int i = 0; i < 4; i++)
        {
            int col = i%2;
            int row = i/2;
            generateGCSlot(27+row*18, offset+(col*27), i);
        }

        for(int i = 4; i < 12; i++)
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
        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen)(Object)this;
        int leftP = ((AbstractContainerScreenAccessor)(Object)this).getLeftPos();
        int topP = ((AbstractContainerScreenAccessor)(Object)this).getTopPos();
        var player = ((ScreenAccessor)(Object)this).getMinecraft().player;
        AccessorySlot s = new AccessorySlot(
                player.galacticraft$getGearInv(),
                player,
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
        var player = ((ScreenAccessor)(Object)this).getMinecraft().player;
        var gcInv = player.galacticraft$getGearInv();

        boolean canPlaceInAny = false;
        for(int i = 0; i < gc$slots.size(); i++)
        {
            if( gc$slots.get(i).mayPlace(playerSlot.getItem()) && gc$slots.get(i).getItem().isEmpty())
            {
                canPlaceInAny = true;
                ClientPlayNetworking.send(new CreativeGcTransferItemPayload(1, i, 1, playerSlot.getItem().copy()));
                Minecraft.getInstance().getSoundManager().play(
                        net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
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
        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen)(Object)this;
        var player = ((ScreenAccessor)(Object)this).getMinecraft().player;
        var playerInv = player.getInventory();
        var playerSlots = self.getMenu().slots;

        boolean canPlaceInAny = false;
        for(int i = 0; i <= 35; ++i)
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

                player.inventoryMenu.broadcastChanges();
                break;
            }
        }
        return canPlaceInAny;

    }

    @Inject(method = "slotClicked", at = @At("HEAD"))
    protected void slotClicked(Slot region, int slotId, int button, ClickType actionType, CallbackInfo ci)
    {
        if(region != null)
        {
            if(actionType == ClickType.QUICK_MOVE)
            {
                if(gcTryQuickStackToGCInv(region))
                {
                    return;
                }
            }
        }


    }

    @Unique
    private void generatePlayerInventorySlots()
    {
        var player = ((ScreenAccessor)(Object)this).getMinecraft().player;
        int leftP = ((AbstractContainerScreenAccessor)(Object)this).getLeftPos();
        int topP = ((AbstractContainerScreenAccessor)(Object)this).getTopPos();
        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen)(Object)this;

        AbstractContainerMenu abstractContainerMenu = player.inventoryMenu;
        for(int i = 0; i < abstractContainerMenu.slots.size(); ++i) {
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
            self.getMenu().slots.add( makeSlotWrapper((Slot)abstractContainerMenu.slots.get(i),i, n, j));
        }

        ((CreativeModeInventoryScreenAccessor)(Object)this).setDestroyItemSlot(new Slot(((CreativeModeInventoryScreenAccessor)(Object)this).gcGetCONTAINER(), 0, 173, 112));
        self.getMenu().slots.add(((CreativeModeInventoryScreenAccessor)(Object)this).getDestroyItemSlot());
    }

    @Override
    public void gc$renderGcSlots(GuiGraphics graphics, int mouseX, int mouseY) {

        for (Slot s : gc$slots) {
            ((AbstractContainerScreenAccessor) this).gcRenderSlot(graphics,s);
            if(((AbstractContainerScreenAccessor) this).gcIsHovering(s,mouseX, mouseY) && s.isActive())
            {
                ((AbstractContainerScreenAccessor) this).setHoveredSlot(s);
                if(s.isHighlightable())
                {
                    CreativeModeInventoryScreen.renderSlotHighlight(graphics,s.x, s.y, 0);
                }

            }
        }
    }


}
