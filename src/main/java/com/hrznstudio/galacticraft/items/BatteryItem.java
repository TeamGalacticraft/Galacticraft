package com.hrznstudio.galacticraft.items;

import com.hrznstudio.galacticraft.api.item.EnergyHolderItem;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BatteryItem extends Item implements EnergyHolderItem {
    public static final int MAX_ENERGY = 10000;

    public BatteryItem(Settings settings) {
        super(settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void buildTooltip(ItemStack stack, World world, List<Component> lines, TooltipContext context) {
        int charge = stack.getOrCreateTag().getInt("Energy");
        if (stack.getDurability() - stack.getDamage() < 3334) {
            lines.add(new TranslatableComponent("tooltip.galacticraft-rewoven.energy-remaining", charge).setStyle(new Style().setColor(ChatFormat.DARK_RED)));
        } else if (stack.getDurability() - stack.getDamage() < 6667) {
            lines.add(new TranslatableComponent("tooltip.galacticraft-rewoven.energy-remaining", charge).setStyle(new Style().setColor(ChatFormat.GOLD)));
        } else {
            lines.add(new TranslatableComponent("tooltip.galacticraft-rewoven.energy-remaining", charge).setStyle(new Style().setColor(ChatFormat.GREEN)));
        }
        super.buildTooltip(stack, world, lines, context);
    }

    @Override
    public void appendItemsForGroup(ItemGroup group, DefaultedList<ItemStack> groupStacks) {
        if (this.isInItemGroup(group)) {
            ItemStack charged = new ItemStack(this);
            GalacticraftEnergy.setEnergy(charged, 0);
            groupStacks.add(charged);

            ItemStack depleted = new ItemStack(this);
            GalacticraftEnergy.setEnergy(depleted, MAX_ENERGY);
            groupStacks.add(depleted);
        }
    }

    @Override
    public void onCrafted(ItemStack battery, World world_1, PlayerEntity playerEntity_1) {
        CompoundTag batteryTag = battery.getOrCreateTag();
        batteryTag.putInt("Energy", 0);
        batteryTag.putInt("MaxEnergy", BatteryItem.MAX_ENERGY);
        battery.setDamage(BatteryItem.MAX_ENERGY);
        battery.setTag(batteryTag);
    }

    @Override
    public ItemStack getDefaultStack() {
        return super.getDefaultStack();
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public boolean canRepair(ItemStack itemStack_1, ItemStack itemStack_2) {
        return false;
    }
}