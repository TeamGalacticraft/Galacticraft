package com.hrznstudio.galacticraft.api.rocket;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.List;

public class RocketPart {
    private final Text name;
    private final RocketPartType partType;
    private final BlockState renderState;
    @Environment(EnvType.CLIENT)
    private Renderer preRender;
    @Environment(EnvType.CLIENT)
    private Renderer postRender;
    private final ItemStack renderStack;
    private final TierGetter tier;
    private final boolean hasRecipe;

    @Environment(EnvType.CLIENT)
    private RocketPart(@Nonnull RocketPartType partType, @Nonnull Text name, @Nonnull BlockState renderState, @Nonnull Renderer preRender, @Nonnull Renderer postRender, @Nonnull ItemStack renderStack, TierGetter tier, boolean hasRecipe) {
        this.partType = partType;
        this.name = name;
        this.renderState = renderState;
        this.preRender = preRender;
        this.postRender = postRender;
        this.renderStack = renderStack;
        this.tier = tier;
        this.hasRecipe = hasRecipe;
    }

    private RocketPart(@Nonnull RocketPartType partType, @Nonnull Text name, @Nonnull BlockState renderState, @Nonnull ItemStack renderStack, TierGetter tier, boolean hasRecipe) {
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

    @Environment(EnvType.CLIENT)
    public void preRender(MatrixStack stack, RocketEntity entity, VertexConsumerProvider vertexConsumers, float tickDelta) {
        preRender.render(stack, entity, vertexConsumers, tickDelta);
    }

    public Text getName() {
        return name;
    }

    @Environment(EnvType.CLIENT)
    public void postRender(MatrixStack stack, RocketEntity entity, VertexConsumerProvider vertexConsumers, float tickDelta) {
        postRender.render(stack, entity, vertexConsumers, tickDelta);
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putString(this.getType().asString(), Galacticraft.ROCKET_PARTS.getId(this).toString());
        return tag;
    }

    public boolean hasRecipe() {
        return hasRecipe;
    }

    public static class ServerBuilder {
        private Text name;
        private RocketPartType partType;
        private BlockState renderState;
        private ItemStack renderItem;
        private TierGetter tier = (list) -> 0;
        private boolean hasRecipe = true;

        public static ServerBuilder create() {
            return new ServerBuilder();
        }

        public ServerBuilder name(Text name) {
            this.name = name;
            return this;
        }

        public ServerBuilder type(RocketPartType type) {
            this.partType = type;
            return this;
        }

        public ServerBuilder renderState(BlockState renderState) {
            this.renderState = renderState;
            return this;
        }

        public ServerBuilder recipe(boolean hasRecipe) {
            this.hasRecipe = hasRecipe;
            return this;
        }

        public ServerBuilder renderItem(ItemStack renderItem) {
            this.renderItem = renderItem;
            return this;
        }

        public ServerBuilder tier(TierGetter tier) {
            this.tier = tier;
            return this;
        }

        public RocketPart build() {
            if (name == null || partType == null || renderState == null) {
                throw new RuntimeException();
            }
            return new RocketPart(partType, name, renderState, renderItem == null ? new ItemStack(renderState.getBlock().asItem()) : renderItem, tier, hasRecipe);
        }
    }

    public static class ClientBuilder {
        private Renderer preRender = (stack, entity, vertexConsumers, tickDelta) -> {};
        private Renderer postRender = (stack, entity, vertexConsumers, tickDelta) -> {};

        @Environment(EnvType.CLIENT)
        public ClientBuilder preRender(Renderer renderer) {
            this.preRender = renderer;
            return this;
        }

        @Environment(EnvType.CLIENT)
        public ClientBuilder postRender(Renderer renderer) {
            this.postRender = renderer;
            return this;
        }
    }

    @FunctionalInterface
    public interface Renderer {
        void render(MatrixStack stack, RocketEntity entity, VertexConsumerProvider vertexConsumers, float tickDelta);
    }

    @FunctionalInterface
    public interface TierGetter {
        int getTier(List<RocketPart> pieces);
    }
}
