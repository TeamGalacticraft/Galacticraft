package io.github.teamgalacticraft.galacticraft.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GCSwordItem extends SwordItem {

    public GCSwordItem(ToolMaterial material, int additionalDamage, float cooldown, Settings settings) {
        super(material, additionalDamage, cooldown, settings);
    }

    @Override
    public boolean onBlockBroken(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        //All of these are stronger than vanilla
        if (state.getHardness(null, pos) > 0.2001F) {
            stack.applyDamage(2, entityLiving);
        }
        return true;
    }
}
