/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.client.gui.screen;

import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CFPage extends GuiTeleporting
{
    public CFPage()
    {
        super(1);
        this.message = "";
        TickHandlerClient.teleportingGui = this;
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ResourceLocation img = new ResourceLocation(Constants.MOD_ID_CORE, "textures/gui/landing.png");
        Minecraft.getMinecraft().renderEngine.bindTexture(img);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator   tessellator  = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(0 + 0, 0 + height, zLevel).tex(0, 1).endVertex();
        vertexbuffer.pos(0 + width, 0 + height, zLevel).tex(1, 1).endVertex();
        vertexbuffer.pos(0 + width, 0 + 0, zLevel).tex(1, 0).endVertex();
        vertexbuffer.pos(0 + 0, 0 + 0, zLevel).tex(0, 0).endVertex();
        tessellator.draw();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return true;
    }
}
