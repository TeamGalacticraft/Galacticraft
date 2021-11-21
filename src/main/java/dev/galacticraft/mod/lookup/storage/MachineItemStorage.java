/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.lookup.storage;

import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.screen.slot.SlotSettings;
import dev.galacticraft.mod.screen.slot.SlotType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class MachineItemStorage extends CombinedStorage<ItemVariant, MachineItemStorage.MachineItemSlot> implements Automatable, Inventory {
    private final MachineBlockEntity machine;
    private final ItemStack[] stacks;
    private final SlotSettings[] slotSettings;
    private final SlotType[] types;
    private final ExposedItemStorage exposed;
    private final ReadOnlyItemStorage view;
    private final DirtyMarker marker;

    private MachineItemStorage(MachineBlockEntity machine, SlotSettings @NotNull [] slotSettings, ItemFilter @NotNull [] internalFilters) {
        super(Collections.emptyList());
        int size = slotSettings.length;
        assert internalFilters.length == size;

        this.machine = machine;
        this.slotSettings = slotSettings;
        this.stacks = new ItemStack[size];
        this.types = new SlotType[size];

        Arrays.fill(this.stacks, ItemStack.EMPTY);
        ImmutableList.Builder<MachineItemStorage.MachineItemSlot> builder = ImmutableList.builderWithExpectedSize(size);
        for (int i = 0; i < size; i++) {
            builder.add(new MachineItemSlot(i, slotSettings[i], internalFilters[i]));
            this.types[i] = slotSettings[i].type();
        }
        this.parts = builder.build();
        this.exposed = new ExposedItemStorage(slotSettings);
        this.view = new ReadOnlyItemStorage();
        this.marker = new DirtyMarker();
    }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public void setStack(int index, @NotNull ItemStack stack) {
        Preconditions.checkNotNull(stack);
        TransactionContext context = Transaction.getCurrentUnsafe();
        if (context != null) {
            this.setStack(index, stack, context);
        } else {
            if (this.stacks[index] != (this.stacks[index] = stack)) {
                this.markDirty();
            }
        }
    }

    public void setStack(int index, @NotNull ItemStack stack, TransactionContext context) {
        Preconditions.checkNotNull(stack);
        if (!stack.equals(this.getStack(index))) {
            this.parts.get(index).updateSnapshots(context);
            this.stacks[index] = stack;
        }
    }

    //returns failed
    public ItemStack insertStack(int index, @NotNull ItemStack stack, TransactionContext context) {
        Preconditions.checkNotNull(stack);
        this.parts.get(index).updateSnapshots(context);
        ItemStack stack1 = this.stacks[index];
        if (ItemVariant.of(stack).matches(stack1)) {
            int count = stack.getCount() + stack1.getCount();
            int min = Math.min(count, stack.getMaxCount());
            stack.setCount(min);
            stack1.setCount(count - min);
        }
        this.stacks[index] = stack;
        return stack1;
    }

    public ItemStack simulateInsertion(int index, @NotNull ItemStack stack, TransactionContext context) {
        try (Transaction transaction = context.openNested()) {
            return insertStack(index, stack, transaction);
        }
    }

    public ItemStack simulateInsertion(int index, @NotNull ItemStack stack) {
        try (Transaction transaction = Transaction.openOuter()) {
            return insertStack(index, stack, transaction);
        }
    }

    public ItemStack extractStack(int index, @NotNull ItemFilter filter, int amount, @NotNull TransactionContext context) {
        Preconditions.checkNotNull(filter);
        StoragePreconditions.notNegative(amount);

        ItemStack stack = this.getStack(index);
        if (amount != 0 && filter.matches(stack) && !stack.isEmpty()) {
            this.parts.get(index).updateSnapshots(context);
            int count = Math.min(stack.getCount(), amount);
            ItemStack copy = stack.copy();
            ItemStack copy1 = stack.copy();
            copy.setCount(count);
            copy1.setCount(stack.getCount() - count);
            this.stacks[index] = copy1;
            return copy;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack extractStack(int index, int amount, @NotNull TransactionContext context) {
        return extractStack(index, Constant.Filter.Item.ALWAYS, amount, context);
    }

    public ItemStack simulateExtraction(int index, int amount, @Nullable TransactionContext context) {
        try (Transaction transaction = Transaction.openNested(context)) {
            return extractStack(index, amount, transaction);
        }
    }

    public ItemStack simulateExtraction(int index, int amount) {
        return simulateExtraction(index, amount, null);
    }

    public ItemStack simulateExtraction(int index, @NotNull ItemFilter filter, int amount, @Nullable TransactionContext context) {
        try (Transaction transaction = Transaction.openNested(context)) {
            return extractStack(index, filter, amount, transaction);
        }
    }

    public ItemStack simulateExtraction(int index, @NotNull ItemFilter filter, int amount) {
        return simulateExtraction(index, filter, amount, null);
    }

    @Override
    public void markDirty() {
        this.machine.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.machine.getConfiguration().getSecurity().hasAccess(player);
    }

    public Inventory mapped(int... ints) {
        return new MappedInventory(ints, this);
    }

    public Inventory mappedFrom(int start, int len) {
        return MappedInventory.mappedRange(this, start, len);
    }

    public void writeNbt(NbtCompound nbt) {
        boolean empty = true;
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                empty = false;
                break;
            }
        }
        if (!empty) {
            NbtList list = new NbtList();
            for (ItemStack stack : this.stacks) {
                list.add(stack.writeNbt(new NbtCompound()));
            }
            nbt.put(Constant.Nbt.ITEMS, list);
        }
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains(Constant.Nbt.ITEMS, NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList(Constant.Nbt.ITEMS, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                this.stacks[i] = ItemStack.fromNbt(list.getCompound(i));
            }
        }
    }

    @Override
    public @NotNull ItemStack getStack(int index) {
        return this.stacks[index];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack copy = this.getStack(slot).copy();
        ItemStack copy2 = this.getStack(slot).copy();
        copy.setCount(Math.min(copy.getCount(), amount));
        copy2.setCount(copy2.getCount() - copy.getCount());
        this.setStack(slot, copy2);
        return copy;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = this.getStack(slot);
        this.setStack(slot, ItemStack.EMPTY);
        return stack;
    }

    public ExposedItemStorage exposed() {
        return this.exposed;
    }

    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return this.parts.get(slot);
    }

    public void createSlots(PlayerEntity player, Consumer<Slot> consumer) {
        for (int i = 0; i < this.slotSettings.length; i++) {
            consumer.accept(new ConfiguredItemSlot(this.vanilla(), i, player, this.slotSettings[i]));
        }
    }

    public Inventory vanilla() {
        return this;
    }

    public Storage<ItemVariant> view() {
        return this.view;
    }

    @Override
    public int size() {
        return this.stacks.length;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    public MachineBlockEntity machine() {
        return this.machine;
    }

    @Override
    public SlotType[] getTypes() {
        return this.types;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.stacks.length; i++) {
            this.removeStack(i);
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return this.parts.get(slot).filter().matches(stack);
    }

    protected class MachineItemSlot extends SingleStackStorage {
        private final int index;
        private final boolean insertion;
        private final boolean extraction;
        private final ItemFilter filter;

        public MachineItemSlot(int index, SlotSettings settings, ItemFilter filter) {
            this.index = index;
            this.filter = filter;
            this.insertion = settings.canTakeItems(); // the inversion is on purpose.
            this.extraction = settings.canInsertItems();
        }

        @Override
        public boolean supportsInsertion() {
            return this.insertion;
        }

        @Override
        public boolean supportsExtraction() {
            return this.extraction;
        }

        @Override
        protected boolean canInsert(@NotNull ItemVariant itemVariant) {
            return this.filter.matches(itemVariant.toStack());
        }

        @Override
        protected boolean canExtract(@NotNull ItemVariant itemVariant) {
            return this.filter.matches(itemVariant.toStack());
        }

        @Override
        public void updateSnapshots(TransactionContext transaction) {
            super.updateSnapshots(transaction);
            MachineItemStorage.this.marker.updateSnapshots(transaction);
        }

        @Override
        protected ItemStack getStack() {
            return MachineItemStorage.this.getStack(this.index);
        }

        @Override
        protected void setStack(ItemStack stack) {
            MachineItemStorage.this.stacks[this.index] = stack;
        }

        public ItemFilter filter() {
            return this.filter;
        }
    }

    public class ExposedItemStorage extends CombinedStorage<ItemVariant, ExposedItemStorage.ExposedItemSlot> implements Inventory {
        private final Storage<ItemVariant> insertion;
        private final Storage<ItemVariant> extraction;

        private ExposedItemStorage(SlotSettings[] settings) {
            super(Collections.emptyList());
            ImmutableList.Builder<ExposedItemSlot> builder = ImmutableList.builderWithExpectedSize(settings.length);
            for (int i = 0; i < settings.length; i++) {
                builder.add(new ExposedItemSlot(i, settings[i].type(), settings[i].filter()));
            }
            this.parts = builder.build();
            this.insertion = new DirectionalExposedItemStorage(true);
            this.extraction = new DirectionalExposedItemStorage(false);
        }

        public Inventory wrapper() {
            return this;
        }

        public Storage<ItemVariant> insertion() {
            return this.insertion;
        }

        public Storage<ItemVariant> extraction() {
            return this.extraction;
        }

        @Override
        public int size() {
            return MachineItemStorage.this.size();
        }

        @Override
        public boolean isEmpty() {
            return MachineItemStorage.this.isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            return MachineItemStorage.this.getStack(slot);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            if (amount == 0 || !this.parts.get(slot).supportsExtraction()) {
                return ItemStack.EMPTY;
            }

            return MachineItemStorage.this.removeStack(slot, amount);
        }

        @Override
        public ItemStack removeStack(int slot) {
            if (!this.parts.get(slot).supportsExtraction()) {
                return ItemStack.EMPTY;
            }

            return MachineItemStorage.this.removeStack(slot);
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            ItemStack stack1 = this.getStack(slot);
            ExposedItemSlot exposedItemSlot = this.parts.get(slot); //todo: improve this
            if (stack1.equals(stack)) return;
            if (!isValid(slot, stack)) return;
            if (stack.isEmpty() && !stack1.isEmpty() && !exposedItemSlot.supportsExtraction()) return;
            if (!stack.isEmpty() && stack1.isEmpty() && !exposedItemSlot.supportsInsertion()) return;
            if (ItemVariant.of(stack).matches(stack1)) {
                if (stack.getCount() < stack1.getCount()) {
                    if (!exposedItemSlot.supportsExtraction()) return;
                } else {
                    if (stack.getCount() + stack1.getCount() > stack.getItem().getMaxCount()) return;
                    if (!exposedItemSlot.supportsInsertion()) return;
                }
            } else {
                if (!(exposedItemSlot.supportsExtraction() && exposedItemSlot.supportsInsertion())) return;
            }

            MachineItemStorage.this.setStack(slot, stack);
        }

        @Override
        public void markDirty() {
            MachineItemStorage.this.markDirty();
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return MachineItemStorage.this.canPlayerUse(player);
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return stack.isEmpty() || this.parts.get(slot).filter.matches(stack);
        }

        @Override
        public void clear() {
            List<ExposedItemSlot> exposedItemSlots = this.parts;
            for (int i = 0; i < exposedItemSlots.size(); i++) {
                if (exposedItemSlots.get(i).supportsExtraction()) {
                    MachineItemStorage.this.removeStack(i);
                }
            }
        }

        private class ExposedItemSlot implements SingleSlotStorage<ItemVariant> {
            private final int index;
            private final SlotType type;
            private final ItemFilter filter;

            public ExposedItemSlot(int index, SlotType type, ItemFilter filter) {
                this.index = index;
                this.type = type;
                this.filter = filter;
            }

            @Override
            public boolean supportsInsertion() {
                return this.type.getType().isInput();
            }

            @Override
            public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                StoragePreconditions.notBlankNotNegative(resource, maxAmount);

                if (this.filter.matches(resource.toStack())) {
                    return MachineItemStorage.this.getSlot(this.index).insert(resource, maxAmount, transaction);
                }

                return 0;
            }

            @Override
            public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                StoragePreconditions.notBlankNotNegative(resource, maxAmount);

                if (this.filter.matches(resource.toStack())) {
                    return MachineItemStorage.this.getSlot(this.index).simulateInsert(resource, maxAmount, transaction);
                }

                return 0;
            }

            @Override
            public boolean supportsExtraction() {
                return this.type.getType().isOutput();
            }

            @Override
            public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                StoragePreconditions.notBlankNotNegative(resource, maxAmount);

                return MachineItemStorage.this.getSlot(this.index).extract(resource, maxAmount, transaction);
            }

            @Override
            public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                StoragePreconditions.notBlankNotNegative(resource, maxAmount);

                return MachineItemStorage.this.getSlot(this.index).simulateExtract(resource, maxAmount, transaction);
            }

            @Override
            public boolean isResourceBlank() {
                return this.getResource().isBlank();
            }

            @Override
            public ItemVariant getResource() {
                return MachineItemStorage.this.getSlot(this.index).getResource();
            }

            @Override
            public long getAmount() {
                return MachineItemStorage.this.getSlot(this.index).getAmount();
            }

            @Override
            public long getCapacity() {
                return MachineItemStorage.this.getSlot(this.index).getCapacity();
            }

            @Override
            public @Nullable StorageView<ItemVariant> exactView(TransactionContext transaction, ItemVariant resource) {
                return MachineItemStorage.this.getSlot(this.index).exactView(transaction, resource);
            }

            @Override
            public long getVersion() {
                return MachineItemStorage.this.getSlot(this.index).getVersion();
            }
        }

        public class DirectionalExposedItemStorage extends CombinedStorage<ItemVariant, DirectionalExposedItemStorage.DirectionalExposedItemSlot> {
            private DirectionalExposedItemStorage(boolean insertion) {
                super(Collections.emptyList());
                ImmutableList.Builder<DirectionalExposedItemSlot> builder = ImmutableList.builder();
                for (ExposedItemSlot exposedItemSlot : ExposedItemStorage.this.parts) {
                    if (insertion ? exposedItemSlot.supportsInsertion() : exposedItemSlot.supportsExtraction()) {
                        builder.add(new DirectionalExposedItemSlot(exposedItemSlot.index, insertion));
                    }
                }
                this.parts = builder.build();
            }

            private class DirectionalExposedItemSlot implements SingleSlotStorage<ItemVariant> {
                private final int index;
                private final boolean insertion;

                public DirectionalExposedItemSlot(int index, boolean insertion) {
                    this.index = index;
                    this.insertion = insertion;
                }

                @Override
                public boolean supportsInsertion() {
                    return this.insertion && ExposedItemStorage.this.supportsInsertion();
                }

                @Override
                public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                    if (this.insertion && ExposedItemStorage.this.supportsInsertion()) {
                        return ExposedItemStorage.this.parts.get(this.index).insert(resource, maxAmount, transaction);
                    }

                    return 0;
                }

                @Override
                public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                    if (this.insertion && ExposedItemStorage.this.supportsInsertion()) {
                        return ExposedItemStorage.this.parts.get(this.index).simulateInsert(resource, maxAmount, transaction);
                    }

                    return 0;
                }

                @Override
                public boolean supportsExtraction() {
                    return !this.insertion && ExposedItemStorage.this.supportsExtraction();
                }

                @Override
                public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                    if (!this.insertion && ExposedItemStorage.this.supportsExtraction()) {
                        return ExposedItemStorage.this.parts.get(this.index).extract(resource, maxAmount, transaction);
                    }
                    return 0;
                }

                @Override
                public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                    if (!this.insertion && ExposedItemStorage.this.supportsExtraction()) {
                        return ExposedItemStorage.this.parts.get(this.index).simulateExtract(resource, maxAmount, transaction);
                    }
                    return 0;
                }

                @Override
                public boolean isResourceBlank() {
                    return this.getResource().isBlank();
                }

                @Override
                public ItemVariant getResource() {
                    return ExposedItemStorage.this.parts.get(this.index).getResource();
                }

                @Override
                public long getAmount() {
                    return ExposedItemStorage.this.parts.get(this.index).getAmount();
                }

                @Override
                public long getCapacity() {
                    return ExposedItemStorage.this.parts.get(this.index).getCapacity();
                }

                @Override
                public @Nullable StorageView<ItemVariant> exactView(TransactionContext transaction, ItemVariant resource) {
                    return ExposedItemStorage.this.parts.get(this.index).exactView(transaction, resource);
                }

                @Override
                public long getVersion() {
                    return ExposedItemStorage.this.parts.get(this.index).getVersion();
                }
            }
        }
    }

    public class ReadOnlyItemStorage extends CombinedStorage<ItemVariant, ReadOnlyItemStorage.ReadOnlyItemSlot> {
        public ReadOnlyItemStorage() {
            super(Collections.emptyList());
            ImmutableList.Builder<ReadOnlyItemSlot> builder = ImmutableList.builderWithExpectedSize(MachineItemStorage.this.parts.size());
            for (int i = 0; i < MachineItemStorage.this.parts.size(); i++) {
                builder.add(new ReadOnlyItemSlot(i));
            }
            this.parts = builder.build();
        }

        @Override
        public boolean supportsInsertion() {
            return false;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
            return 0;
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
            return 0;
        }

        public class ReadOnlyItemSlot implements SingleSlotStorage<ItemVariant> {
            private final int index;

            public ReadOnlyItemSlot(int index) {
                this.index = index;
            }

            @Override
            public boolean supportsInsertion() {
                return false;
            }

            @Override
            public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                return 0;
            }

            @Override
            public boolean supportsExtraction() {
                return false;
            }

            @Override
            public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                return 0;
            }

            @Override
            public boolean isResourceBlank() {
                return MachineItemStorage.this.getSlot(this.index).isResourceBlank();
            }

            @Override
            public ItemVariant getResource() {
                return MachineItemStorage.this.getSlot(this.index).getResource();
            }

            @Override
            public long getAmount() {
                return MachineItemStorage.this.getSlot(this.index).getAmount();
            }

            @Override
            public long getCapacity() {
                return MachineItemStorage.this.getSlot(this.index).getCapacity();
            }
        }
    }

    public static class Builder {
        private final MachineBlockEntity machine;
        private final List<SlotSettings> slots = new ArrayList<>();
        private final List<ItemFilter> filters = new ArrayList<>();

        private Builder(MachineBlockEntity machine) {
            this.machine = machine;
        }

        @Contract("_ -> new")
        public static @NotNull Builder create(MachineBlockEntity machine) {
            return new Builder(machine);
        }

        public Builder addSlot(@NotNull SlotSettings settings) {
            return addSlot(settings, null);
        }

        public Builder addSlot(@NotNull SlotSettings settings, @Nullable ItemFilter internalFilter) {
            Preconditions.checkNotNull(settings);
            this.slots.add(settings);
            this.filters.add(internalFilter == null ? ConstantItemFilter.ANYTHING : internalFilter);
            return this;
        }

        public MachineItemStorage build() {
            return new MachineItemStorage(this.machine, this.slots.toArray(new SlotSettings[0]), this.filters.toArray(new ItemFilter[0]));
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "machine=" + machine +
                    ", slots=" + slots +
                    ", filters=" + filters +
                    '}';
        }
    }

    public static class ConfiguredItemSlot extends Slot {
        private final SlotSettings settings;
        private final PlayerEntity player;

        public ConfiguredItemSlot(Inventory inventory, int index, PlayerEntity player, @NotNull SlotSettings settings) {
            super(inventory, index, settings.x(), settings.y());
            this.settings = settings;
            this.player = player;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return this.settings.canTakeItems();
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return this.settings.canInsertItems() && this.settings.filter().matches(stack);
        }

        @Nullable
        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
            return this.settings.icon();
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int count) {
            ItemStack stack1 = super.insertStack(stack);
            this.settings.insertionListener().onChange(this.player, stack1);

            return stack1;
        }

        @Override
        public int getMaxItemCount() {
            return this.settings.maxCount();
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            super.onTakeItem(player, stack);
            this.settings.extractionListener().onChange(player, stack);
        }
    }

    public class DirtyMarker extends SnapshotParticipant<Boolean> {
        @Override
        protected Boolean createSnapshot() {
            return true;
        }

        @Override
        protected void readSnapshot(Boolean snapshot) {
        }

        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            MachineItemStorage.this.markDirty();
        }
    }

}
