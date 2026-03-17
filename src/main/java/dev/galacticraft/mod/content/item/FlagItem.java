package dev.galacticraft.mod.content.item;

import dev.galacticraft.mod.content.block.entity.decoration.FlagBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlagItem extends BlockItem {
    public FlagItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        BannerItem.appendHoverTextFromBannerBlockEntityTag(stack, tooltip);
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (result.indicateItemUse() && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof FlagBlockEntity flag) {
            flag.setFacingRadians((float) Math.toRadians(context.getPlayer().getYHeadRot()));
        }

        return result;
    }
}