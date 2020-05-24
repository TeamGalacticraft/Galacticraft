package com.hrznstudio.galacticraft.screen;

import com.hrznstudio.galacticraft.block.entity.ResearchTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ResearchTableScreenHandler extends ScreenHandler {
    private final ResearchTableBlockEntity blockEntity;

    public ResearchTableScreenHandler(int syncId, PlayerEntity player, ResearchTableBlockEntity entity) {
        super(null, syncId);
        this.blockEntity = entity;

        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 94 + i * 18));
            }
        }

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(player.inventory, i, 8 + i * 18, 152));
        }
    }

    public ResearchTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
