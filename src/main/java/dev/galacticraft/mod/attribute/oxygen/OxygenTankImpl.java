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

package dev.galacticraft.mod.attribute.oxygen;

import alexiil.mc.lib.attributes.misc.Saveable;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
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
