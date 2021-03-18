package com.hrznstudio.galacticraft.screen.slot;

import net.minecraft.screen.slot.Slot;

public class MachineSlotComponent extends MachineComponent<Slot> {
    public MachineSlotComponent(Slot slot) {
        super(slot, slot.x, slot.y);
    }
}
