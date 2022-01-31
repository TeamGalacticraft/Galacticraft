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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.screen.slot.SlotType;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class MachineEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
	private final EnergyStorage view;
	private final ExposedEnergyStorage exposed;
	private final MachineBlockEntity machine;
	private final long capacity;
	private long energy = 0;

	public MachineEnergyStorage(MachineBlockEntity machine) {
		StoragePreconditions.notNegative(machine.getEnergyCapacity());
		this.machine = machine;
		this.capacity = machine.getEnergyCapacity();
		this.view = new EnergyStorageView();
		this.exposed = new ExposedEnergyStorage();
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		long inserted = Math.min(maxAmount, this.capacity - this.energy);

		if (inserted > 0) {
			this.updateSnapshots(transaction);
			this.energy += inserted;
			return inserted;
		}

		return 0;
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		long extracted = Math.min(maxAmount, this.energy);

		if (extracted > 0) {
			this.updateSnapshots(transaction);
			this.energy -= extracted;
			return extracted;
		}

		return 0;
	}

	public void writeNbt(NbtCompound nbt) {
		if (this.energy > 0 && this.capacity > 0) {
			nbt.putLong(Constant.Nbt.ENERGY, this.energy);
		}
	}

	public void readNbt(NbtCompound nbt) {
		if (nbt.contains(Constant.Nbt.ENERGY, NbtElement.LONG_TYPE)) {
			this.energy = nbt.getLong(Constant.Nbt.ENERGY);
		}
	}

	@Override
	protected Long createSnapshot() {
		return this.energy;
	}

	@Override
	protected void readSnapshot(Long amount) {
		this.energy = amount;
	}

	@Override
	protected void onFinalCommit() {
		super.onFinalCommit();
		this.machine.markDirty();
	}

	public void setEnergy(long amount) {
		this.energy = amount;
	}

	@Override
	public long getAmount() {
		return this.energy;
	}

	@Override
	public long getCapacity() {
		return this.capacity;
	}

	public EnergyStorage view() {
		return this.view;
	}

	public ExposedEnergyStorage exposed() {
		return this.exposed;
	}

	@Override
	public String toString() {
		return "MachineEnergyStorage{" +
				"machine=" + machine +
				", capacity=" + capacity +
				", energy=" + energy +
				'}';
	}

	public class EnergyStorageView extends SnapshotParticipant<Long> implements EnergyStorage {
		@Override
		protected Long createSnapshot() {
			return MachineEnergyStorage.this.createSnapshot();
		}

		@Override
		protected void readSnapshot(Long snapshot) {
			MachineEnergyStorage.this.readSnapshot(snapshot);
		}

		@Override
		public boolean supportsInsertion() {
			return false;
		}

		@Override
		public long insert(long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public boolean supportsExtraction() {
			return false;
		}

		@Override
		public long extract(long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public long getAmount() {
			return MachineEnergyStorage.this.getAmount();
		}

		@Override
		public long getCapacity() {
			return MachineEnergyStorage.this.getCapacity();
		}
	}

	public class ExposedEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
		private final ExposedDirectionalEnergyStorage insert;
		private final ExposedDirectionalEnergyStorage extract;
		private final long insertionRate;
		private final long extractionRate;

		private ExposedEnergyStorage() {
			StoragePreconditions.notNegative(MachineEnergyStorage.this.machine.energyInsertionRate());
			StoragePreconditions.notNegative(MachineEnergyStorage.this.machine.energyExtractionRate());
			this.insertionRate = MachineEnergyStorage.this.machine.energyInsertionRate();
			this.extractionRate = MachineEnergyStorage.this.machine.energyExtractionRate();
			this.insert = new ExposedDirectionalEnergyStorage(true);
			this.extract = new ExposedDirectionalEnergyStorage(false);
		}

		public EnergyStorage insertion() {
			return this.insert;
		}

		public EnergyStorage extraction() {
			return this.extract;
		}

		@Override
		protected Long createSnapshot() {
			return MachineEnergyStorage.this.createSnapshot();
		}

		@Override
		protected void readSnapshot(Long snapshot) {
			MachineEnergyStorage.this.readSnapshot(snapshot);
		}

		@Override
		public boolean supportsInsertion() {
			return this.insertionRate > 0;
		}

		@Override
		public long insert(long maxAmount, TransactionContext transaction) {
			return MachineEnergyStorage.this.insert(Math.min(this.insertionRate, maxAmount), transaction);
		}

		@Override
		public boolean supportsExtraction() {
			return this.extractionRate > 0;
		}

		@Override
		public long extract(long maxAmount, TransactionContext transaction) {
			return MachineEnergyStorage.this.extract(Math.min(this.extractionRate, maxAmount), transaction);
		}

		@Override
		public long getAmount() {
			return MachineEnergyStorage.this.getAmount();
		}

		@Override
		public long getCapacity() {
			return MachineEnergyStorage.this.getCapacity();
		}

		@Override
		protected void onFinalCommit() {
			super.onFinalCommit();
			MachineEnergyStorage.this.onFinalCommit();
		}

		private class ExposedDirectionalEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
			private final boolean insert;

			private ExposedDirectionalEnergyStorage(boolean insert) {
				this.insert = insert;
			}

			@Override
			protected Long createSnapshot() {
				return ExposedEnergyStorage.this.createSnapshot();
			}

			@Override
			protected void readSnapshot(Long snapshot) {
				ExposedEnergyStorage.this.readSnapshot(snapshot);
			}

			@Override
			public boolean supportsInsertion() {
				return this.insert;
			}

			@Override
			public long insert(long maxAmount, TransactionContext transaction) {
				if (insert) {
					return ExposedEnergyStorage.this.insert(maxAmount, transaction);
				}
				return 0;
			}

			@Override
			protected void onFinalCommit() {
				super.onFinalCommit();
				ExposedEnergyStorage.this.onFinalCommit();
			}

			@Override
			public boolean supportsExtraction() {
				return !this.insert;
			}

			@Override
			public long extract(long maxAmount, TransactionContext transaction) {
				if (!this.insert) {
					return ExposedEnergyStorage.this.extract(maxAmount, transaction);
				}
				return 0;
			}

			@Override
			public long getAmount() {
				return ExposedEnergyStorage.this.getAmount();
			}

			@Override
			public long getCapacity() {
				return ExposedEnergyStorage.this.getCapacity();
			}
		}
	}
}