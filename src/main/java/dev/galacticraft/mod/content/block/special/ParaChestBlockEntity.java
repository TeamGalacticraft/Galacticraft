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

package dev.galacticraft.mod.content.block.special;

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.entity.ScalableFuelLevel;
import dev.galacticraft.mod.screen.ParachestMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ParaChestBlockEntity extends RandomizableContainerBlockEntity implements SidedStorageBlockEntity, ExtendedScreenHandlerFactory, ScalableFuelLevel {

    public final SingleFluidStorage tank = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET * 5, () -> {
    });
    private NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    public ParaChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(GCBlockEntityTypes.PARACHEST, blockPos, blockState);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.tank.readNbt(compoundTag);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        ContainerHelper.saveAllItems(compoundTag, this.inventory);
        this.tank.writeNbt(compoundTag);
    }

    @Override
    protected Component getDefaultName() {
        return Component.literal("Parachest");
    }

    @Override
    public Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return tank;
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory inventory) {
        return new ParachestMenu(syncId, inventory, this);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public void setItems(NonNullList<ItemStack> items) {
        this.inventory = items;
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBoolean(true);
        buf.writeBlockPos(getBlockPos());
    }

    @Override
    public int getScaledFuelLevel(int scale) {
        final double fuelLevel = this.tank.getResource().isBlank() ? 0 : this.tank.getAmount();

        return (int) (fuelLevel * scale / this.tank.getCapacity());
    }

    public void tick() {
        ContainerItemContext context = ContainerItemContext.ofSingleSlot(InventoryStorage.of(this, null).getSlot(this.inventory.size() - 1));
        Storage<FluidVariant> fluidStorage = context.find(FluidStorage.ITEM);
        if (fluidStorage != null && !tank.isResourceBlank() && tank.getAmount() > 0) {
            try (Transaction tx = Transaction.openOuter()) {
                tank.extract(tank.getResource(), fluidStorage.insert(tank.getResource(), tank.getAmount(), tx), tx);
                tx.commit();
            }
        }
    }
}