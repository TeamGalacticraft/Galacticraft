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

package dev.galacticraft.mod.content.block.entity.decoration;

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FlagBlockEntity extends BannerBlockEntity {
    protected float yaw = 0;

    public FlagBlockEntity(BlockPos pos, BlockState state, DyeColor baseColor) {
        super(pos, state, baseColor);
    }

    public FlagBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registryLookup) {
        super.saveAdditional(compound, registryLookup);

        compound.putFloat("yaw", this.yaw);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registryLookup) {
        super.loadAdditional(compound, registryLookup);

        this.yaw = compound.getFloat("yaw");
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    public @NotNull Component getName() {
        return super.getCustomName() != null ? super.getCustomName() : Component.translatable("block.galacticraft.flag");
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack itemStack = new ItemStack(GCItems.FLAGS.get(this.getBaseColor()));
        itemStack.applyComponents(this.collectComponents());
        return itemStack;
    }

    // Overriding these two to replace the block entity type
    @Override
    public @NotNull BlockEntityType<FlagBlockEntity> getType() {
        return GCBlockEntityTypes.FLAG;
    }

    @Override
    public boolean isValidBlockState(BlockState state) {
        return GCBlockEntityTypes.FLAG.isValid(state);
    }
}
