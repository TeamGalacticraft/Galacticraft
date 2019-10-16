package com.hrznstudio.galacticraft.api.rocket;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public abstract class RocketPart {

    public abstract PartType getType();

    /**
     * @return The block that will be rendered
     */
    public abstract Block getBlockToRender();

    /**
     * This is where you should call {@link GlStateManager#translatef(float, float, float)} or
     * {@link GlStateManager#scalef(float, float, float)} to render your object in the correct position.
     */
    public void preRender(RocketEntity entity) {

    }

    /**
     * This is where you should call {@link GlStateManager#translatef(float, float, float)} or
     * {@link GlStateManager#scalef(float, float, float)} to revert your changes in
     * {@link #preRender(RocketEntity entity)} for the sake of the parts that render after this one.
     */
    public void postRender(RocketEntity entity) {

    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putString(this.getType().asString(), Galacticraft.ROCKET_PARTS.getId(this).toString());
        return tag;
    }
}
