package io.github.teamgalacticraft.galacticraft.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class BatteryItem extends Item {

    public BatteryItem(Settings settings) {
        super(settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void buildTooltip(ItemStack stack, World world, List<TextComponent> lines, TooltipContext context) {
        if (stack.getDurability() - stack.getDamage() < 5000) {
            lines.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.energy-remaining", (stack.getDurability() - stack.getDamage())).setStyle(new Style().setColor(TextFormat.DARK_RED)));
        } else if (stack.getDurability() - stack.getDamage() < 10000) {
            lines.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.energy-remaining", (stack.getDurability() - stack.getDamage())).setStyle(new Style().setColor(TextFormat.GOLD)));
        } else {
            lines.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.energy-remaining", (stack.getDurability() - stack.getDamage())).setStyle(new Style().setColor(TextFormat.GREEN)));
        }
        super.buildTooltip(stack, world, lines, context);
    }

    @Override
    public void appendItemsForGroup(ItemGroup group, DefaultedList<ItemStack> groupStacks) {
        if (group != GalacticraftItems.ITEMS_GROUP) {
            return;
        }
        // Adds a full battery and a depleted one
        ItemStack battery_full = new ItemStack(GalacticraftItems.BATTERY);
        ItemStack battery_depleted = new ItemStack(GalacticraftItems.BATTERY);
        battery_depleted.setDamage(15000);
        battery_full.setDamage(0);

        groupStacks.add(battery_full);
        groupStacks.add(battery_depleted);
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