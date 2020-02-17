package com.hrznstudio.galacticraft.api.rocket;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public interface RocketPart {

    RocketPartType getType();

    /**
     * @return The block that will be rendered
     */
    Block getBlockToRender();

    /**
     * This is where you should call {@link GlStateManager#translatef(float, float, float)} or
     * {@link GlStateManager#scalef(float, float, float)} to render your object in the correct position.
     */
    default void preRender(RocketEntity entity) {

    }

    /**
     * This is where you should call {@link GlStateManager#translatef(float, float, float)} or
     * {@link GlStateManager#scalef(float, float, float)} to revert your changes in
     * {@link #preRender(RocketEntity entity)} for the sake of the parts that render after this one.
     */
    default void postRender(RocketEntity entity) {

    }

    default Item getDesignerItem() {
        Item item = this.getBlockToRender().asItem();
        if (item != Items.AIR) {
            return item;
        } else {
            return Items.BARRIER;
        }
    }

    default CompoundTag toTag(CompoundTag tag) {
        tag.putString(this.getType().asString(), Galacticraft.ROCKET_PARTS.getId(this).toString());
        return tag;
    }

    default boolean affectsTier() {
        return false;
    }

    default int getTier(List<RocketPart> parts) {
        return 0;
    }
}
