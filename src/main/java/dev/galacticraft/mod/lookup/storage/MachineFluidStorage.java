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
import dev.galacticraft.api.fluid.FluidStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.lookup.filter.FluidFilter;
import dev.galacticraft.mod.screen.slot.FluidTankSettings;
import dev.galacticraft.mod.screen.slot.ResourceFlow;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.screen.tank.Tank;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class MachineFluidStorage extends CombinedStorage<FluidVariant, MachineFluidStorage.MachineFluidSlot> implements Automatable<FluidVariant> {
    private final MachineBlockEntity machine;
    private final FluidStack[] stacks;
    private final FluidTankSettings[] slotSettings;
    private final SlotType[] types;
    private final ExposedFluidStorage exposed;
    private final ReadOnlyFluidStorage view;
    private final DirtyMarker marker;

    private MachineFluidStorage(MachineBlockEntity machine, FluidTankSettings @NotNull [] slotSettings, FluidFilter @NotNull [] internalFilters) {
        super(Collections.emptyList());
        int size = slotSettings.length;
        assert internalFilters.length == size;

        this.machine = machine;
        this.slotSettings = slotSettings;
        this.stacks = new FluidStack[size];
        this.types = new SlotType[size];

        Arrays.fill(this.stacks, FluidStack.EMPTY);
        ImmutableList.Builder<MachineFluidStorage.MachineFluidSlot> builder = ImmutableList.builderWithExpectedSize(size);
        for (int i = 0; i < size; i++) {
            builder.add(new MachineFluidSlot(i, slotSettings[i], internalFilters[i]));
            this.types[i] = slotSettings[i].type();
        }
        this.parts = builder.build();
        this.exposed = new ExposedFluidStorage(slotSettings);
        this.view = new ReadOnlyFluidStorage();
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

    public void setStack(int index, @NotNull FluidStack stack) {
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

    public void setStack(int index, @NotNull FluidStack stack, TransactionContext context) {
        Preconditions.checkNotNull(stack);
        if (!stack.equals(this.getFluid(index))) {
            this.parts.get(index).updateSnapshots(context);
            this.stacks[index] = stack;
        }
    }

    //returns failed
    public FluidStack insertFluid(int index, @NotNull FluidStack stack, TransactionContext context) {
        Preconditions.checkNotNull(stack);
        this.parts.get(index).updateSnapshots(context);
        FluidStack stack1 = this.stacks[index];
        if (stack.fluid().equals(stack1.fluid())) {
            long count = stack.amount() + stack1.amount();
            long min = Math.min(count, this.parts.get(index).capacity);
            stack.setAmount(min);
            stack1.setAmount(count - min);
        }
        this.stacks[index] = stack;
        return stack1;
    }

    public boolean isFull(int tank) {
        assert this.getFluid(tank).amount() <= this.getCapacity(tank) : "Overfilled tank?!";
        return this.getFluid(tank).amount() >= this.getCapacity(tank);
    }

    public FluidStack simulateInsertion(int index, @NotNull FluidStack stack, TransactionContext context) {
        try (Transaction transaction = Transaction.openNested(context)) {
            return insertFluid(index, stack, transaction);
        }
    }

    public FluidStack simulateInsertion(int index, @NotNull FluidStack stack) {
        try (Transaction transaction = Transaction.openOuter()) {
            return insertFluid(index, stack, transaction);
        }
    }

    public FluidStack extractFluid(int index, @NotNull FluidFilter filter, long amount, @NotNull TransactionContext context) {
        Preconditions.checkNotNull(filter);
        StoragePreconditions.notNegative(amount);

        FluidStack stack = this.getFluid(index);
        if (amount != 0 && filter.test(stack.fluid()) && !stack.isEmpty()) {
            this.parts.get(index).updateSnapshots(context);
            long count = Math.min(stack.amount(), amount);
            FluidStack copy = stack.copy();
            FluidStack copy1 = stack.copy();
            copy.setAmount(count);
            copy1.setAmount(stack.amount() - count);
            this.stacks[index] = copy1;
            return copy;
        }
        return FluidStack.EMPTY;
    }

    public FluidStack extractFluid(int index, long amount, @NotNull TransactionContext context) {
        return extractFluid(index, Constant.Filter.Fluid.ALWAYS, amount, context);
    }

    public FluidStack simulateExtraction(int index, long amount, @Nullable TransactionContext context) {
        try (Transaction transaction = Transaction.openNested(context)) {
            return extractFluid(index, amount, transaction);
        }
    }

    public FluidStack simulateExtraction(int index, long amount) {
        return simulateExtraction(index, amount, null);
    }

    public FluidStack simulateExtraction(int index, @NotNull FluidFilter filter, long amount, @Nullable TransactionContext context) {
        try (Transaction transaction = Transaction.openNested(context)) {
            return extractFluid(index, filter, amount, transaction);
        }
    }

    public FluidStack simulateExtraction(int index, @NotNull FluidFilter filter, long amount) {
        return simulateExtraction(index, filter, amount, null);
    }

    public long getCapacity(int tank) {
        return this.parts.get(tank).capacity;
    }

    public void markDirty() {
        this.machine.markDirty();
    }

    public void writeNbt(NbtCompound nbt) {
        boolean empty = true;
        for (FluidStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                empty = false;
                break;
            }
        }
        if (!empty) {
            NbtList list = new NbtList();
            for (FluidStack stack : this.stacks) {
                list.add(stack.writeNbt(new NbtCompound()));
            }
            nbt.put(Constant.Nbt.ITEMS, list);
        }
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains(Constant.Nbt.ITEMS, NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList(Constant.Nbt.ITEMS, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                this.stacks[i] = FluidStack.readNbt(list.getCompound(i));
            }
        }
    }

    public @NotNull FluidStack getFluid(int index) {
        return this.stacks[index];
    }

    public FluidStack extractFluid(int slot, long amount) {
        FluidStack copy = this.getFluid(slot).copy();
        FluidStack copy2 = this.getFluid(slot).copy();
        copy.setAmount(Math.min(copy.amount(), amount));
        copy2.setAmount(copy2.amount() - copy.amount());
        this.setStack(slot, copy2);
        return copy;
    }

    public FluidStack clearTank(int slot) {
        FluidStack stack = this.getFluid(slot);
        this.setStack(slot, FluidStack.EMPTY);
        return stack;
    }

    public ExposedFluidStorage exposed() {
        return this.exposed;
    }

    public SingleSlotStorage<FluidVariant> getTank(int slot) {
        return this.parts.get(slot);
    }

    public void createTanks(PlayerEntity player, Consumer<Tank> consumer) { //todo
        for (int i = 0; i < this.slotSettings.length; i++) {
//            consumer.accept(new ConfiguredFluidSlot(this, i, player, this.slotSettings[i]));
        }
    }

    public Storage<FluidVariant> view() {
        return this.view;
    }

    public int size() {
        return this.stacks.length;
    }

    public boolean isEmpty() {
        for (FluidStack stack : this.stacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    public MachineBlockEntity machine() {
        return this.machine;
    }

    @Override
    public SlotType<FluidVariant>[] getTypes() {
        return this.types;
    }

    public FluidFilter getFilter(int slot) {
        return this.parts.get(slot).filter();
    }

    public boolean canAccept(int slot, FluidStack stack) {
        return this.parts.get(slot).filter().test(stack.fluid());
    }

    protected class MachineFluidSlot extends SnapshotParticipant<FluidStack> implements SingleSlotStorage<FluidVariant> {
        private final int index;
        private final boolean insertion;
        private final boolean extraction;
        private final FluidFilter filter;
        private final long capacity;

        public MachineFluidSlot(int index, FluidTankSettings settings, FluidFilter filter) {
            this.index = index;
            this.filter = filter;
            this.insertion = settings.canExtractFluids(); // the inversion is on purpose.
            this.extraction = settings.canInsertFluids();
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

        protected boolean canAccept(@NotNull FluidVariant fluid) {
            return this.filter.test(fluid);
        }

        @Override
        public void updateSnapshots(TransactionContext transaction) {
            super.updateSnapshots(transaction);
            MachineFluidStorage.this.marker.updateSnapshots(transaction);
        }

        @Override
        public boolean isResourceBlank() {
            return MachineFluidStorage.this.getFluid(this.index).isEmpty();
        }

        @Override
        public final FluidVariant getResource() {
            return MachineFluidStorage.this.getFluid(this.index).fluid();
        }

        @Override
        public long getAmount() {
            return MachineFluidStorage.this.getFluid(this.index).amount();
        }

        @Override
        public long getCapacity() {
            return this.capacity;
        }

        @Override
        public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);
            FluidStack currentStack = MachineFluidStorage.this.getFluid(this.index);
            if ((currentStack.fluid() == insertedVariant || currentStack.isEmpty()) && this.canAccept(insertedVariant)) {
                int insertedAmount = (int)Math.min(maxAmount, this.capacity - currentStack.amount());
                if (insertedAmount > 0) {
                    this.updateSnapshots(transaction);
                    currentStack = MachineFluidStorage.this.getFluid(this.index);
                    if (currentStack.isEmpty()) {
                        currentStack = new FluidStack(insertedVariant, insertedAmount);
                    } else {
                        currentStack.setAmount(currentStack.amount() + insertedAmount);
                    }

                    MachineFluidStorage.this.stacks[this.index] = currentStack;
                }

                return insertedAmount;
            } else {
                return 0;
            }
        }

        @Override
        public long extract(FluidVariant variant, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(variant, maxAmount);
            FluidStack currentStack = MachineFluidStorage.this.getFluid(this.index);
            if (currentStack.fluid() == variant && this.canAccept(variant)) {
                int extracted = (int)Math.min(currentStack.amount(), maxAmount);
                if (extracted > 0) {
                    this.updateSnapshots(transaction);
                    currentStack = MachineFluidStorage.this.getFluid(this.index);
                    currentStack.setAmount(currentStack.amount() - extracted);
                    MachineFluidStorage.this.stacks[this.index] = currentStack;
                }

                return extracted;
            } else {
                return 0;
            }
        }

        @Override
        protected final FluidStack createSnapshot() {
            FluidStack original = MachineFluidStorage.this.getFluid(this.index);
            MachineFluidStorage.this.stacks[this.index] = original.copy();
            return original;
        }

        @Override
        protected void readSnapshot(FluidStack snapshot) {
            MachineFluidStorage.this.stacks[this.index] = snapshot;
        }
        
        public FluidFilter filter() {
            return this.filter;
        }
    }

    public class ExposedFluidStorage extends CombinedStorage<FluidVariant, ExposedFluidStorage.ExposedFluidSlot> {
        private final Storage<FluidVariant> insertion;
        private final Storage<FluidVariant> extraction;

        private ExposedFluidStorage(FluidTankSettings[] settings) {
            super(Collections.emptyList());
            ImmutableList.Builder<ExposedFluidSlot> builder = ImmutableList.builderWithExpectedSize(settings.length);
            for (int i = 0; i < settings.length; i++) {
                builder.add(new ExposedFluidSlot(i, settings[i].type(), settings[i].filter()));
            }
            this.parts = builder.build();
            this.insertion = new DirectionalExposedFluidStorage(true);
            this.extraction = new DirectionalExposedFluidStorage(false);
        }

        public Storage<FluidVariant> insertion() {
            return this.insertion;
        }

        public Storage<FluidVariant> extraction() {
            return this.extraction;
        }

        public boolean canAccept(int slot, FluidStack stack) {
            return stack.isEmpty() || this.parts.get(slot).filter.test(stack.fluid());
        }

        private class ExposedFluidSlot implements SingleSlotStorage<FluidVariant> {
            private final int index;
            private final SlotType<FluidVariant> type;
            private final FluidFilter filter;

            public ExposedFluidSlot(int index, SlotType<FluidVariant> type, FluidFilter filter) {
                this.index = index;
                this.type = type;
                this.filter = filter;
            }

            @Override
            public boolean supportsInsertion() {
                return this.type.getType().canFlow(ResourceFlow.INPUT);
            }

            @Override
            public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                StoragePreconditions.notBlankNotNegative(resource, maxAmount);

                if (this.filter.test(resource)) {
                    return MachineFluidStorage.this.getTank(this.index).insert(resource, maxAmount, transaction);
                }

                return 0;
            }

            @Override
            public long simulateInsert(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                StoragePreconditions.notBlankNotNegative(resource, maxAmount);

                if (this.filter.test(resource)) {
                    return MachineFluidStorage.this.getTank(this.index).simulateInsert(resource, maxAmount, transaction);
                }

                return 0;
            }

            @Override
            public boolean supportsExtraction() {
                return this.type.getType().canFlow(ResourceFlow.OUTPUT);
            }

            @Override
            public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                StoragePreconditions.notBlankNotNegative(resource, maxAmount);

                return MachineFluidStorage.this.getTank(this.index).extract(resource, maxAmount, transaction);
            }

            @Override
            public long simulateExtract(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                StoragePreconditions.notBlankNotNegative(resource, maxAmount);

                return MachineFluidStorage.this.getTank(this.index).simulateExtract(resource, maxAmount, transaction);
            }

            @Override
            public boolean isResourceBlank() {
                return MachineFluidStorage.this.getTank(this.index).isResourceBlank();
            }

            @Override
            public FluidVariant getResource() {
                return MachineFluidStorage.this.getTank(this.index).getResource();
            }

            @Override
            public long getAmount() {
                return MachineFluidStorage.this.getTank(this.index).getAmount();
            }

            @Override
            public long getCapacity() {
                return MachineFluidStorage.this.getTank(this.index).getCapacity();
            }

            @Override
            public @Nullable StorageView<FluidVariant> exactView(TransactionContext transaction, FluidVariant resource) {
                return MachineFluidStorage.this.getTank(this.index).exactView(transaction, resource);
            }

            @Override
            public long getVersion() {
                return MachineFluidStorage.this.getTank(this.index).getVersion();
            }
        }

        public class DirectionalExposedFluidStorage extends CombinedStorage<FluidVariant, DirectionalExposedFluidStorage.DirectionalExposedFluidSlot> {
            private DirectionalExposedFluidStorage(boolean insertion) {
                super(Collections.emptyList());
                ImmutableList.Builder<DirectionalExposedFluidSlot> builder = ImmutableList.builder();
                for (ExposedFluidSlot exposedFluidSlot : ExposedFluidStorage.this.parts) {
                    if (insertion ? exposedFluidSlot.supportsInsertion() : exposedFluidSlot.supportsExtraction()) {
                        builder.add(new DirectionalExposedFluidSlot(exposedFluidSlot.index, insertion));
                    }
                }
                this.parts = builder.build();
            }

            private class DirectionalExposedFluidSlot implements SingleSlotStorage<FluidVariant> {
                private final int index;
                private final boolean insertion;

                public DirectionalExposedFluidSlot(int index, boolean insertion) {
                    this.index = index;
                    this.insertion = insertion;
                }

                @Override
                public boolean supportsInsertion() {
                    return this.insertion && ExposedFluidStorage.this.supportsInsertion();
                }

                @Override
                public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                    if (this.insertion && ExposedFluidStorage.this.supportsInsertion()) {
                        return ExposedFluidStorage.this.parts.get(this.index).insert(resource, maxAmount, transaction);
                    }

                    return 0;
                }

                @Override
                public long simulateInsert(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                    if (this.insertion && ExposedFluidStorage.this.supportsInsertion()) {
                        return ExposedFluidStorage.this.parts.get(this.index).simulateInsert(resource, maxAmount, transaction);
                    }

                    return 0;
                }

                @Override
                public boolean supportsExtraction() {
                    return !this.insertion && ExposedFluidStorage.this.supportsExtraction();
                }

                @Override
                public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                    if (!this.insertion && ExposedFluidStorage.this.supportsExtraction()) {
                        return ExposedFluidStorage.this.parts.get(this.index).extract(resource, maxAmount, transaction);
                    }
                    return 0;
                }

                @Override
                public long simulateExtract(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                    if (!this.insertion && ExposedFluidStorage.this.supportsExtraction()) {
                        return ExposedFluidStorage.this.parts.get(this.index).simulateExtract(resource, maxAmount, transaction);
                    }
                    return 0;
                }

                @Override
                public boolean isResourceBlank() {
                    return ExposedFluidStorage.this.parts.get(this.index).isResourceBlank();
                }

                @Override
                public FluidVariant getResource() {
                    return ExposedFluidStorage.this.parts.get(this.index).getResource();
                }

                @Override
                public long getAmount() {
                    return ExposedFluidStorage.this.parts.get(this.index).getAmount();
                }

                @Override
                public long getCapacity() {
                    return ExposedFluidStorage.this.parts.get(this.index).getCapacity();
                }

                @Override
                public @Nullable StorageView<FluidVariant> exactView(TransactionContext transaction, FluidVariant resource) {
                    return ExposedFluidStorage.this.parts.get(this.index).exactView(transaction, resource);
                }

                @Override
                public long getVersion() {
                    return ExposedFluidStorage.this.parts.get(this.index).getVersion();
                }
            }
        }
    }

    public class ReadOnlyFluidStorage extends CombinedStorage<FluidVariant, ReadOnlyFluidStorage.ReadOnlyFluidSlot> {
        public ReadOnlyFluidStorage() {
            super(Collections.emptyList());
            ImmutableList.Builder<ReadOnlyFluidSlot> builder = ImmutableList.builderWithExpectedSize(MachineFluidStorage.this.parts.size());
            for (int i = 0; i < MachineFluidStorage.this.parts.size(); i++) {
                builder.add(new ReadOnlyFluidSlot(i));
            }
            this.parts = builder.build();
        }

        @Override
        public boolean supportsInsertion() {
            return false;
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long simulateInsert(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
            return 0;
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long simulateExtract(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
            return 0;
        }

        public class ReadOnlyFluidSlot implements SingleSlotStorage<FluidVariant> {
            private final int index;

            public ReadOnlyFluidSlot(int index) {
                this.index = index;
            }

            @Override
            public boolean supportsInsertion() {
                return false;
            }

            @Override
            public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public long simulateInsert(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                return 0;
            }

            @Override
            public boolean supportsExtraction() {
                return false;
            }

            @Override
            public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public long simulateExtract(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
                return 0;
            }

            @Override
            public boolean isResourceBlank() {
                return MachineFluidStorage.this.getTank(this.index).isResourceBlank();
            }

            @Override
            public FluidVariant getResource() {
                return MachineFluidStorage.this.getTank(this.index).getResource();
            }

            @Override
            public long getAmount() {
                return MachineFluidStorage.this.getTank(this.index).getAmount();
            }

            @Override
            public long getCapacity() {
                return MachineFluidStorage.this.getTank(this.index).getCapacity();
            }
        }
    }

    public static class Builder {
        private final MachineBlockEntity machine;
        private final List<FluidTankSettings> slots = new ArrayList<>();
        private final List<FluidFilter> filters = new ArrayList<>();

        private Builder(MachineBlockEntity machine) {
            this.machine = machine;
        }

        @Contract("_ -> new")
        public static @NotNull Builder create(MachineBlockEntity machine) {
            return new Builder(machine);
        }

        public Builder addTank(@NotNull FluidTankSettings settings) {
            return addTank(settings, Constant.Filter.Fluid.ALWAYS);
        }

        public Builder addTank(@NotNull FluidTankSettings settings, @NotNull FluidFilter internalFilter) {
            Preconditions.checkNotNull(settings);
            this.slots.add(settings);
            this.filters.add(internalFilter);
            return this;
        }

        public MachineFluidStorage build() {
            return new MachineFluidStorage(this.machine, this.slots.toArray(new FluidTankSettings[0]), this.filters.toArray(new FluidFilter[0]));
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
            MachineFluidStorage.this.markDirty();
        }
    }

}
