package dev.galacticraft.mod.content.block.machine;

import dev.galacticraft.machinelib.api.block.SimpleMachineBlock;
import dev.galacticraft.machinelib.client.api.util.DisplayUtil;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FoodCannerBlock extends SimpleMachineBlock {
    public FoodCannerBlock(Properties settings) {
        super(settings, Constant.id(Constant.Block.FOOD_CANNER));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.addAll(DisplayUtil.wrapText(Component.translatable(this.getDescriptionId() + ".description", CannedFoodItem.MAX_FOOD).withStyle(Constant.Text.GRAY_STYLE), 150));
        } else {
            tooltip.add(PRESS_SHIFT);
        }

        appendBlockEntityTooltip(stack, tooltip);
    }
}
