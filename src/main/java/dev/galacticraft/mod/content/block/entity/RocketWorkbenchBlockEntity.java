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

import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
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

        @Override
        public boolean canTakeItem(Container target, int index, ItemStack stack) {
            return true;
        }
    };
    public final RecipeSelection cone = new RecipeSelection(new VariableSizedContainer(0));
    public final RecipeSelection body = new RecipeSelection(new VariableSizedContainer(0));
    public final RecipeSelection fins = new RecipeSelection(new VariableSizedContainer(0));
    public final RecipeSelection booster = new RecipeSelection(new VariableSizedContainer(0));
    public final RecipeSelection bottom = new RecipeSelection(new VariableSizedContainer(0));
    public final RecipeSelection upgrade = new RecipeSelection(new VariableSizedContainer(0));

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
        tag.put("Bottom", this.bottom.toTag());
        tag.put("Upgrade", this.upgrade.toTag());
        tag.putByteArray("Color", this.color);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ListTag list = new ListTag();
        list.add(tag.get("Output"));
        this.output.fromTag(list);
        this.cone.readTag(tag.getCompound("Cone"));
        this.body.readTag(tag.getCompound("Body"));
        this.fins.readTag(tag.getCompound("Fins"));
        this.booster.readTag(tag.getCompound("Booster"));
        this.bottom.readTag(tag.getCompound("Bottom"));
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

    public class RecipeSelection {
        public final VariableSizedContainer inventory;
        @Nullable
        public Holder.Reference<? extends RocketPart<?, ?>> selection = null;

        public RecipeSelection(VariableSizedContainer inventory) {
            this.inventory = inventory;
        }

        public void setSelection(@Nullable Holder.Reference<? extends RocketPart<?, ?>> selection) {
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

        public @Nullable RocketPart<?, ?> getSelection() {
            return this.selection != null ? this.selection.value() : null;
        }

        public @Nullable RocketPartRecipe<?, ?> getRecipe() {
            return this.selection != null ? this.selection.value().getRecipe() != null ? this.selection.value().getRecipe() : null : null;
        }

        public CompoundTag toTag() {
            CompoundTag nbt = this.inventory.toTag();
            if (this.selection != null) {
                nbt.putString("sel_reg", this.selection.key().registry().toString());
                nbt.putString("sel_loc", this.selection.key().location().toString());
            }
            return nbt;
        }

        public void readTag(CompoundTag nbt) {
            this.inventory.readTag(nbt);
            String selReg = nbt.getString("sel_reg");
            String selLoc = nbt.getString("sel_loc");
            if (!selReg.isEmpty() && !selLoc.isEmpty()) {
                this.setSelection((Holder.Reference<? extends RocketPart<?, ?>>) RocketWorkbenchBlockEntity.this.level.registryAccess().registryOrThrow(ResourceKey.createRegistryKey(new ResourceLocation(selReg))).get(new ResourceLocation(selLoc)));
            }
        }
    }
}
