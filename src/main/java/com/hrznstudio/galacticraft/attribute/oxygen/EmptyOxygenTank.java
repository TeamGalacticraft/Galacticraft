package com.hrznstudio.galacticraft.attribute.oxygen;

import java.util.function.Consumer;

public enum EmptyOxygenTank implements OxygenTank {
    NULL;

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public void setAmount(int amount) {
    }

    @Override
    public int getAmount() {
        return 0;
    }

    @Override
    public OxygenTank listen(Consumer<OxygenTank> consumer) {
        return this;
    }

    @Override
    public void removeListeners() {
    }
}
