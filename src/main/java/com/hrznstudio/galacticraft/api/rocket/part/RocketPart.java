package com.hrznstudio.galacticraft.api.rocket.part;

import com.hrznstudio.galacticraft.Galacticraft;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;
import java.util.List;

public class RocketPart {
    private final Text name;
    private final RocketPartType partType;
    private final BlockState renderState;
    private final ItemStack renderStack;
    private final TierSupplier tier;
    private final boolean hasRecipe;

    private RocketPart(@Nonnull RocketPartType partType, @Nonnull Text name, @Nonnull BlockState renderState, @Nonnull ItemStack renderStack, TierSupplier tier, boolean hasRecipe) {
        this.partType = partType;
        this.name = name;
        this.renderState = renderState;
        this.renderStack = renderStack;
        this.tier = tier;
        this.hasRecipe = hasRecipe;
    }

    public int getTier(List<RocketPart> pieces) {
        return tier.getTier(pieces);
    }

    public RocketPartType getType() {
        return partType;
    }

    public BlockState getRenderState() {
        return renderState;
    }

    public ItemStack getRenderStack() {
        return renderStack;
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putString(this.getType().asString(), Galacticraft.ROCKET_PARTS.getId(this).toString());
        return tag;
    }

    public boolean hasRecipe() {
        return hasRecipe;
    }

    public static class Builder {
        private Text name;
        private RocketPartType partType;
        private BlockState renderState;
        private ItemStack renderItem;
        private TierSupplier tier = (list) -> 0;
        private boolean hasRecipe = true;

        public static Builder create() {
            return new Builder();
        }

        public Builder name(Text name) {
            this.name = name;
            return this;
        }

        public Builder type(RocketPartType type) {
            this.partType = type;
            return this;
        }

        public Builder renderState(BlockState renderState) {
            this.renderState = renderState;
            return this;
        }

        public Builder recipe(boolean hasRecipe) {
            this.hasRecipe = hasRecipe;
            return this;
        }

        public Builder renderItem(ItemStack renderItem) {
            this.renderItem = renderItem;
            return this;
        }

        public Builder tier(TierSupplier tier) {
            this.tier = tier;
            return this;
        }

        public RocketPart build() {
            if (name == null || partType == null || renderState == null) {
                throw new RuntimeException("Tried to build incomplete RocketPart!");
            }
            return new RocketPart(partType, name, renderState, renderItem == null ? new ItemStack(renderState.getBlock().asItem()) : renderItem, tier, hasRecipe);
        }
    }

    @FunctionalInterface
    public interface TierSupplier {
        int getTier(List<RocketPart> pieces);
    }
}
