package com.hrznstudio.galacticraft.screen.slot;

import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.client.gui.widget.machine.AbstractWidget;

import javax.annotation.Nullable;
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
