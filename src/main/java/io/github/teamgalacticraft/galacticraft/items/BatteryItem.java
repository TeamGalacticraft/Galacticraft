package io.github.teamgalacticraft.galacticraft.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.*;
import net.minecraft.util.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class BatteryItem extends Item {
    public static final int maxEnergy = 10000;
    public static ItemStack battery_full;
    public static ItemStack battery_depleted;

    public BatteryItem(Settings settings) {
        super(settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void buildTooltip(ItemStack stack, World world, List<TextComponent> lines, TooltipContext context) {
        int charge = stack.getOrCreateTag().getInt("Energy");
        if (stack.getDurability() - stack.getDamage() < 3334) {
            lines.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.energy-remaining", charge).setStyle(new Style().setColor(TextFormat.DARK_RED)));
        } else if (stack.getDurability() - stack.getDamage() < 6667) {
            lines.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.energy-remaining", charge).setStyle(new Style().setColor(TextFormat.GOLD)));
        } else {
            lines.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.energy-remaining", charge).setStyle(new Style().setColor(TextFormat.GREEN)));
        }

        lines.add(new StringTextComponent(stack.getOrCreateTag().toString()));
        super.buildTooltip(stack, world, lines, context);
    }

    @Override
    public void appendItemsForGroup(ItemGroup group, DefaultedList<ItemStack> groupStacks) {
        if (group != GalacticraftItems.ITEMS_GROUP) {
            return;
        }
        groupStacks.add(battery_full);
        groupStacks.add(battery_depleted);
    }

    @Override
    public void onCrafted(ItemStack battery, World world_1, PlayerEntity playerEntity_1) {
        CompoundTag batteryTag = battery.getOrCreateTag();
        batteryTag.putInt("Energy", 0);
        batteryTag.putInt("MaxEnergy", BatteryItem.maxEnergy);
        battery.setDamage(BatteryItem.maxEnergy);
        battery.setTag(batteryTag);
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