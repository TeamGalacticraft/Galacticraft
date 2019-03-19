/*package io.github.teamgalacticraft.galacticraft.items;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class CannedFoodItem extends Item {

    public CannedFoodItem(int amount, float saturation, boolean wolfFood, Item.Settings settings) {
        super(amount, saturation, wolfFood, settings);
    }

    @Override
    public ItemStack onItemFinishedUsing(ItemStack stack, World world, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            player.getHungerManager().eat(this, stack);
            world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYER, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            this.onConsumed(stack, world, player);
            player.dropStack(new ItemStack(GalacticraftItems.TIN_CANISTER));
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            if (player instanceof ServerPlayerEntity) {
                Criterions.CONSUME_ITEM.handle((ServerPlayerEntity)player, stack);
            }
        }
        stack.subtractAmount(1);
        return stack;
    }

}*/
