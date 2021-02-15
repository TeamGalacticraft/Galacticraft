package com.hrznstudio.galacticraft.attribute.oxygen;

import java.util.function.Consumer;

public interface OxygenTank {
    int getCapacity();

    void setAmount(int amount);

    int getAmount();

    OxygenTank listen(Consumer<OxygenTank> consumer);

    void removeListeners();
}
