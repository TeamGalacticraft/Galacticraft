package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.screen.GCPlayerInventoryMenu;
import dev.galacticraft.mod.screen.slot.AccessorySlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeScreenMixin {

    private static final ResourceLocation GC_GUIARROWS_TEX =
            ResourceLocation.fromNamespaceAndPath("galacticraft", "textures/gui/vertical_arrows.png");
    private static final ResourceLocation GC_GUIBG_TEX =
            ResourceLocation.fromNamespaceAndPath("galacticraft", "textures/gui/creative_tab_inventory.png");

    @Shadow private static CreativeModeTab selectedTab;

    @Unique private boolean bGCInventory = false;

    @Unique private int b0x, b0y, b1x, b1y;
    @Unique private static final int BTN_W = 12;
    @Unique private static final int BTN_H = 10;
    @Unique private boolean btn1Hovered, btn2Hovered;

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

    /**
     * @author
     * @reason
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


        graphics.blit(bGCInventory ? GC_GUIBG_TEX : selectedTab.getBackgroundTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);
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
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, bGCInventory ? leftPos + 54 : leftPos + 73, topPos + 6, leftPos + 105, topPos + 49, 20, 0.0625F, (float)mouseX, (float)mouseY, ((ScreenAccessor)(Object)this).getMinecraft().player);
        }

        renderArrows(graphics, delta, mouseX, mouseY);
    }

    private void renderArrows(GuiGraphics g, float delta, int MouseX, int MouseY)
    {
        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen)(Object)this;

        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            int leftP = ((AbstractContainerScreenAccessor)(Object)this).getLeftPos();
            int topP = ((AbstractContainerScreenAccessor)(Object)this).getTopPos();
            int offset = 18;


            b0x = leftP+11;
            b0y = topP+offset;
            b1x = leftP+11;
            b1y = topP+offset+BTN_H;

            btn1Hovered = isButtonHovered(false, MouseX, MouseY);
            btn2Hovered = isButtonHovered(true, MouseX, MouseY);

            if(!bGCInventory)
            {
                if(btn1Hovered)
                {
                    g.blit(GC_GUIARROWS_TEX, leftP+11, topP+offset,getArrowBlitCoordsU(2),getArrowBlitCoordsV(2, false),12,10,256,256);
                }
                else
                {
                    g.blit(GC_GUIARROWS_TEX, leftP+11, topP+offset,getArrowBlitCoordsU(2),getArrowBlitCoordsV(2, false),12,10,256,256);
                }
                if(btn2Hovered)
                {
                    g.blit(GC_GUIARROWS_TEX, leftP+11, topP+offset+BTN_H,getArrowBlitCoordsU(0),getArrowBlitCoordsV(1, true),12,10,256,256);
                }
                else
                {
                    g.blit(GC_GUIARROWS_TEX, leftP+11, topP+offset+BTN_H,getArrowBlitCoordsU(0),getArrowBlitCoordsV(0, true),12,10,256,256);
                }
            }
            else
            {
                if(btn1Hovered)
                {
                    g.blit(GC_GUIARROWS_TEX, leftP+11, topP+offset,getArrowBlitCoordsU(0),getArrowBlitCoordsV(1, false),12,10,256,256);
                }
                else
                {
                    g.blit(GC_GUIARROWS_TEX, leftP+11, topP+offset,getArrowBlitCoordsU(0),getArrowBlitCoordsV(0, false),12,10,256,256);
                }
                if(btn2Hovered)
                {
                    g.blit(GC_GUIARROWS_TEX, leftP+11, topP+offset+BTN_H,getArrowBlitCoordsU(2),getArrowBlitCoordsV(2, true),12,10,256,256);
                }
                else
                {
                    g.blit(GC_GUIARROWS_TEX, leftP+11, topP+offset+BTN_H,getArrowBlitCoordsU(2),getArrowBlitCoordsV(2, true),12,10,256,256);
                }
            }





        }
    }

    // style - 0: default 1: active 2: inactive
    private int getArrowBlitCoordsV(int style,boolean isBottomTex)
    {
        return switch (style) {
            case 0 -> isBottomTex ? 226 : 216;
            case 1 -> isBottomTex ? 246 : 236;
            case 2 -> isBottomTex ? 226 : 216;
            default -> 0;
        };
    }

    // style - 0: default 1: active 2: inactive
    private int getArrowBlitCoordsU(int style)
    {
        return switch (style) {
            case 0 -> 0;
            case 1 -> 0;
            case 2 -> 12;
            default -> 0;
        };
    }

    public boolean isButtonHovered(boolean isBottomButton, int MouseX, int MouseY)
    {
        if(isBottomButton)
        {
            return
                    MouseX >= b1x && MouseX < b1x + BTN_W &&
                            MouseY >= b1y && MouseY < b1y + BTN_H;
        }
        else
        {
            return
                    MouseX >= b0x && MouseX < b0x + BTN_W &&
                            MouseY >= b0y && MouseY < b0y + BTN_H;
        }
    }

    private void playButtonClickSound()
    {
        Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                        net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,
                        1.0F
                )
        );
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button != 0) return;


        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {

            boolean topButtonH = isButtonHovered(false, (int)mouseX, (int)mouseY);
            boolean bottomButtonH = isButtonHovered(true, (int)mouseX, (int)mouseY);

            if(!bGCInventory && bottomButtonH)
            {
                playButtonClickSound();
                bGCInventory = true;
                cir.setReturnValue(true);
            }

            if(bGCInventory && topButtonH)
            {
                playButtonClickSound();
                bGCInventory = false;
                cir.setReturnValue(true);
            }
        }
    }


}
