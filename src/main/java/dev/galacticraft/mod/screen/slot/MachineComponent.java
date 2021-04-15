/*
 * Copyright (c) 2020 Team Galacticraft
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

package dev.galacticraft.mod.screen.slot;

import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.client.gui.widget.machine.AbstractWidget;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MachineComponent<T> {
    private final T component;
    private final int x;
    private final int y;

    public MachineComponent(T component, int x, int y) {
        this.component = component;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public T getComponent() {
        return component;
    }

    @Override
    public String toString() {
        return "MachineComponent{" +
                "component=" + component +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MachineComponent<?> that = (MachineComponent<?>) o;
        return getX() == that.getX() && getY() == that.getY() && getComponent().equals(that.getComponent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponent(), getX(), getY());
    }

    @Nullable
    public AbstractWidget createWidget(MachineBlockEntity machine) {
        return null;
    }
}
