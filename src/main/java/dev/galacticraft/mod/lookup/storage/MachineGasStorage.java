/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import dev.galacticraft.api.gas.Gas;
import dev.galacticraft.api.gas.GasStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.lookup.filter.GasFilter;
import dev.galacticraft.mod.screen.slot.GasSlotSettings;
import dev.galacticraft.mod.screen.slot.ResourceFlow;
import dev.galacticraft.mod.screen.slot.SlotType;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MachineGasStorage extends CombinedStorage<Gas, MachineGasStorage.MachineGasSlot> implements Automatable<Gas> {
    private final MachineBlockEntity machine;
    private final GasStack[] stacks;
    private final SlotType[] types;
    private final ExposedGasStorage exposed;
    private final ReadOnlyGasStorage view;
    private final DirtyMarker marker;

    private MachineGasStorage(MachineBlockEntity machine, GasSlotSettings @NotNull [] slotSettings, GasFilter @NotNull [] internalFilters) {
        super(Collections.emptyList());
        int size = slotSettings.length;
        assert internalFilters.length == size;

        this.machine = machine;
        this.stacks = new GasStack[size];
        this.types = new SlotType[size];

        Arrays.fill(this.stacks, GasStack.EMPTY);
        ImmutableList.Builder<MachineGasStorage.MachineGasSlot> builder = ImmutableList.builderWithExpectedSize(size);
        for (int i = 0; i < size; i++) {
            builder.add(new MachineGasSlot(i, slotSettings[i], internalFilters[i]));
            this.types[i] = slotSettings[i].type();
        }
        this.parts = builder.build();
        this.exposed = new ExposedGasStorage(slotSettings);
        this.view = new ReadOnlyGasStorage();
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

    public void setStack(int index, @NotNull GasStack stack) {
        Preconditions.checkNotNull(stack);
        TransactionContext context = Transaction.getCurrentUnsafe();
        if (context != null) {
            this.setStack(index, stack, context);
        } else {
            if (this.stacks[index] != stack) {
                this.stacks[index] = stack;
                this.markDirty();
            }
        }
    }

    public void setStack(int index, @NotNull GasStack stack, TransactionContext context) {
        Preconditions.checkNotNull(stack);
        if (!stack.equals(this.getStack(index))) {
            this.parts.get(index).updateSnapshots(context);
            this.stacks[index] = stack;
        }
    }

    //returns failed
    public GasStack insertStack(int index, @NotNull GasStack stack, TransactionContext context) {
        Preconditions.checkNotNull(stack);
        this.parts.get(index).updateSnapshots(context);
        GasStack stack1 = this.stacks[index];
        if (stack.gas() == stack1.gas()) {
            long count = stack.amount() + stack1.amount();
            long min = Math.min(count, this.parts.get(index).capacity);
            stack.setAmount(min);
            stack1.setAmount(count - min);
        }
        this.stacks[index] = stack;
        return stack1;
    }

    public GasStack simulateInsertion(int index, @NotNull GasStack stack, TransactionContext context) {
        try (Transaction transaction = Transaction.openNested(context)) {
            return insertStack(index, stack, transaction);
        }
    }

    public GasStack simulateInsertion(int index, @NotNull GasStack stack) {
        try (Transaction transaction = Transaction.openOuter()) {
            return insertStack(index, stack, transaction);
        }
    }

    public GasStack extractStack(int index, @NotNull GasFilter filter, long amount, @NotNull TransactionContext context) {
        Preconditions.checkNotNull(filter);
        StoragePreconditions.notNegative(amount);

        GasStack stack = this.getStack(index);
        if (amount != 0 && filter.test(stack) && !stack.isEmpty()) {
            this.parts.get(index).updateSnapshots(context);
            long count = Math.min(stack.amount(), amount);
            GasStack copy = stack.copy();
            GasStack copy1 = stack.copy();
            copy.setAmount(count);
            copy1.setAmount(stack.amount() - count);
            this.stacks[index] = copy1;
            return copy;
        }
        return GasStack.EMPTY;
    }

    public GasStack extractStack(int index, long amount, @NotNull TransactionContext context) {
        return extractStack(index, Constant.Filter.Gas.ALWAYS, amount, context);
    }

    public GasStack simulateExtraction(int index, long amount, @Nullable TransactionContext context) {
        try (Transaction transaction = Transaction.openNested(context)) {
            return extractStack(index, amount, transaction);
        }
    }

    public GasStack simulateExtraction(int index, long amount) {
        return simulateExtraction(index, amount, null);
    }

    public GasStack simulateExtraction(int index, @NotNull GasFilter filter, long amount, @Nullable TransactionContext context) {
        try (Transaction transaction = Transaction.openNested(context)) {
            return extractStack(index, filter, amount, transaction);
        }
    }

    public GasStack simulateExtraction(int index, @NotNull GasFilter filter, long amount) {
        return simulateExtraction(index, filter, amount, null);
    }

    public void markDirty() {
        this.machine.markDirty();
    }

    public void writeNbt(DynamicRegistryManager manager, NbtCompound nbt) {
        boolean empty = true;
        for (GasStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                empty = false;
                break;
            }
        }
        if (!empty) {
            NbtList list = new NbtList();
            for (GasStack stack : this.stacks) {
                list.add(stack.writeNbt(manager, new NbtCompound()));
            }
            nbt.put(Constant.Nbt.GASES, list);
        }
    }

    public void readNbt(DynamicRegistryManager manager, NbtCompound nbt) {
        if (nbt.contains(Constant.Nbt.GASES, NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList(Constant.Nbt.GASES, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                this.stacks[i] = GasStack.readNbt(manager, list.getCompound(i));
            }
        }
    }

    public @NotNull GasStack getStack(int index) {
        return this.stacks[index];
    }

    public GasStack removeStack(int slot, long amount) {
        GasStack copy = this.getStack(slot).copy();
        GasStack copy2 = this.getStack(slot).copy();
        copy.setAmount(Math.min(copy.amount(), amount));
        copy2.setAmount(copy2.amount() - copy.amount());
        this.setStack(slot, copy2);
        return copy;
    }

    public GasStack removeStack(int slot) {
        GasStack stack = this.getStack(slot);
        this.setStack(slot, GasStack.EMPTY);
        return stack;
    }

    public ExposedGasStorage exposed() {
        return this.exposed;
    }

    public SingleSlotStorage<Gas> getSlot(int slot) {
        return this.parts.get(slot);
    }
    
    public Storage<Gas> view() {
        return this.view;
    }

    public int size() {
        return this.stacks.length;
    }

    public boolean isEmpty() {
        for (GasStack stack : this.stacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    public MachineBlockEntity machine() {
        return this.machine;
    }

    @Override
    public SlotType<Gas>[] getTypes() {
        return this.types;
    }

    public boolean canInsert(int slot, GasStack stack) {
        return this.parts.get(slot).filter().test(stack);
    }

    public long getCapacity(int i) {
        return this.parts.get(i).capacity;
    }

    protected class MachineGasSlot extends SnapshotParticipant<GasStack> implements SingleSlotStorage<Gas> {
        private final int index;
        private final boolean insertion;
        private final boolean extraction;
        private final GasFilter filter;
        private final long capacity;

        public MachineGasSlot(int index, GasSlotSettings settings, GasFilter filter) {
            this.index = index;
            this.filter = filter;
            this.insertion = settings.canTakeGases(); // the inversion is on purpose.
            this.extraction = settings.canInsertGases();
            this.capacity = settings.capacity();
        }

        @Override
        public boolean supportsInsertion() {
            return this.insertion;
        }

        @Override
        public boolean supportsExtraction() {
            return this.extraction;
        }

        protected boolean canInsert(@NotNull Gas gas) {
            return this.filter.test(new GasStack(gas, 1));
        }

        protected boolean canExtract(@NotNull Gas gas) {
            return this.filter.test(new GasStack(gas, 1));
        }

        @Override
        public void updateSnapshots(TransactionContext transaction) {
            super.updateSnapshots(transaction);
            MachineGasStorage.this.marker.updateSnapshots(transaction);
        }

        public GasFilter filter() {
            return this.filter;
        }

        @Override
        public boolean isResourceBlank() {
            return MachineGasStorage.this.getStack(this.index).isEmpty();
        }

        @Override
        public Gas getResource() {
            return MachineGasStorage.this.getStack(this.index).gas();
        }

        @Override
        public long getAmount() {
            return MachineGasStorage.this.getStack(this.index).amount();
        }

        @Override
        public long getCapacity() {
            return this.capacity;
        }

        @Override
        public long insert(Gas gas, long amount, TransactionContext transaction) {
            StoragePreconditions.notNegative(amount);
            GasStack stack = MachineGasStorage.this.getStack(this.index);
            if ((gas == stack.gas() || stack.isEmpty()) && this.canInsert(gas)) {
                int insertedAmount = (int)Math.min(amount, this.capacity - stack.amount());
                if (insertedAmount > 0) {
                    this.updateSnapshots(transaction);
                    if (stack.isEmpty()) {
                        stack = new GasStack(gas, insertedAmount);
                    } else {
                        stack = stack.copy();
                        stack.setAmount(stack.amount() + insertedAmount);
                    }

                    MachineGasStorage.this.stacks[this.index] = stack;
                }

                return insertedAmount;
            }
            return 0;
        }

        @Override
        public long extract(Gas gas, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notNegative(maxAmount);
            GasStack currentStack = MachineGasStorage.this.getStack(this.index);
            if (gas == currentStack.gas() && this.canExtract(gas)) {
                long extracted = Math.min(currentStack.amount(), maxAmount);
                if (extracted > 0) {
                    this.updateSnapshots(transaction);
                    currentStack = MachineGasStorage.this.getStack(this.index);
                    currentStack.setAmount(currentStack.amount() - extracted);
                    MachineGasStorage.this.stacks[this.index] = currentStack;
                }

                return extracted;
            } else {
                return 0;
            }
        }

        @Override
        protected GasStack createSnapshot() {
            GasStack original = MachineGasStorage.this.getStack(this.index);
            MachineGasStorage.this.stacks[this.index] = original.copy();
            return original;
        }

        @Override
        protected void readSnapshot(GasStack snapshot) {
            MachineGasStorage.this.stacks[this.index] = snapshot;
        }
    }

    public class ExposedGasStorage extends CombinedStorage<Gas, ExposedGasStorage.ExposedGasSlot> {
        private final Storage<Gas> insertion;
        private final Storage<Gas> extraction;

        private ExposedGasStorage(GasSlotSettings[] settings) {
            super(Collections.emptyList());
            ImmutableList.Builder<ExposedGasSlot> builder = ImmutableList.builderWithExpectedSize(settings.length);
            for (int i = 0; i < settings.length; i++) {
                builder.add(new ExposedGasSlot(i, settings[i].type(), settings[i].filter()));
            }
            this.parts = builder.build();
            this.insertion = new DirectionalExposedGasStorage(true);
            this.extraction = new DirectionalExposedGasStorage(false);
        }

        public Storage<Gas> insertion() {
            return this.insertion;
        }

        public Storage<Gas> extraction() {
            return this.extraction;
        }
        
        private class ExposedGasSlot implements SingleSlotStorage<Gas> {
            private final int index;
            private final SlotType<Gas> type;
            private final GasFilter filter;

            public ExposedGasSlot(int index, SlotType<Gas> type, GasFilter filter) {
                this.index = index;
                this.type = type;
                this.filter = filter;
            }

            @Override
            public boolean supportsInsertion() {
                return this.type.getType().canFlow(ResourceFlow.INPUT);
            }

            @Override
            public long insert(Gas resource, long maxAmount, TransactionContext transaction) {
                StoragePreconditions.notNegative(maxAmount);

                if (this.filter.test(new GasStack(resource, 1))) {
                    return MachineGasStorage.this.getSlot(this.index).insert(resource, maxAmount, transaction);
                }

                return 0;
            }

            @Override
            public long simulateInsert(Gas resource, long maxAmount, @Nullable TransactionContext transaction) {
                StoragePreconditions.notNegative(maxAmount);

                if (this.filter.test(new GasStack(resource, 1))) {
                    return MachineGasStorage.this.getSlot(this.index).simulateInsert(resource, maxAmount, transaction);
                }

                return 0;
            }

            @Override
            public boolean supportsExtraction() {
                return this.type.getType().canFlow(ResourceFlow.OUTPUT);
            }

            @Override
            public long extract(Gas resource, long maxAmount, TransactionContext transaction) {
                StoragePreconditions.notNegative(maxAmount);

                return MachineGasStorage.this.getSlot(this.index).extract(resource, maxAmount, transaction);
            }

            @Override
            public long simulateExtract(Gas resource, long maxAmount, @Nullable TransactionContext transaction) {
                StoragePreconditions.notNegative(maxAmount);

                return MachineGasStorage.this.getSlot(this.index).simulateExtract(resource, maxAmount, transaction);
            }

            @Override
            public boolean isResourceBlank() {
                return MachineGasStorage.this.getSlot(this.index).isResourceBlank();
            }

            @Override
            public Gas getResource() {
                return MachineGasStorage.this.getSlot(this.index).getResource();
            }

            @Override
            public long getAmount() {
                return MachineGasStorage.this.getSlot(this.index).getAmount();
            }

            @Override
            public long getCapacity() {
                return MachineGasStorage.this.getSlot(this.index).getCapacity();
            }

            @Override
            public @Nullable StorageView<Gas> exactView(TransactionContext transaction, Gas resource) {
                return null; //todo
            }

            @Override
            public long getVersion() {
                return MachineGasStorage.this.getSlot(this.index).getVersion();
            }
        }

        public class DirectionalExposedGasStorage extends CombinedStorage<Gas, DirectionalExposedGasStorage.DirectionalExposedGasSlot> {
            private DirectionalExposedGasStorage(boolean insertion) {
                super(Collections.emptyList());
                ImmutableList.Builder<DirectionalExposedGasSlot> builder = ImmutableList.builder();
                for (ExposedGasSlot exposedGasSlot : ExposedGasStorage.this.parts) {
                    if (insertion ? exposedGasSlot.supportsInsertion() : exposedGasSlot.supportsExtraction()) {
                        builder.add(new DirectionalExposedGasSlot(exposedGasSlot.index, insertion));
                    }
                }
                this.parts = builder.build();
            }

            private class DirectionalExposedGasSlot implements SingleSlotStorage<Gas> {
                private final int index;
                private final boolean insertion;

                public DirectionalExposedGasSlot(int index, boolean insertion) {
                    this.index = index;
                    this.insertion = insertion;
                }

                @Override
                public boolean supportsInsertion() {
                    return this.insertion && ExposedGasStorage.this.supportsInsertion();
                }

                @Override
                public long insert(Gas resource, long maxAmount, TransactionContext transaction) {
                    if (this.insertion && ExposedGasStorage.this.supportsInsertion()) {
                        return ExposedGasStorage.this.parts.get(this.index).insert(resource, maxAmount, transaction);
                    }

                    return 0;
                }

                @Override
                public long simulateInsert(Gas resource, long maxAmount, @Nullable TransactionContext transaction) {
                    if (this.insertion && ExposedGasStorage.this.supportsInsertion()) {
                        return ExposedGasStorage.this.parts.get(this.index).simulateInsert(resource, maxAmount, transaction);
                    }

                    return 0;
                }

                @Override
                public boolean supportsExtraction() {
                    return !this.insertion && ExposedGasStorage.this.supportsExtraction();
                }

                @Override
                public long extract(Gas resource, long maxAmount, TransactionContext transaction) {
                    if (!this.insertion && ExposedGasStorage.this.supportsExtraction()) {
                        return ExposedGasStorage.this.parts.get(this.index).extract(resource, maxAmount, transaction);
                    }
                    return 0;
                }

                @Override
                public long simulateExtract(Gas resource, long maxAmount, @Nullable TransactionContext transaction) {
                    if (!this.insertion && ExposedGasStorage.this.supportsExtraction()) {
                        return ExposedGasStorage.this.parts.get(this.index).simulateExtract(resource, maxAmount, transaction);
                    }
                    return 0;
                }

                @Override
                public boolean isResourceBlank() {
                    return ExposedGasStorage.this.parts.get(this.index).isResourceBlank();
                }

                @Override
                public Gas getResource() {
                    return ExposedGasStorage.this.parts.get(this.index).getResource();
                }

                @Override
                public long getAmount() {
                    return ExposedGasStorage.this.parts.get(this.index).getAmount();
                }

                @Override
                public long getCapacity() {
                    return ExposedGasStorage.this.parts.get(this.index).getCapacity();
                }

                @Override
                public @Nullable StorageView<Gas> exactView(TransactionContext transaction, Gas resource) {
                    return null; //todo
                }

                @Override
                public long getVersion() {
                    return ExposedGasStorage.this.parts.get(this.index).getVersion();
                }
            }
        }
    }

    public class ReadOnlyGasStorage extends CombinedStorage<Gas, ReadOnlyGasStorage.ReadOnlyGasSlot> {
        public ReadOnlyGasStorage() {
            super(Collections.emptyList());
            ImmutableList.Builder<ReadOnlyGasSlot> builder = ImmutableList.builderWithExpectedSize(MachineGasStorage.this.parts.size());
            for (int i = 0; i < MachineGasStorage.this.parts.size(); i++) {
                builder.add(new ReadOnlyGasSlot(i));
            }
            this.parts = builder.build();
        }

        @Override
        public boolean supportsInsertion() {
            return false;
        }

        @Override
        public long insert(Gas resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long simulateInsert(Gas resource, long maxAmount, @Nullable TransactionContext transaction) {
            return 0;
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }

        @Override
        public long extract(Gas resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long simulateExtract(Gas resource, long maxAmount, @Nullable TransactionContext transaction) {
            return 0;
        }

        public class ReadOnlyGasSlot implements SingleSlotStorage<Gas> {
            private final int index;

            public ReadOnlyGasSlot(int index) {
                this.index = index;
            }

            @Override
            public boolean supportsInsertion() {
                return false;
            }

            @Override
            public long insert(Gas resource, long maxAmount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public long simulateInsert(Gas resource, long maxAmount, @Nullable TransactionContext transaction) {
                return 0;
            }

            @Override
            public boolean supportsExtraction() {
                return false;
            }

            @Override
            public long extract(Gas resource, long maxAmount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public long simulateExtract(Gas resource, long maxAmount, @Nullable TransactionContext transaction) {
                return 0;
            }

            @Override
            public boolean isResourceBlank() {
                return MachineGasStorage.this.getSlot(this.index).isResourceBlank();
            }

            @Override
            public Gas getResource() {
                return MachineGasStorage.this.getSlot(this.index).getResource();
            }

            @Override
            public long getAmount() {
                return MachineGasStorage.this.getSlot(this.index).getAmount();
            }

            @Override
            public long getCapacity() {
                return MachineGasStorage.this.getSlot(this.index).getCapacity();
            }
        }
    }

    public static class Builder {
        private final MachineBlockEntity machine;
        private final List<GasSlotSettings> slots = new ArrayList<>();
        private final List<GasFilter> filters = new ArrayList<>();

        private Builder(MachineBlockEntity machine) {
            this.machine = machine;
        }

        @Contract("_ -> new")
        public static @NotNull Builder create(MachineBlockEntity machine) {
            return new Builder(machine);
        }

        public Builder addSlot(@NotNull GasSlotSettings settings) {
            return addSlot(settings, null);
        }

        public Builder addSlot(@NotNull GasSlotSettings settings, @Nullable GasFilter internalFilter) {
            Preconditions.checkNotNull(settings);
            this.slots.add(settings);
            this.filters.add(internalFilter == null ? Constant.Filter.Gas.ALWAYS : internalFilter);
            return this;
        }

        public MachineGasStorage build() {
            return new MachineGasStorage(this.machine, this.slots.toArray(new GasSlotSettings[0]), this.filters.toArray(new GasFilter[0]));
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
            MachineGasStorage.this.markDirty();
        }
    }

}
