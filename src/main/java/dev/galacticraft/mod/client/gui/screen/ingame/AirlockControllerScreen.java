package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gui.widget.CheckboxWidget;
import dev.galacticraft.mod.screen.AirlockControllerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec2;

public class AirlockControllerScreen extends AbstractContainerScreen<AirlockControllerMenu> {

    private static final ResourceLocation TEXTURE = Constant.id("textures/gui/air_lock_controller.png");


    public AirlockControllerScreen(AirlockControllerMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new CheckboxWidget(this.leftPos + 8, this.topPos + 20));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, this.leftPos, this.topPos, 0, 0, 256, 256);
        this.font.draw(poseStack, Component.translatable("ui.galacticraft.airlock.redstone_signal"), this.leftPos + 25, this.topPos + 23, ChatFormatting.DARK_GRAY.getColor());
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int i, int j) {
        this.font.draw(poseStack, Component.literal(Minecraft.getInstance().player.getGameProfile().getName() + "'s Air Lock Controller"), (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        return super.mouseClicked(d, e, i);
    }
}
