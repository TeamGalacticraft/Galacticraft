package com.hrznstudio.galacticraft.entity;

import com.hrznstudio.galacticraft.client.model.block.BasicSolarPanelModel;
import com.hrznstudio.galacticraft.client.render.block.entity.BasicSolarPanelBlockEntityRenderer;
import com.hrznstudio.galacticraft.entity.moonvillager.T1RocketEntity;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketEntityRenderer extends EntityRenderer<T1RocketEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/cow/cow.png");
    protected final BasicSolarPanelModel model = new BasicSolarPanelModel();

    public RocketEntityRenderer(EntityRenderDispatcher entityRenderDispatcher_1) {
        super(entityRenderDispatcher_1);
    }

    @Override
    public void render(T1RocketEntity entity_1, double double_1, double double_2, double double_3, float float_1, float float_2) {
        /*GlStateManager.pushMatrix();
        this.bindEntityTexture(entity_1);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity_1));
        }
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.render(entity_1, double_1, double_2, double_3, float_1, float_2);*/
        this.bindTexture(SKIN);
        int lightmapIndex = entity_1.world.getLightmapIndex(entity_1.getBlockPos().up(), 0);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (lightmapIndex % 65536), (float) (lightmapIndex / 65536));

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogicOp();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translatef((float) entity_1.x, (float) entity_1.y, (float) entity_1.z);

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

    @Override
    protected Identifier getTexture(T1RocketEntity var1) {
        return SKIN;
    }
}