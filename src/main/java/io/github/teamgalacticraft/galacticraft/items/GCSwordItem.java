package io.github.teamgalacticraft.galacticraft.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GCSwordItem extends SwordItem {

    public GCSwordItem(ToolMaterial material, int additionalDamage, float cooldown, Settings settings) {
        super(material, additionalDamage, cooldown, settings);
    }


    @Override
    public boolean onBlockBroken(ItemStack stack, World world, BlockState blockState, BlockPos blockPos, LivingEntity entityLiving) {
        //All of these are stronger than vanilla
        if (blockState.getHardness(null, blockPos) > 0.2001F) {
            stack.applyDamage(2, entityLiving, (livingEntity) -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HAND_MAIN));
        }
        return true;
    }
}
