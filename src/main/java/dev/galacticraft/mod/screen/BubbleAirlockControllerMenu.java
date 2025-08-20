package dev.galacticraft.mod.screen;

import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.MenuData;
import dev.galacticraft.mod.content.AirlockState;
import dev.galacticraft.mod.content.ProximityAccess;
import dev.galacticraft.mod.content.block.entity.BubbleAirlockControllerBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class BubbleAirlockControllerMenu extends MachineMenu<BubbleAirlockControllerBlockEntity> {
    public AirlockState state;
    public ProximityAccess access;

    public BubbleAirlockControllerMenu(int syncId, Player player, BubbleAirlockControllerBlockEntity be) {
        super(GCMenuTypes.BUBBLE_AIR_LOCK_CONTROLLER_MENU, syncId, player, be);
        this.state = be.getDisplayState();
        this.access = be.getAccess();
    }

    public BubbleAirlockControllerMenu(int syncId, Inventory inv, net.minecraft.core.BlockPos pos) {
        super(GCMenuTypes.BUBBLE_AIR_LOCK_CONTROLLER_MENU, syncId, inv, pos, 8, 89);
    }

    @Override
    public void registerData(@NotNull MenuData data) {
        super.registerData(data);
        data.registerInt(() -> this.be.getDisplayState().ordinal(), i -> this.state = AirlockState.values()[i]);
        data.registerInt(() -> this.be.getAccess().ordinal(),        i -> this.access = ProximityAccess.values()[i]);
    }
}