package com.hrznstudio.galacticraft.items;

import com.hrznstudio.galacticraft.api.item.EnergyHolderItem;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
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
    public int getMaxEnergy(ItemStack battery) {
        return MAX_ENERGY;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> lines, TooltipContext context) {
        int charge = stack.getOrCreateTag().getInt("Energy");
        if (stack.getMaxDamage() - stack.getDamage() < 3334) {
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.energy-remaining", charge).setStyle(new Style().setColor(Formatting.DARK_RED)));
        } else if (stack.getMaxDamage() - stack.getDamage() < 6667) {
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.energy-remaining", charge).setStyle(new Style().setColor(Formatting.GOLD)));
        } else {
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.energy-remaining", charge).setStyle(new Style().setColor(Formatting.GREEN)));
        }
        super.appendTooltip(stack, world, lines, context);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> groupStacks) {
        if (this.isIn(group)) {
            ItemStack charged = new ItemStack(this);
            GalacticraftEnergy.setEnergy(charged, 0);
            groupStacks.add(charged);

            ItemStack depleted = new ItemStack(this);
            GalacticraftEnergy.setEnergy(depleted, MAX_ENERGY);
            groupStacks.add(depleted);
        }
    }

    @Override
    public void onCraft(ItemStack battery, World world_1, PlayerEntity playerEntity_1) {
        CompoundTag batteryTag = battery.getOrCreateTag();
        batteryTag.putInt("Energy", 0);
        batteryTag.putInt("MaxEnergy", BatteryItem.MAX_ENERGY);
        battery.setDamage(BatteryItem.MAX_ENERGY);
        battery.setTag(batteryTag);
    }

    @Override
    public ItemStack getStackForRender() {
        return super.getStackForRender();
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