package com.hrznstudio.galacticraft.items;

import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.accessor.GCPlayerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class OxygenGearItem extends Item {
    public OxygenGearItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (((GCPlayerAccessor) user).getGearInventory().getInvStack(5).isEmpty()) {
            ((GCPlayerAccessor) user).getGearInventory().setInvStack(5, user.getStackInHand(hand), Simulation.ACTION);
            return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
        }
        return super.use(world, user, hand);
    }
}
