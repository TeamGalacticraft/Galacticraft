package io.github.teamgalacticraft.galacticraft.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DeshSwordItem extends SwordItem {

    public DeshSwordItem(ToolMaterial toolMaterial_1, int cooldown, float additionalDamage, Settings settings) {
        super(toolMaterial_1, cooldown, additionalDamage, settings);
        settings.durabilityIfNotSet(toolMaterial_1.getDurability());
    }

    @Override
    public boolean onBlockBroken(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        //Don't break blocks (that are hard) with swords !
        if (state.getHardness(null, pos) > 0.2001F)
        {
            stack.applyDamage(2, entityLiving);
        }
        return true;
    }
}
