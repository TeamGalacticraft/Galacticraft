/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.content.block.entity.machine;

import dev.galacticraft.machinelib.api.block.entity.BasicRecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.compat.vanilla.MachineTakeHandler;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.mod.Constant;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * A machine that processes vanilla cooking recipes and stores the experience earned from them,
 * in the same way as a vanilla furnace.
 * The experience is awarded when a player takes items from an output slot, or when the machine is broken.
 */
public abstract class CookingMachineBlockEntity<R extends AbstractCookingRecipe> extends BasicRecipeMachineBlockEntity<SingleRecipeInput, R> implements MachineTakeHandler {
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

    protected CookingMachineBlockEntity(BlockEntityType<? extends CookingMachineBlockEntity<R>> type, BlockPos pos, BlockState state, RecipeType<R> recipeType, StorageSpec spec,
                                        int inputSlots, int inputSlotsLen, int outputSlots, int outputSlotsLen) {
        super(type, pos, state, recipeType, spec, inputSlots, inputSlotsLen, outputSlots, outputSlotsLen);
    }

    @Override
    protected void craft(@NotNull ProfilerFiller profiler, @NotNull RecipeHolder<R> recipe) {
        super.craft(profiler, recipe);
        this.recipesUsed.addTo(recipe.id(), 1);
        this.setChanged();
    }

    /**
     * {@return the number of times each recipe has been crafted since the experience was last collected}
     */
    @VisibleForTesting
    @ApiStatus.Internal
    public Object2IntMap<ResourceLocation> getRecipesUsed() {
        return this.recipesUsed;
    }

    /**
     * Spawns experience orbs for all the recipes crafted since the experience was last collected.
     *
     * @param level The level to spawn the experience orbs in.
     * @param pos The position to spawn the experience orbs at.
     */
    public void popExperience(@NotNull ServerLevel level, @NotNull Vec3 pos) {
        for (Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent(holder -> {
                if (holder.value() instanceof AbstractCookingRecipe recipe) {
                    createExperience(level, pos, entry.getIntValue(), recipe.getExperience());
                }
            });
        }
        this.recipesUsed.clear();
    }

    @Override
    public void onTake(ServerPlayer player) {
        this.popExperience(player.serverLevel(), player.position());
    }

    private static void createExperience(ServerLevel level, Vec3 pos, int count, float experience) {
        float total = count * experience;
        int amount = Mth.floor(total);
        float remainder = Mth.frac(total);
        if (remainder != 0.0F && level.getRandom().nextFloat() < remainder) {
            ++amount;
        }

        ExperienceOrb.award(level, pos, amount);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        CompoundTag recipes = new CompoundTag();
        this.recipesUsed.forEach((id, count) -> recipes.putInt(id.toString(), count));
        tag.put(Constant.Nbt.RECIPES_USED, recipes);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        this.recipesUsed.clear();
        CompoundTag recipes = tag.getCompound(Constant.Nbt.RECIPES_USED);
        for (String id : recipes.getAllKeys()) {
            this.recipesUsed.put(ResourceLocation.parse(id), recipes.getInt(id));
        }
    }
}
