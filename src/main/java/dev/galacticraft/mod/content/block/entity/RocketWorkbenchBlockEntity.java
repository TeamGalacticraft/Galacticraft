/*
 * Copyright (c) 2019-2024 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RocketWorkbenchBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    public final SimpleContainer output = new SimpleContainer(1) {
        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            return false;
        }
    };

    public final RecipeSelection<RocketCone<?, ?>> cone = new RecipeSelection<>(RocketRegistries.ROCKET_CONE, new VariableSizedContainer(0));
    public final RecipeSelection<RocketBody<?, ?>> body = new RecipeSelection<>(RocketRegistries.ROCKET_BODY, new VariableSizedContainer(0));
    public final RecipeSelection<RocketFin<?, ?>> fins = new RecipeSelection<>(RocketRegistries.ROCKET_FIN, new VariableSizedContainer(0));
    public final RecipeSelection<RocketBooster<?, ?>> booster = new RecipeSelection<>(RocketRegistries.ROCKET_BOOSTER, new VariableSizedContainer(0));
    public final RecipeSelection<RocketEngine<?, ?>> engine = new RecipeSelection<>(RocketRegistries.ROCKET_ENGINE, new VariableSizedContainer(0));
    public final RecipeSelection<RocketUpgrade<?, ?>> upgrade = new RecipeSelection<>(RocketRegistries.ROCKET_UPGRADE, new VariableSizedContainer(0));

    public byte[] color = new byte[4];

    public RocketWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.ROCKET_WORKBENCH, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        ListTag tag1 = this.output.createTag();
        if (!tag1.isEmpty()) tag.put("Output", tag1.get(0));
        tag.put("Cone", this.cone.toTag());
        tag.put("Body", this.body.toTag());
        tag.put("Fins", this.fins.toTag());
        tag.put("Booster", this.booster.toTag());
        tag.put("Engine", this.engine.toTag());
        tag.put("Upgrade", this.upgrade.toTag());
        tag.putByteArray("Color", this.color);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Output")) {
            ListTag list = new ListTag();
            list.add(tag.get("Output"));
            this.output.fromTag(list);
        }
        this.cone.readTag(tag.getCompound("Cone"));
        this.body.readTag(tag.getCompound("Body"));
        this.fins.readTag(tag.getCompound("Fins"));
        this.booster.readTag(tag.getCompound("Booster"));
        this.engine.readTag(tag.getCompound("Engine"));
        this.upgrade.readTag(tag.getCompound("Upgrade"));
        this.color = tag.getByteArray("Color");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.galacticraft.rocket_workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new RocketWorkbenchMenu(i, this, inventory);
    }

    public class RecipeSelection<P extends RocketPart<?, ?>> {
        private final ResourceKey<Registry<P>> key;
        public final VariableSizedContainer inventory;
        @Nullable
        public Holder.Reference<P> selection = null;

        public RecipeSelection(ResourceKey<Registry<P>> key, VariableSizedContainer inventory) {
            this.key = key;
            this.inventory = inventory;
        }

        public void setSelection(@Nullable Holder.Reference<P> selection) {
            if (this.selection != selection) {
                this.selection = selection;
                if (selection != null) {
                    assert selection.value().getRecipe() != null;
                    this.inventory.resize(selection.value().getRecipe().slots());
                } else {
                    this.inventory.resize(0);
                }
            }
        }

        public @Nullable P getSelection() {
            return this.selection != null ? this.selection.value() : null;
        }

        public @Nullable RocketPartRecipe<?, ?> getRecipe() {
            return this.selection != null ? this.selection.value().getRecipe() != null ? this.selection.value().getRecipe() : null : null;
        }

        public CompoundTag toTag() {
            CompoundTag nbt = this.inventory.toTag();
            if (this.selection != null) {
                nbt.putString("selection", this.selection.key().location().toString());
            }
            return nbt;
        }

        public void readTag(CompoundTag nbt) {
            this.inventory.readTag(nbt);
            String selLoc = nbt.getString("selection");
            if (!selLoc.isEmpty()) {
                this.setSelection(RocketWorkbenchBlockEntity.this.level.registryAccess().registryOrThrow(this.key).getHolderOrThrow(ResourceKey.create(this.key, new ResourceLocation(selLoc))));
            }
        }

        public @Nullable ResourceKey<P> getSelectionKey() {
            return this.selection == null ? null : this.selection.key();
        }
    }
}
