package io.github.teamgalacticraft.galacticraft.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
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
        System.out.println(stack.getDurability() - stack.getDamage());
        System.out.println(stack.getDurability());
        System.out.println(stack.getDamage());
        if (stack.getDurability() - stack.getDamage() < 34) {
            lines.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.energy-remaining", (stack.getDurability() - stack.getDamage()) * 150).setStyle(new Style().setColor(TextFormat.DARK_RED)));
        } else if (stack.getDurability() - stack.getDamage() < 67) {
            lines.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.energy-remaining", (stack.getDurability() - stack.getDamage()) * 150).setStyle(new Style().setColor(TextFormat.GOLD)));
        } else {
            lines.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.energy-remaining", (stack.getDurability() - stack.getDamage()) * 150).setStyle(new Style().setColor(TextFormat.GREEN)));
        }
        super.buildTooltip(stack, world, lines, context);
    }

}
