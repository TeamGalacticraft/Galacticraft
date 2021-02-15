package com.hrznstudio.galacticraft.attribute.oxygen;

import alexiil.mc.lib.attributes.misc.Saveable;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OxygenTankImpl implements OxygenTank, Saveable {
    public static final OxygenTankImpl NULL = new OxygenTankImpl(-1);
    private final int capacity;
    private final List<Consumer<OxygenTank>> listeners = new ArrayList<>();
    private int amount;

    public OxygenTankImpl(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
        for (Consumer<OxygenTank> listener : listeners) {
            listener.accept(this);
        }
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("amount", amount);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.amount = tag.getInt("amount");
    }

    @Override
    public OxygenTankImpl listen(Consumer<OxygenTank> consumer) {
        listeners.add(consumer);
        return this;
    }

    @Override
    public void removeListeners() {
        this.listeners.clear();
    }
}
