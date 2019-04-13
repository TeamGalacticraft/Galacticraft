package io.github.teamgalacticraft.galacticraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelBlockEntity;
import io.github.teamgalacticraft.galacticraft.client.model.block.BasicSolarPanelModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BasicSolarPanelBlockEntityRenderer extends BlockEntityRenderer<BasicSolarPanelBlockEntity> {

    public BasicSolarPanelModel model = new BasicSolarPanelModel();
    private static Identifier solarPanelTexture = new Identifier(Constants.MOD_ID, "textures/model/solar_panel_basic.png");

    @Override
    public void render(BasicSolarPanelBlockEntity entity, double x, double y, double z, float f, int i) {
        this.bindTexture(BasicSolarPanelBlockEntityRenderer.solarPanelTexture);
        int lightmapIndex = this.getWorld().getLightmapIndex(entity.getPos().up(), 0);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (lightmapIndex % 65536), (float) (lightmapIndex / 65536));

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogicOp();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translatef((float) x, (float) y, (float) z);

        GlStateManager.translatef(0.5F, 1.0F, 0.5F);
        this.model.renderPole();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogicOp();
        GlStateManager.translatef(0.0F, 1.5F, 0.0F);

        GlStateManager.rotatef(180.0F, 0, 0, 1);
        GlStateManager.rotatef(-90.0F, 0, 1, 0);

        this.model.renderPanel();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogicOp();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
