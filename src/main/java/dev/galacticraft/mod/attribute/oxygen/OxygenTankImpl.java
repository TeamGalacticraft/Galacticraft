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

import alexiil.mc.lib.attributes.ListenerToken;
import alexiil.mc.lib.attributes.misc.Saveable;
import dev.galacticraft.mod.Constant;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenTankImpl implements OxygenTank, Saveable {
    public static final OxygenTankImpl NULL = new OxygenTankImpl(-1);
    private final int capacity;
    private final List<OxygenTankChangedListener> listeners = new ArrayList<>();
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
        int prev = this.amount;
        this.amount = amount;
        for (OxygenTankChangedListener listener : listeners) {
            listener.onChanged(this, prev);
        }
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public NbtCompound toTag(NbtCompound tag) {
        tag.putInt(Constant.Nbt.AMOUNT, amount);
        return tag;
    }

    @Override
    public void fromTag(NbtCompound tag) {
        this.amount = tag.getInt(Constant.Nbt.AMOUNT);
    }

    @Override
    public @Nullable ListenerToken listen(OxygenTankChangedListener listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    @Override
    public void removeListeners() {
        this.listeners.clear();
    }
}
