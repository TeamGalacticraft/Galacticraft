package com.hrznstudio.galacticraft.attribute.oxygen;

import java.util.function.Consumer;

public class InfiniteOxygenTank implements OxygenTank {
    @Override
    public int getCapacity() {
        return 1620 * 64;
    }

    @Override
    public void setAmount(int amount) {
    }

    @Override
    public int getAmount() {
        return 1620 * 64;
    }

    @Override
    public OxygenTank listen(Consumer<OxygenTank> consumer) {
        return this;
    }

    @Override
    public void removeListeners() {
    }
}
