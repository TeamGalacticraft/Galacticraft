package io.github.teamgalacticraft.galacticraft.items;

import io.github.teamgalacticraft.galacticraft.accessor.GCPlayerAccessor;
import io.github.teamgalacticraft.galacticraft.container.PlayerInventoryGCContainer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class ThermalArmorItem extends Item {
    private EquipmentSlot slotType;

    public ThermalArmorItem(Settings item$Settings_1, EquipmentSlot slotType) {
        super(item$Settings_1);
        this.slotType = slotType;
    }

    public EquipmentSlot getSlotType() {
        return slotType;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world_1, PlayerEntity playerEntity_1, Hand hand_1) {
        PlayerInventoryGCContainer gcContainer = ((GCPlayerAccessor) playerEntity_1).getGCContainer();
        ItemStack thermalPiece = gcContainer.getThermalPiece(getSlotType());
        if (!thermalPiece.isEmpty()) {
            gcContainer.setThermalPiece(getSlotType(), playerEntity_1.getStackInHand(hand_1));
        }
        return super.use(world_1, playerEntity_1, hand_1);
    }
}