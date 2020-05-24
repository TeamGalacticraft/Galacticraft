package com.hrznstudio.galacticraft.api.rocket;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public interface RocketPart {

    RocketPartType getType();

    /**
     * @return The block that will be rendered
     */
    BlockState getBlockToRender();

    default void preRender(RocketEntity entity) {

    }

    default void postRender(RocketEntity entity) {

    }

    default Item getDesignerItem() {
        return this.getBlockToRender().getBlock().asItem();
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

    default boolean hasRecipe() {
        return true;
    }

}
