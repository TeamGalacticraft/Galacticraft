package io.github.teamgalacticraft.galacticraft.container;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import net.minecraft.container.Container;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;


/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class PlayerInventoryContainer extends Container {

    public static final SimpleFixedItemInv inv = new SimpleFixedItemInv(11);
    private PlayerEntity playerEntity;

    public PlayerInventoryContainer(int syncId, PlayerEntity playerEntity) {
        super(null, syncId);
        this.playerEntity = playerEntity;
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return true;
    }
}
