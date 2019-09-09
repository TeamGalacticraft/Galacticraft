package com.hrznstudio.galacticraft.entity;

import com.hrznstudio.galacticraft.client.model.block.BasicSolarPanelModel;
import com.hrznstudio.galacticraft.entity.moonvillager.T1RocketEntity;
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
        GlStateManager.pushMatrix();
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
        super.render(entity_1, double_1, double_2, double_3, float_1, float_2);
    }

    @Override
    protected Identifier getTexture(T1RocketEntity var1) {
        return SKIN;
    }
}