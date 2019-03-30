package io.github.teamgalacticraft.galacticraft.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class OxygenTankItem extends Item {

    public OxygenTankItem(Settings settings) {
        super(settings);
        settings.stackSize(1);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void buildTooltip(ItemStack stack, World world, List<TextComponent> lines, TooltipContext context) {
        lines.add(new TranslatableTextComponent("tooltip.galacticraft-fabric.oxygen-remaining", this.getDurability()));
        super.buildTooltip(stack, world, lines, context);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand)); //TODO
    }

}
