package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import io.github.cottonmc.component.UniversalComponents;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * DISCLAIMER: ALL CODE HERE NOT FINAL, MAY ENCOUNTER BREAKING CHANGES REGULARLY
 *
 * This is a patched? version of the fluid API as it seems that deserialization and serialization doesn't work properly
 */
public final class FluidVolume {
	public static final FluidVolume EMPTY = new FluidVolume(Fluids.EMPTY);

	private Fluid fluid;
	private Fraction amount;
	private CompoundTag tag;

	private boolean empty;

	public FluidVolume(Fluid fluid) {
		this(fluid, Fraction.ONE);
	}

	public FluidVolume(Fluid fluid, Fraction amount) {
		this.fluid = fluid;
		this.amount = amount;
		this.updateEmptyState();
	}

	private FluidVolume(CompoundTag tag) {
		fluid = Registry.FLUID.get(new Identifier(tag.getString("Id")));
		if (tag.contains("Amount")) {
			int[] amounts = tag.getIntArray("Amount");
			if (amounts.length == 2) {
				amount = Fraction.of(amounts[0], amounts[1]);
			} else if (amounts.length == 1) {
				amount = Fraction.ofWhole(amounts[0]);
			} else {
				amount = Fraction.ZERO;
			}
		} else {
			amount = Fraction.ZERO;
		}

		if (tag.contains("Tag", NbtType.COMPOUND)) {
			this.tag = tag.getCompound("Tag");
		}

		this.updateEmptyState();
	}

	public Fluid getFluid() {
		return empty ? Fluids.EMPTY : fluid;
	}

	public Fraction getAmount() {
		return empty ? Fraction.ZERO : amount;
	}

	public boolean isEmpty() {
		if (this == EMPTY) {
			return true;
		} else if (this.getFluid() != null && this.getFluid() != Fluids.EMPTY) {
			return !this.amount.isPositive();
		} else {
			return true;
		}
	}

	public void setAmount(Fraction amount) {
		this.amount = amount;
	}

	public void increment(Fraction incrementBy) {
		amount = amount.add(incrementBy);
	}

	public void decrement(Fraction decrementBy) {
		amount = amount.subtract(decrementBy);
		if (amount.isNegative()) amount = Fraction.ZERO;
	}

	public FluidVolume split(Fraction amount) {
		Fraction min = Fraction.min(amount, this.amount);
		FluidVolume volume = this.copy();
		volume.setAmount(min);
		this.decrement(min);
		return volume;
	}

	//TODO: better equality/stackability methods
	public static boolean areFluidsEqual(FluidVolume left, FluidVolume right) {
		return left.getFluid() == right.getFluid();
	}

	//public static boolean areCombinable(FluidVolume left, FluidVolume right) {
	//	if (left == right) return true;
	//	if (left.isEmpty() && right.isEmpty()) return true;
	//}

	private void updateEmptyState() {
		empty = isEmpty();
	}

	public boolean hasTag() {
		return !empty && tag != null && !tag.isEmpty();
	}

	public CompoundTag getTag() {
		return tag;
	}

	public CompoundTag getOrCreateTag() {
		if (tag == null) {
			tag = new CompoundTag();
		}

		return tag;
	}

	public void setTag(CompoundTag tag) {
		this.tag = tag;
	}

	public FluidVolume copy() {
		if (this.isEmpty()) return FluidVolume.EMPTY;
		FluidVolume stack = new FluidVolume(this.fluid, this.amount);
		if (this.hasTag()) stack.setTag(this.getTag());
		return stack;
	}

	public static FluidVolume fromTag(CompoundTag tag) {
		return new FluidVolume(tag);
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag.putString("Id", Registry.FLUID.getId(getFluid()).toString());

		tag.put("Amount", Fraction.CODEC.encodeStart(NbtOps.INSTANCE, amount).resultOrPartial(UniversalComponents.logger::error).orElseGet(CompoundTag::new));

		if (this.tag != null) {
			tag.put("Tag", this.tag.copy());
		}

		return tag;
	}
}
