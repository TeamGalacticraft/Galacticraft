/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CannedFoodBlockEntity extends BlockEntity {
    private final List<ItemStack> canContents = new ArrayList<>();

    public CannedFoodBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(GCBlockEntityTypes.CANNED_FOOD, blockPos, blockState);
    }

    public int getCanCount() {
        return canContents.size();
    }

    public List<ItemStack> getCanContents() {
        return canContents;
    }

    public void addCanItem(ItemStack stack) {
        if (this.canContents.size() < 8) {
            this.canContents.add(stack);
            setChanged();
            if (level != null) {
                BlockState state = getBlockState();
                level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
                level.setBlocksDirty(worldPosition, state, state);

                if (!level.isClientSide) {
                    level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    public void dropStoredCans(Level level, BlockPos pos) {
        for (ItemStack stack : canContents) {
            Block.popResource(level, pos, stack);
        }
    }

    @Override
    public List<ItemStack> getRenderData() {
        return List.copyOf(this.getCanContents());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        this.canContents.clear();

        ListTag list = tag.getList(Constant.Nbt.CAN_CONTENTS, Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag itemTag = (CompoundTag) t;
            ItemStack stack = ItemStack.parseOptional(lookup, itemTag);
            if (!stack.isEmpty()) {
                this.canContents.add(stack);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putInt(Constant.Nbt.CAN_COUNT, getCanCount());
        ListTag list = new ListTag();
        for (ItemStack stack : canContents) {
            if (!stack.isEmpty()) {
                list.add(stack.save(lookup, new CompoundTag()));
            }
        }

        tag.put(Constant.Nbt.CAN_CONTENTS, list);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        CompoundTag tag = super.getUpdateTag(lookup);
        tag.putInt(Constant.Nbt.CAN_COUNT, getCanCount());
        ListTag list = new ListTag();
        for (ItemStack stack : canContents) {
            if (!stack.isEmpty()) {
                list.add(stack.save(lookup));
            }
        }
        tag.put(Constant.Nbt.CAN_CONTENTS, list);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
