package dev.galacticraft.mod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeScreenMixin {

    private static final ResourceLocation GC_GUIARROWS_TEX =
            ResourceLocation.fromNamespaceAndPath("galacticraft", "textures/gui/vertical_arrows.png");

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
            throw new RuntimeException("Не удалось создать SlotWrapper", e);
        }
    }

    @Inject(method = "selectTab", at=@At("TAIL"))
     void addExtraSlotToInventoryTab(CreativeModeTab group, CallbackInfo ci) {
        CreativeModeInventoryScreen self = (CreativeModeInventoryScreen)(Object)this;

        if (!self.isInventoryOpen()) return;

        AbstractContainerMenu invMenu = self.getMenu();

        int extraTargetIndex = 0;

        int x = 108 + 18;
        int y = 6;

        Slot wrapped = makeSlotWrapper(invMenu.slots.get(extraTargetIndex), extraTargetIndex, x, y);
        ((CreativeModeInventoryScreen.ItemPickerMenu) self.getMenu()).slots.add(wrapped);
    }

    @Inject(method = "renderBg", at=@At("TAIL"))
    private void renderArrows(GuiGraphics g, float delta, int MouseX, int MouseY, CallbackInfo ci)
    {

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
