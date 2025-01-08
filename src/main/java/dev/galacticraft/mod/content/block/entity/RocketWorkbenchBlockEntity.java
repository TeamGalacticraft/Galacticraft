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
import dev.galacticraft.mod.recipe.RocketRecipe;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RocketWorkbenchBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<RocketWorkbenchMenu.OpeningData>, VariableSizedContainer.Listener {
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

    // Using a VariableSizedContainer here to support different recipes in the future
    public final VariableSizedContainer inventory = new VariableSizedContainer(14);

    public RocketWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.ROCKET_WORKBENCH, pos, state);
        this.inventory.addListener(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.saveAdditional(tag, registryLookup);

        ListTag outputTag = this.output.createTag(registryLookup);
        if (!outputTag.isEmpty()) tag.put("Output", outputTag.getFirst());
        tag.put("Cone", this.cone.toTag(registryLookup));
        tag.put("Body", this.body.toTag(registryLookup));
        tag.put("Fins", this.fins.toTag(registryLookup));
        tag.put("Booster", this.booster.toTag(registryLookup));
        tag.put("Engine", this.engine.toTag(registryLookup));
        tag.put("Upgrade", this.upgrade.toTag(registryLookup));
        tag.putByteArray("Color", this.color);
        tag.put("Inventory", this.inventory.toTag(registryLookup));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.loadAdditional(tag, registryLookup);
        if (tag.contains("Output")) {
            ListTag list = new ListTag();
            list.add(tag.get("Output"));
            this.output.fromTag(list, registryLookup);
        }
        this.cone.readTag(tag.getCompound("Cone"), registryLookup);
        this.body.readTag(tag.getCompound("Body"), registryLookup);
        this.fins.readTag(tag.getCompound("Fins"), registryLookup);
        this.booster.readTag(tag.getCompound("Booster"), registryLookup);
        this.engine.readTag(tag.getCompound("Engine"), registryLookup);
        this.upgrade.readTag(tag.getCompound("Upgrade"), registryLookup);
        this.color = tag.getByteArray("Color");
        this.inventory.readTag(tag.getCompound("Inventory"), registryLookup);
    }

//    private static RecipeHolder<RocketRecipe> lookUpRecipe(ResourceLocation id, HolderLookup.Provider registryLookup) {
//        RocketRecipe recipe = (RocketRecipe) registryLookup.lookupOrThrow(Registries.RECIPE).get(ResourceKey.create(Registries.RECIPE, id)).get().value();
//        return new RecipeHolder<>(id, recipe);
//    }

    public void resizeInventory(int newSize) {
        if (newSize >= this.inventory.getTargetSize()) {
            this.inventory.resize(newSize);
        } else {
            // TODO drop excess items
            this.inventory.resize(newSize);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return this.getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new RocketWorkbenchMenu(i, this, inventory);
    }

    @Override
    public void onSizeChanged() {
        this.setChanged();
    }

    @Override
    public void onItemChanged() {
        this.setChanged();
    }

    @Override
    public RocketWorkbenchMenu.OpeningData getScreenOpeningData(ServerPlayer player) {
        return new RocketWorkbenchMenu.OpeningData(this.getBlockPos(), this.cone.selection, this.body.selection, this.fins.selection, this.booster.selection, this.engine.selection, this.upgrade.selection);
    }

    public class RecipeSelection<P extends RocketPart<?, ?>> {
        private final ResourceKey<Registry<P>> key;
        public final VariableSizedContainer inventory;
        @Nullable
        public ResourceLocation selection = null;

        public RecipeSelection(ResourceKey<Registry<P>> key, VariableSizedContainer inventory) {
            this.key = key;
            this.inventory = inventory;

            this.inventory.addListener(RocketWorkbenchBlockEntity.this);
        }

        public void setSelection(@Nullable ResourceLocation selection) {
            if (this.selection != selection) {
                RocketWorkbenchBlockEntity.this.setChanged();
                this.selection = selection;

                updateSize();
            }
        }

        private void updateSize() {
            if (this.selection != null) {
                P value = this.getSelection();
                assert value.getRecipe() != null;
                this.inventory.resize(value.getRecipe().slots());
            } else {
                this.inventory.resize(0);
            }
        }

        public @Nullable P getSelection() {
            return this.selection != null ? RocketWorkbenchBlockEntity.this.level.registryAccess().registryOrThrow(this.key).get(this.selection) : null;
        }

        public @Nullable RocketPartRecipe<?, ?> getRecipe() {
            P value = this.getSelection();
            return value != null ? value.getRecipe() : null;
        }

        public CompoundTag toTag(HolderLookup.Provider lookup) {
            CompoundTag nbt = this.inventory.toTag(lookup);
            if (this.selection != null) {
                nbt.putString("selection", this.selection.toString());
//                nbt.putInt("size", this.inventory.getContainerSize());
            }
            return nbt;
        }

        public void readTag(CompoundTag nbt, HolderLookup.Provider lookup) {
            this.inventory.readTag(nbt, lookup);
            String selLoc = nbt.getString("selection");
            if (!selLoc.isEmpty()) {
                this.selection = ResourceLocation.parse(selLoc);
//                this.inventory.resize(nbt.getInt("size"));
            }
        }

//        public @Nullable ResourceKey<P> getSelectionKey() {
//            return this.selection == null ? null : ResourceKey.create(this.key, this.selection);
//        }
    }
}
