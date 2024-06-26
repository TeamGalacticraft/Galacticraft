package dev.galacticraft.mod.content;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ClientCannedFoodTooltip implements ClientTooltipComponent {
    private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("container/bundle/background");
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;
    private final NonNullList<ItemStack> items;

    public ClientCannedFoodTooltip(CannedFoodTooltip cannedFoodTooltip) {
        this.items = cannedFoodTooltip.getItems();
    }

    public int getHeight() {
        return this.backgroundHeight() + 4;
    }

    public int getWidth(Font textRenderer) {
        return this.backgroundWidth();
    }

    private int backgroundWidth() {
        return this.gridSizeX() * 18 + 2;
    }

    private int backgroundHeight() {
        return this.gridSizeY() * 20 + 2;
    }

    public void renderImage(Font textRenderer, int x, int y, GuiGraphics context) {
        int i = this.gridSizeX();
        int j = this.gridSizeY();
        //creates a background panel over the items
        //context.blitSprite(BACKGROUND_SPRITE, x, y, this.backgroundWidth(), this.backgroundHeight());
        int weight = 1;
        boolean bl = weight >= 64;
        int k = 0;

        for(int l = 0; l < j; ++l) {
            for(int m = 0; m < i; ++m) {
                int n = x + m * 18 + 1;
                int o = y + l * 20 + 1;
                this.renderSlot(n, o, k++, bl, context, textRenderer);
            }
        }

    }

    private void renderSlot(int x, int y, int index, boolean shouldBlock, GuiGraphics context, Font textRenderer) {
        if (index >= this.items.size()) {
            //creates a ending slot used for bundle not items contained
            //this.blit(context, x, y, shouldBlock ? ClientCannedFoodTooltip.Texture.BLOCKED_SLOT : ClientCannedFoodTooltip.Texture.SLOT);
        } else {
            ItemStack itemStack = (ItemStack)this.items.get(index);
            this.blit(context, x, y, ClientCannedFoodTooltip.Texture.SLOT);
            context.renderItem(itemStack, x + 1, y + 1, index);
            context.renderItemDecorations(textRenderer, itemStack, x + 1, y + 1);
            //used to highlight the item slot
            //not needed
//            if (index == 0) {
//                AbstractContainerScreen.renderSlotHighlight(context, x + 1, y + 1, 0);
//            }

        }
    }

    private void blit(GuiGraphics context, int x, int y, ClientCannedFoodTooltip.Texture sprite) {
        context.blitSprite(sprite.sprite, x, y, 0, sprite.w, sprite.h);
    }

    private int gridSizeX() {
        return Math.max(2, (int)Math.ceil(Math.sqrt((double)this.items.size() + 1.0)));
    }

    private int gridSizeY() {
        return (int)Math.ceil(((double)this.items.size() + 1.0) / (double)this.gridSizeX());
    }

    @Environment(EnvType.CLIENT)
    private static enum Texture {
        BLOCKED_SLOT(new ResourceLocation("container/bundle/blocked_slot"), 18, 20),
        SLOT(new ResourceLocation("container/bundle/slot"), 18, 20);

        public final ResourceLocation sprite;
        public final int w;
        public final int h;

        private Texture(ResourceLocation resourceLocation, int j, int k) {
            this.sprite = resourceLocation;
            this.w = j;
            this.h = k;
        }
    }
}
